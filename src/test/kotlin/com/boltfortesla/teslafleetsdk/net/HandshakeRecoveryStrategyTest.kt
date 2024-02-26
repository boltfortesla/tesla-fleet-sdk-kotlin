package com.boltfortesla.teslafleetsdk.net

import com.boltfortesla.teslafleetsdk.TeslaFleetApi
import com.boltfortesla.teslafleetsdk.TestKeys
import com.boltfortesla.teslafleetsdk.crypto.HmacCalculatorImpl
import com.boltfortesla.teslafleetsdk.encoding.HexCodec.decodeHex
import com.boltfortesla.teslafleetsdk.encoding.TlvEncoderImpl
import com.boltfortesla.teslafleetsdk.fixtures.Constants.EPOCH
import com.boltfortesla.teslafleetsdk.fixtures.Constants.VIN
import com.boltfortesla.teslafleetsdk.fixtures.Responses
import com.boltfortesla.teslafleetsdk.fixtures.Responses.HANDSHAKE_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.fakes.FakeIdentifiers
import com.boltfortesla.teslafleetsdk.handshake.HandshakerImpl
import com.boltfortesla.teslafleetsdk.handshake.SessionInfo
import com.boltfortesla.teslafleetsdk.handshake.SessionInfoAuthenticatorImpl
import com.boltfortesla.teslafleetsdk.handshake.SessionInfoRepositoryImpl
import com.boltfortesla.teslafleetsdk.keys.Pem
import com.boltfortesla.teslafleetsdk.keys.PublicKeyEncoderImpl
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.createVehicleEndpointsApi
import com.google.common.truth.Truth.assertThat
import com.google.protobuf.ByteString
import com.tesla.generated.signatures.hMACSignatureData
import com.tesla.generated.signatures.signatureData
import com.tesla.generated.universalmessage.UniversalMessage.Domain.DOMAIN_INFOTAINMENT
import com.tesla.generated.universalmessage.copy
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test

class HandshakeRecoveryStrategyTest {
  private val server = MockWebServer()
  private val vehicleEndpointsApi = createVehicleEndpointsApi(server.url("/").toString())
  private val keyParser = PublicKeyEncoderImpl()
  private val fakeIdentifiers = FakeIdentifiers()
  private val sessionInfoRepository = SessionInfoRepositoryImpl()
  private val handshaker =
    HandshakerImpl(
      Pem(TestKeys.CLIENT_PUBLIC_KEY).byteArray(),
      keyParser,
      vehicleEndpointsApi,
      SessionInfoAuthenticatorImpl(TlvEncoderImpl(), HmacCalculatorImpl()),
      fakeIdentifiers,
      NetworkExecutorImpl(TeslaFleetApi.RetryConfig(), JitterFactorCalculatorImpl()),
    )

  @Test
  fun recover_updatesSessionInfoRepositoryWithNewSessionInfo() = runTest {
    val modifiedHandshakeResponse =
      HANDSHAKE_RESPONSE.copy {
        requestUuid = ByteString.copyFromUtf8("otherUuid")
        signatureData = signatureData {
          sessionInfoTag = hMACSignatureData {
            tag =
              ByteString.fromHex("8c1c0cc6526bc9012b4e77c1368bee68d4edd9800eb567eb0bfb6db202adafa6")
          }
        }
      }
    val sharedSecret = "sharedSecret".toByteArray()
    server.enqueue(
      MockResponse()
        .setBody(Responses.signedCommandJson(modifiedHandshakeResponse))
        .setResponseCode(200)
    )

    HandshakeRecoveryStrategy(handshaker, sessionInfoRepository, VIN, DOMAIN_INFOTAINMENT) {
        sharedSecret
      }
      .recover()

    assertThat(sessionInfoRepository.get(VIN, DOMAIN_INFOTAINMENT))
      .isEqualTo(SessionInfo(EPOCH.decodeHex(), 2650, AtomicInteger(6), sharedSecret))
  }
}
