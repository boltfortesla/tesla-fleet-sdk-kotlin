package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands

import com.boltfortesla.teslafleetsdk.TeslaFleetApi
import com.boltfortesla.teslafleetsdk.TeslaFleetApi.RetryConfig
import com.boltfortesla.teslafleetsdk.TestKeys
import com.boltfortesla.teslafleetsdk.commands.CommandSignerImpl
import com.boltfortesla.teslafleetsdk.commands.HmacCommandAuthenticator
import com.boltfortesla.teslafleetsdk.crypto.HmacCalculatorImpl
import com.boltfortesla.teslafleetsdk.encoding.HexCodec.decodeHex
import com.boltfortesla.teslafleetsdk.encoding.TlvEncoderImpl
import com.boltfortesla.teslafleetsdk.fixtures.Constants
import com.boltfortesla.teslafleetsdk.fixtures.Responses
import com.boltfortesla.teslafleetsdk.fixtures.Responses.signedCommandJson
import com.boltfortesla.teslafleetsdk.fixtures.fakes.FakeIdentifiers
import com.boltfortesla.teslafleetsdk.handshake.HandshakerImpl
import com.boltfortesla.teslafleetsdk.handshake.SessionInfo
import com.boltfortesla.teslafleetsdk.handshake.SessionInfoAuthenticatorImpl
import com.boltfortesla.teslafleetsdk.handshake.SessionInfoRepositoryImpl
import com.boltfortesla.teslafleetsdk.keys.Pem
import com.boltfortesla.teslafleetsdk.keys.PublicKeyEncoderImpl
import com.boltfortesla.teslafleetsdk.net.JitterFactorCalculatorImpl
import com.boltfortesla.teslafleetsdk.net.NetworkExecutor.Companion.HTTP_TOO_MANY_REQUESTS
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
import com.tesla.generated.universalmessage.UniversalMessage.Domain
import com.tesla.generated.universalmessage.copy
import com.tesla.generated.vcsec.Vcsec
import com.tesla.generated.vcsec.Vcsec.RKEAction_E
import com.tesla.generated.vcsec.unsignedMessage
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test
import retrofit2.HttpException

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
      fakeIdentifiers,
      commandExpiration = 15.seconds,
    )
  private val sessionInfo =
    SessionInfo(
      "epoch".toByteArray(),
      clockTime = 0,
      counter = 0,
      Constants.SHARED_SECRET.decodeHex(),
    )
  private val sharedSecretFetcher =
    TeslaFleetApi.SharedSecretFetcher { Constants.SHARED_SECRET.decodeHex() }
  private val sessionInfoRepository = SessionInfoRepositoryImpl()
  private val sessionInfoAuthenticator =
    SessionInfoAuthenticatorImpl(TlvEncoderImpl(), HmacCalculatorImpl())
  private val handshaker =
    HandshakerImpl(
      Pem(TestKeys.CLIENT_PUBLIC_KEY).byteArray(),
      PublicKeyEncoderImpl(),
      vehicleEndpointsApi,
      sessionInfoAuthenticator,
      fakeIdentifiers,
      NetworkExecutorImpl(RetryConfig(), JitterFactorCalculatorImpl()),
    )

  @Test
  fun execute_success_infotainment_returnsSuccessResponse() = runTest {
    sessionInfoRepository.set(Constants.VIN, Domain.DOMAIN_INFOTAINMENT, sessionInfo)
    server.enqueue(
      MockResponse()
        .setResponseCode(200)
        .setBody(signedCommandJson(Responses.INFOTAINMENT_COMMAND_RESPONSE))
    )

    val response =
      createSignedCommandSender()
        .signAndSend(
          Domain.DOMAIN_INFOTAINMENT,
          ACTION,
          TestKeys.CLIENT_PUBLIC_KEY_BYTES,
          sharedSecretFetcher,
        )

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
    sessionInfoRepository.set(Constants.VIN, Domain.DOMAIN_VEHICLE_SECURITY, sessionInfo)
    server.enqueue(
      MockResponse()
        .setResponseCode(200)
        .setBody(signedCommandJson(Responses.SECURITY_COMMAND_RESPONSE))
    )

    val response =
      createSignedCommandSender()
        .signAndSend(
          Domain.DOMAIN_VEHICLE_SECURITY,
          UNSIGNED_MESSAGE,
          TestKeys.CLIENT_PUBLIC_KEY_BYTES,
          sharedSecretFetcher,
        )

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
    assertThat(sessionInfoRepository.get(Constants.VIN, Domain.DOMAIN_VEHICLE_SECURITY)).isNotNull()
  }

  @Test
  fun execute_noSession_throwsIllegalStateException() = runTest {
    val result =
      createSignedCommandSender(RetryConfig(maxRetries = 0))
        .signAndSend(
          Domain.DOMAIN_INFOTAINMENT,
          UNSIGNED_MESSAGE,
          TestKeys.CLIENT_PUBLIC_KEY_BYTES,
          sharedSecretFetcher,
        )

    val exception = result.exceptionOrNull()!!
    assertThat(exception).isInstanceOf(IllegalStateException::class.java)
    assertThat(exception.message).isEqualTo("No session found")
  }

  @Test
  fun fails_nonRetryableSignedMessageFault_doesNotRetry() = runTest {
    sessionInfoRepository.set(Constants.VIN, Domain.DOMAIN_INFOTAINMENT, sessionInfo)
    server.enqueue(
      MockResponse()
        .setResponseCode(200)
        .setBody(signedCommandJson(Responses.SIGNED_MESSAGE_FAULT_RESPONSE))
    )

    val response =
      createSignedCommandSender()
        .signAndSend(
          Domain.DOMAIN_INFOTAINMENT,
          ACTION,
          TestKeys.CLIENT_PUBLIC_KEY_BYTES,
          sharedSecretFetcher,
        )

    assertThat(server.requestCount).isEqualTo(1)
    assertThat(response.isFailure).isTrue()
    val exception = response.exceptionOrNull() as SignedMessagesFaultException
    assertThat(exception.fault)
      .isEqualTo(UniversalMessage.MessageFault_E.MESSAGEFAULT_ERROR_BAD_PARAMETER)
  }

  @Test
  fun fails_retryableSignedMessageFault_validSession_updatesCounterFromResponseAndRetries() =
    runTest {
      sessionInfoRepository.set(Constants.VIN, Domain.DOMAIN_INFOTAINMENT, sessionInfo)
      server.enqueue(
        MockResponse()
          .setResponseCode(200)
          .setBody(signedCommandJson(Responses.RETRYABLE_SIGNED_MESSAGE_FAULT_RESPONSE))
      )
      server.enqueue(
        MockResponse()
          .setResponseCode(200)
          .setBody(signedCommandJson(Responses.RETRYABLE_SIGNED_MESSAGE_FAULT_RESPONSE))
      )

      val response =
        createSignedCommandSender(RetryConfig(maxRetries = 1))
          .signAndSend(
            Domain.DOMAIN_INFOTAINMENT,
            ACTION,
            TestKeys.CLIENT_PUBLIC_KEY_BYTES,
            sharedSecretFetcher,
          )

      assertThat(server.requestCount).isEqualTo(2)
      val updatedSessionInfo =
        sessionInfoRepository.get(Constants.VIN, Domain.DOMAIN_INFOTAINMENT)!!
      // After the first failure, the request counter is 1, and the response is 6. New counter = 7
      // After the second failure, the request counter is 8 (incremented before sending), and the
      // response is 6. New counter = 9
      assertThat(updatedSessionInfo.counter).isEqualTo(9)
      assertThat(updatedSessionInfo.clockTime).isEqualTo(3000)
      assertThat(response.isFailure).isTrue()
      val exception = response.exceptionOrNull() as SignedMessagesFaultException
      assertThat(exception.fault).isEqualTo(UniversalMessage.MessageFault_E.MESSAGEFAULT_ERROR_BUSY)
    }

  @Test
  fun fails_retryableSignedMessageFault_mismatchedEpoch_updatesCounterFromResponse() = runTest {
    sessionInfoRepository.set(
      Constants.VIN,
      Domain.DOMAIN_INFOTAINMENT,
      sessionInfo.copy(epoch = "otherEpoch".toByteArray()),
    )
    server.enqueue(
      MockResponse()
        .setResponseCode(200)
        .setBody(signedCommandJson(Responses.RETRYABLE_SIGNED_MESSAGE_FAULT_RESPONSE))
    )

    val response =
      createSignedCommandSender(RetryConfig(maxRetries = 0))
        .signAndSend(
          Domain.DOMAIN_INFOTAINMENT,
          ACTION,
          TestKeys.CLIENT_PUBLIC_KEY_BYTES,
          sharedSecretFetcher,
        )

    assertThat(server.requestCount).isEqualTo(1)
    val updatedSessionInfo = sessionInfoRepository.get(Constants.VIN, Domain.DOMAIN_INFOTAINMENT)!!
    assertThat(updatedSessionInfo.counter).isEqualTo(6)
    assertThat(updatedSessionInfo.clockTime).isEqualTo(3000)
    assertThat(response.isFailure).isTrue()
    val exception = response.exceptionOrNull() as SignedMessagesFaultException
    assertThat(exception.fault).isEqualTo(UniversalMessage.MessageFault_E.MESSAGEFAULT_ERROR_BUSY)
  }

  @Test
  fun fails_retryableSignedMessageFault_invalidSession_newHandshakePerformed() = runTest {
    sessionInfoRepository.set(
      Constants.VIN,
      Domain.DOMAIN_INFOTAINMENT,
      SessionInfo(
        "otherEpoch".toByteArray(),
        clockTime = 1,
        counter = 2,
        "otherSharedSecret".toByteArray(),
      ),
    )
    server.enqueue(
      MockResponse()
        .setResponseCode(200)
        .setBody(
          signedCommandJson(
            Responses.RETRYABLE_SIGNED_MESSAGE_FAULT_RESPONSE.copy { clearSignatureData() }
          )
        )
    )
    server.enqueue(
      MockResponse().setBody(signedCommandJson(Responses.HANDSHAKE_RESPONSE)).setResponseCode(200)
    )
    server.enqueue(
      MockResponse()
        .setResponseCode(200)
        .setBody(signedCommandJson(Responses.RETRYABLE_SIGNED_MESSAGE_FAULT_RESPONSE))
    )

    val response =
      createSignedCommandSender(RetryConfig(maxRetries = 1))
        .signAndSend(
          Domain.DOMAIN_INFOTAINMENT,
          ACTION,
          TestKeys.CLIENT_PUBLIC_KEY_BYTES,
          sharedSecretFetcher,
        )

    assertThat(server.requestCount).isEqualTo(3)
    assertThat(sessionInfoRepository.get(Constants.VIN, Domain.DOMAIN_INFOTAINMENT)!!.counter)
      .isEqualTo(6)
    assertThat(response.isFailure).isTrue()
    val exception = response.exceptionOrNull() as SignedMessagesFaultException
    assertThat(exception.fault).isEqualTo(UniversalMessage.MessageFault_E.MESSAGEFAULT_ERROR_BUSY)
  }

  @Test
  fun success_infotainment_operationFailure_doesNotRetry() = runTest {
    sessionInfoRepository.set(Constants.VIN, Domain.DOMAIN_INFOTAINMENT, sessionInfo)
    repeat(2) {
      server.enqueue(
        MockResponse()
          .setResponseCode(200)
          .setBody(signedCommandJson(Responses.INFOTAINMENT_OPERATION_FAILURE_RESPONSE))
      )
    }

    val response =
      createSignedCommandSender(RetryConfig(maxRetries = 1))
        .signAndSend(
          Domain.DOMAIN_INFOTAINMENT,
          ACTION,
          TestKeys.CLIENT_PUBLIC_KEY_BYTES,
          sharedSecretFetcher,
        )

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
    sessionInfoRepository.set(Constants.VIN, Domain.DOMAIN_VEHICLE_SECURITY, sessionInfo)
    server.enqueue(
      MockResponse()
        .setResponseCode(200)
        .setBody(signedCommandJson(Responses.SECURITY_OPERATION_FAILURE_RESPONSE))
    )

    val response =
      createSignedCommandSender(RetryConfig(maxRetries = 0))
        .signAndSend(
          Domain.DOMAIN_VEHICLE_SECURITY,
          UNSIGNED_MESSAGE,
          TestKeys.CLIENT_PUBLIC_KEY_BYTES,
          sharedSecretFetcher,
        )

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

  @Test
  fun fails_tooManyRequests_retryAfterHeaderOverMaxRetryAfter_doesNotRetry() = runTest {
    sessionInfoRepository.set(Constants.VIN, Domain.DOMAIN_INFOTAINMENT, sessionInfo)
    server.enqueue(
      MockResponse().setResponseCode(HTTP_TOO_MANY_REQUESTS).setHeader("retry-after", 10)
    )

    val response =
      createSignedCommandSender(RetryConfig(maxRetryAfter = 1.seconds))
        .signAndSend(
          Domain.DOMAIN_INFOTAINMENT,
          ACTION,
          TestKeys.CLIENT_PUBLIC_KEY_BYTES,
          sharedSecretFetcher,
        )

    assertThat(server.requestCount).isEqualTo(1)
    assertThat(response.isFailure).isTrue()
    val exception = response.exceptionOrNull() as HttpException
    assertThat(exception).isInstanceOf(UnrecoverableHttpException::class.java)
    assertThat(exception.code()).isEqualTo(HTTP_TOO_MANY_REQUESTS)
  }

  @Test
  fun fails_tooManyRequests_retryAfterHeaderUnderMaxRetryAfter_retries() = runTest {
    sessionInfoRepository.set(Constants.VIN, Domain.DOMAIN_INFOTAINMENT, sessionInfo)
    repeat(5) {
      server.enqueue(
        MockResponse().setResponseCode(HTTP_TOO_MANY_REQUESTS).setHeader("retry-after", 10)
      )
    }

    val response =
      createSignedCommandSender(RetryConfig(maxRetries = 4, maxRetryAfter = 15.seconds))
        .signAndSend(
          Domain.DOMAIN_INFOTAINMENT,
          ACTION,
          TestKeys.CLIENT_PUBLIC_KEY_BYTES,
          sharedSecretFetcher,
        )

    assertThat(server.requestCount).isEqualTo(5)
    assertThat(response.isFailure).isTrue()
    val exception = response.exceptionOrNull() as HttpException
    assertThat(exception).isInstanceOf(UnrecoverableHttpException::class.java)
    assertThat(exception.code()).isEqualTo(HTTP_TOO_MANY_REQUESTS)
  }

  private fun createSignedCommandSender(
    retryConfig: RetryConfig = RetryConfig()
  ): SignedCommandSender {
    val networkExecutor = NetworkExecutorImpl(retryConfig, jitterFactorCalculator)
    return SignedCommandSenderImpl(
      commandSigner,
      VehicleEndpointsImpl(Constants.VIN, vehicleEndpointsApi, networkExecutor),
      networkExecutor,
      SessionValidatorImpl(sessionInfoAuthenticator),
      sessionInfoRepository,
      handshaker,
      Constants.VIN,
    )
  }

  companion object {
    private val ACTION =
      action {
          vehicleAction = vehicleAction {
            vehicleControlFlashLightsAction = vehicleControlFlashLightsAction {}
          }
        }
        .toByteString()

    private val UNSIGNED_MESSAGE =
      unsignedMessage { rKEAction = RKEAction_E.RKE_ACTION_REMOTE_DRIVE }.toByteString()
  }
}
