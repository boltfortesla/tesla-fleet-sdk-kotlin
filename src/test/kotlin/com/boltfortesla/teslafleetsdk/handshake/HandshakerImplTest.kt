package com.boltfortesla.teslafleetsdk.handshake

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.RetryConfig
import com.boltfortesla.teslafleetsdk.TestKeys
import com.boltfortesla.teslafleetsdk.crypto.HmacCalculatorImpl
import com.boltfortesla.teslafleetsdk.encoding.HexCodec.decodeHex
import com.boltfortesla.teslafleetsdk.encoding.TlvEncoderImpl
import com.boltfortesla.teslafleetsdk.fixtures.Constants.EPOCH
import com.boltfortesla.teslafleetsdk.fixtures.Constants.SHARED_SECRET
import com.boltfortesla.teslafleetsdk.fixtures.Constants.VIN
import com.boltfortesla.teslafleetsdk.fixtures.Responses
import com.boltfortesla.teslafleetsdk.fixtures.Responses.HANDSHAKE_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.signedCommandJson
import com.boltfortesla.teslafleetsdk.fixtures.fakes.FakeIdentifiers
import com.boltfortesla.teslafleetsdk.handshake.SessionInfoAuthenticator.ResponseAuthenticationFailedException
import com.boltfortesla.teslafleetsdk.keys.Pem
import com.boltfortesla.teslafleetsdk.keys.PublicKeyEncoderImpl
import com.boltfortesla.teslafleetsdk.net.JitterFactorCalculatorImpl
import com.boltfortesla.teslafleetsdk.net.NetworkExecutorImpl
import com.boltfortesla.teslafleetsdk.net.SignedMessagesFaultException
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.createVehicleEndpointsApi
import com.google.common.truth.Truth.assertThat
import com.google.protobuf.ByteString
import com.google.protobuf.kotlin.toByteString
import com.tesla.generated.signatures.hMACSignatureData
import com.tesla.generated.signatures.signatureData
import com.tesla.generated.universalmessage.UniversalMessage
import com.tesla.generated.universalmessage.UniversalMessage.Domain
import com.tesla.generated.universalmessage.UniversalMessage.RoutableMessage
import com.tesla.generated.universalmessage.copy
import com.tesla.generated.universalmessage.destination
import com.tesla.generated.universalmessage.routableMessage
import com.tesla.generated.universalmessage.sessionInfoRequest
import java.util.Base64
import kotlin.test.assertFailsWith
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONObject
import org.junit.After
import org.junit.Test

class HandshakerImplTest {
  private val server = MockWebServer()
  private val vehicleEndpointsApi = createVehicleEndpointsApi(server.url("/").toString())
  private val keyParser = PublicKeyEncoderImpl()
  private val fakeIdentifiers = FakeIdentifiers()
  private val handshaker =
    HandshakerImpl(
      Pem(TestKeys.CLIENT_PUBLIC_KEY).byteArray(),
      keyParser,
      vehicleEndpointsApi,
      SessionInfoAuthenticatorImpl(TlvEncoderImpl(), HmacCalculatorImpl()),
      fakeIdentifiers,
      NetworkExecutorImpl(RetryConfig(), JitterFactorCalculatorImpl())
    )

  @After
  fun tearDown() {
    server.shutdown()
  }

  @Test
  fun performHandshake_returnsCommandAuthorizationData() = runTest {
    server.enqueue(
      MockResponse().setBody(signedCommandJson(HANDSHAKE_RESPONSE)).setResponseCode(200)
    )
    val commandAuthData =
      handshaker.performHandshake(VIN, Domain.DOMAIN_INFOTAINMENT) { SHARED_SECRET.decodeHex() }

    val request = JSONObject(server.takeRequest().body.readUtf8())
    val routableMessage =
      RoutableMessage.parseFrom(
        Base64.getDecoder().decode(request.get("routable_message") as String)
      )

    assertThat(routableMessage)
      .isEqualTo(
        routableMessage {
          toDestination = destination { domain = Domain.DOMAIN_INFOTAINMENT }
          fromDestination = destination {
            routingAddress = ByteString.copyFrom(fakeIdentifiers.routingAddress)
          }
          sessionInfoRequest = sessionInfoRequest {
            publicKey =
              ByteString.copyFrom(
                keyParser.encodedPublicKey(Pem(TestKeys.CLIENT_PUBLIC_KEY).byteArray())
              )
          }
          uuid = ByteString.copyFrom(fakeIdentifiers.uuid)
        }
      )
    assertThat(commandAuthData)
      .isEqualTo(SessionInfo(EPOCH.decodeHex(), 2650, 6, SHARED_SECRET.decodeHex()))
  }

  @Test
  fun performHandshake_modifiedResponse_rejectsHandshake() = runTest {
    val response =
      HANDSHAKE_RESPONSE.copy {
        signatureData = signatureData {
          sessionInfoTag = hMACSignatureData { tag = "ffffffff".decodeHex().toByteString() }
        }
      }

    server.enqueue(MockResponse().setBody(signedCommandJson(response)).setResponseCode(200))

    assertFailsWith<ResponseAuthenticationFailedException> {
      handshaker.performHandshake(VIN, Domain.DOMAIN_INFOTAINMENT) { SHARED_SECRET.decodeHex() }
    }
  }

  @Test
  fun performHandshake_signedMessageFault_failsImmediately() = runTest {
    server.enqueue(
      MockResponse()
        .setResponseCode(200)
        .setBody(signedCommandJson(Responses.SIGNED_MESSAGE_FAULT_RESPONSE))
    )

    val exception =
      assertFailsWith<SignedMessagesFaultException> {
        handshaker.performHandshake(VIN, Domain.DOMAIN_INFOTAINMENT) { SHARED_SECRET.decodeHex() }
      }
    assertThat(exception.fault)
      .isEqualTo(UniversalMessage.MessageFault_E.MESSAGEFAULT_ERROR_BAD_PARAMETER)
  }
}
