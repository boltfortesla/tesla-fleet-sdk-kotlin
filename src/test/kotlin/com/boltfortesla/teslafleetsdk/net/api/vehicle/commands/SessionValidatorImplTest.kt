package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands

import com.boltfortesla.teslafleetsdk.crypto.HmacCalculatorImpl
import com.boltfortesla.teslafleetsdk.encoding.HexCodec.decodeHex
import com.boltfortesla.teslafleetsdk.encoding.TlvEncoderImpl
import com.boltfortesla.teslafleetsdk.fixtures.Constants.EPOCH
import com.boltfortesla.teslafleetsdk.fixtures.Constants.REQUEST_UUID
import com.boltfortesla.teslafleetsdk.fixtures.Constants.SHARED_SECRET
import com.boltfortesla.teslafleetsdk.fixtures.Constants.VIN
import com.boltfortesla.teslafleetsdk.handshake.SessionInfo
import com.boltfortesla.teslafleetsdk.handshake.SessionInfoAuthenticatorImpl
import com.google.common.truth.Truth.assertThat
import com.google.protobuf.ByteString
import com.tesla.generated.signatures.copy
import com.tesla.generated.signatures.hMACSignatureData
import com.tesla.generated.signatures.sessionInfo
import com.tesla.generated.signatures.signatureData
import com.tesla.generated.universalmessage.copy
import com.tesla.generated.universalmessage.routableMessage
import org.junit.Test

class SessionValidatorImplTest {
  private val validator =
    SessionValidatorImpl(SessionInfoAuthenticatorImpl(TlvEncoderImpl(), HmacCalculatorImpl()))

  @Test
  fun isSessionValid_responseHasNoSessionInfo_isTrue() {
    val result =
      validator.isSessionValid(
        RESPONSE_MESSAGE.copy { clearSessionInfo() },
        REQUEST_UUID.decodeHex(),
        SessionInfo(EPOCH.decodeHex(), CLOCK_TIME, COUNTER, SHARED_SECRET.decodeHex()),
        VIN,
      )

    assertThat(result).isTrue()
  }

  @Test
  fun isSessionValid_responseRequestUuidIsEmpty_otherwiseValid_returnsTrue() {
    val result =
      validator.isSessionValid(
        RESPONSE_MESSAGE.copy { clearRequestUuid() },
        REQUEST_UUID.decodeHex(),
        SessionInfo(EPOCH.decodeHex(), CLOCK_TIME, COUNTER, SHARED_SECRET.decodeHex()),
        VIN,
      )

    assertThat(result).isTrue()
  }

  @Test
  fun isSessionValid_uuidsDoNotMatch_returnsFalse() {
    val result =
      validator.isSessionValid(
        RESPONSE_MESSAGE.copy { requestUuid = ByteString.copyFromUtf8("mismatched UUID") },
        REQUEST_UUID.decodeHex(),
        SessionInfo(EPOCH.decodeHex(), CLOCK_TIME, COUNTER, SHARED_SECRET.decodeHex()),
        VIN,
      )

    assertThat(result).isFalse()
  }

  @Test
  fun isSessionValid_responseClockTimeIsInPast_returnsFalse() {
    val result =
      validator.isSessionValid(
        RESPONSE_MESSAGE.copy {
          sessionInfo = SESSION_INFO.copy { clockTime = CLOCK_TIME - 1 }.toByteString()
        },
        REQUEST_UUID.decodeHex(),
        SessionInfo(EPOCH.decodeHex(), CLOCK_TIME, COUNTER, SHARED_SECRET.decodeHex()),
        VIN,
      )

    assertThat(result).isFalse()
  }

  @Test
  fun isSessionValid_responseFailsToAuthenticate_returnsFalse() {
    val result =
      validator.isSessionValid(
        RESPONSE_MESSAGE.copy { clearSignatureData() },
        REQUEST_UUID.decodeHex(),
        SessionInfo(EPOCH.decodeHex(), CLOCK_TIME, COUNTER, SHARED_SECRET.decodeHex()),
        VIN,
      )

    assertThat(result).isFalse()
  }

  @Test
  fun isSessionValid_isValid_returnsTrue() {
    val result =
      validator.isSessionValid(
        RESPONSE_MESSAGE,
        REQUEST_UUID.decodeHex(),
        SessionInfo(EPOCH.decodeHex(), CLOCK_TIME, COUNTER, SHARED_SECRET.decodeHex()),
        VIN,
      )

    assertThat(result).isTrue()
  }

  private companion object {
    val SESSION_INFO = sessionInfo {
      clockTime = CLOCK_TIME
      counter = COUNTER
      epoch = ByteString.fromHex(EPOCH)
    }

    val RESPONSE_MESSAGE = routableMessage {
      sessionInfo = SESSION_INFO.toByteString()
      signatureData = signatureData {
        sessionInfoTag = hMACSignatureData {
          tag =
            ByteString.fromHex("0a47ca7e1dccac3aebe6f9c86c7e0b225b4a5e4d9552f40165f42ed87e4d16b0")
        }
      }
      requestUuid = ByteString.fromHex(REQUEST_UUID)
    }

    private const val CLOCK_TIME = 1000
    private const val COUNTER = 8
  }
}
