package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.RetryConfig
import com.boltfortesla.teslafleetsdk.TestKeys
import com.boltfortesla.teslafleetsdk.commands.CommandSignerImpl
import com.boltfortesla.teslafleetsdk.commands.HmacCommandAuthenticator
import com.boltfortesla.teslafleetsdk.crypto.HmacCalculatorImpl
import com.boltfortesla.teslafleetsdk.encoding.TlvEncoderImpl
import com.boltfortesla.teslafleetsdk.fixtures.Constants
import com.boltfortesla.teslafleetsdk.fixtures.Responses
import com.boltfortesla.teslafleetsdk.fixtures.Responses.signedCommandJson
import com.boltfortesla.teslafleetsdk.fixtures.fakes.FakeIdentifiers
import com.boltfortesla.teslafleetsdk.handshake.SessionInfo
import com.boltfortesla.teslafleetsdk.keys.PublicKeyEncoderImpl
import com.boltfortesla.teslafleetsdk.net.JitterFactorCalculatorImpl
import com.boltfortesla.teslafleetsdk.net.NetworkExecutorImpl
import com.boltfortesla.teslafleetsdk.net.SignedMessagesFaultException
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.response.VehicleCommandResponse.CommandProtocolResponse.InfotainmentResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.response.VehicleCommandResponse.CommandProtocolResponse.VehicleSecurityResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.VehicleEndpointsImpl
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.createVehicleEndpointsApi
import com.google.common.truth.Truth.assertThat
import com.tesla.generated.carserver.server.CarServer
import com.tesla.generated.carserver.server.action
import com.tesla.generated.carserver.server.vehicleAction
import com.tesla.generated.carserver.server.vehicleControlFlashLightsAction
import com.tesla.generated.universalmessage.UniversalMessage
import com.tesla.generated.vcsec.Vcsec
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test

class SignedCommandSenderImplTest {
  private val server = MockWebServer()
  private val fakeIdentifiers = FakeIdentifiers()
  private val vehicleEndpointsApi = createVehicleEndpointsApi(server.url("/").toString())
  private val publicKeyEncoder = PublicKeyEncoderImpl()
  private val tlvEncoder = TlvEncoderImpl()
  private val hmacCalculator = HmacCalculatorImpl()
  private val jitterFactorCalculator = JitterFactorCalculatorImpl()
  private val commandSigner =
    CommandSignerImpl(
      HmacCommandAuthenticator(hmacCalculator),
      tlvEncoder,
      publicKeyEncoder,
      fakeIdentifiers
    )
  private val sessionInfo =
    SessionInfo(
      "epoch".toByteArray(),
      clockTime = 0,
      counter = 0,
      "sharedSecret".toByteArray()
    )

  @Test
  fun execute_success_infotainment_returnsSuccessResponse() = runTest {
    server.enqueue(
      MockResponse()
        .setResponseCode(200)
        .setBody(signedCommandJson(Responses.INFOTAINMENT_COMMAND_RESPONSE))
    )

    val response =
      createSignedCommandSender().signAndSend(ACTION, sessionInfo, TestKeys.CLIENT_PUBLIC_KEY_BYTES)

    assertThat(response)
      .isEqualTo(
        Result.success(
          InfotainmentResponse(
            CarServer.Response.parseFrom(
              Responses.INFOTAINMENT_COMMAND_RESPONSE.protobufMessageAsBytes
            )
          )
        )
      )
  }

  @Test
  fun execute_success_security_returnsSuccessResponse() = runTest {
    server.enqueue(
      MockResponse()
        .setResponseCode(200)
        .setBody(signedCommandJson(Responses.SECURITY_COMMAND_RESPONSE))
    )

    val response =
      createSignedCommandSender().signAndSend(ACTION, sessionInfo, TestKeys.CLIENT_PUBLIC_KEY_BYTES)

    assertThat(response)
      .isEqualTo(
        Result.success(
          VehicleSecurityResponse(
            Vcsec.FromVCSECMessage.parseFrom(
              Responses.SECURITY_COMMAND_RESPONSE.protobufMessageAsBytes
            )
          )
        )
      )
  }

  @Test
  fun fails_signedMessageFault_doesNotRetry() = runTest {
    server.enqueue(
      MockResponse()
        .setResponseCode(200)
        .setBody(signedCommandJson(Responses.SIGNED_MESSAGE_FAULT_RESPONSE))
    )

    val response =
      createSignedCommandSender().signAndSend(ACTION, sessionInfo, TestKeys.CLIENT_PUBLIC_KEY_BYTES)

    assertThat(server.requestCount).isEqualTo(1)
    assertThat(response.isFailure).isTrue()
    val exception = response.exceptionOrNull() as SignedMessagesFaultException
    assertThat(exception.fault)
      .isEqualTo(UniversalMessage.MessageFault_E.MESSAGEFAULT_ERROR_BAD_PARAMETER)
  }

  @Test
  fun success_infotainment_operationFailure_doesNotRetry() = runTest {
    repeat(2) {
      server.enqueue(
        MockResponse()
          .setResponseCode(200)
          .setBody(signedCommandJson(Responses.INFOTAINMENT_OPERATION_FAILURE_RESPONSE))
      )
    }

    val response =
      createSignedCommandSender(RetryConfig(maxRetries = 1))
        .signAndSend(ACTION, sessionInfo, TestKeys.CLIENT_PUBLIC_KEY_BYTES)

    assertThat(response)
      .isEqualTo(
        Result.failure<InfotainmentResponse>(
          ErrorResultException.InfotainmentFailureException(
            CarServer.Response.parseFrom(
              Responses.INFOTAINMENT_OPERATION_FAILURE_RESPONSE.protobufMessageAsBytes
            )
          )
        )
      )
    assertThat(server.requestCount).isEqualTo(1)
  }

  @Test
  fun success_security_operationFailure_returnsFailureResponse() = runTest {
    server.enqueue(
      MockResponse()
        .setResponseCode(200)
        .setBody(signedCommandJson(Responses.SECURITY_OPERATION_FAILURE_RESPONSE))
    )

    val response =
      createSignedCommandSender(RetryConfig(maxRetries = 0))
        .signAndSend(ACTION, sessionInfo, TestKeys.CLIENT_PUBLIC_KEY_BYTES)

    assertThat(response)
      .isEqualTo(
        Result.failure<VehicleSecurityResponse>(
          ErrorResultException.VehicleSecurityFailureException(
            Vcsec.FromVCSECMessage.parseFrom(
              Responses.SECURITY_OPERATION_FAILURE_RESPONSE.protobufMessageAsBytes
            )
          )
        )
      )
    assertThat(server.requestCount).isEqualTo(1)
  }

  private fun createSignedCommandSender(
    retryConfig: RetryConfig = RetryConfig()
  ): SignedCommandSender {
    val networkExecutor = NetworkExecutorImpl(retryConfig, jitterFactorCalculator)
    return SignedCommandSenderImpl(
      commandSigner,
      VehicleEndpointsImpl(Constants.VIN, vehicleEndpointsApi, networkExecutor),
      networkExecutor,
      Constants.VIN
    )
  }

  companion object {
    private val ACTION = action {
      vehicleAction = vehicleAction {
        vehicleControlFlashLightsAction = vehicleControlFlashLightsAction {}
      }
    }
  }
}
