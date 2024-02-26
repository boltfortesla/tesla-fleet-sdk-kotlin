package com.boltfortesla.teslafleetsdk.handshake

import com.boltfortesla.teslafleetsdk.crypto.HmacCalculatorImpl
import com.boltfortesla.teslafleetsdk.encoding.HexCodec.decodeHex
import com.boltfortesla.teslafleetsdk.encoding.TlvEncoderImpl
import com.boltfortesla.teslafleetsdk.fixtures.Constants.HANDSHAKE_SESSION_INFO
import com.boltfortesla.teslafleetsdk.fixtures.Constants.HANDSHAKE_SESSION_INFO_TAG
import com.boltfortesla.teslafleetsdk.fixtures.Constants.REQUEST_UUID
import com.boltfortesla.teslafleetsdk.fixtures.Constants.SHARED_SECRET
import com.boltfortesla.teslafleetsdk.fixtures.Constants.VIN
import kotlin.test.assertFailsWith
import org.junit.Test

class SessionInfoAuthenticatorImplTest {
  private val authenticator = SessionInfoAuthenticatorImpl(TlvEncoderImpl(), HmacCalculatorImpl())

  @Test
  fun sessionInfoTagIsValid_isValid_returnsTrue() {
    authenticator.authenticate(
      SHARED_SECRET.decodeHex(),
      VIN,
      REQUEST_UUID.decodeHex(),
      HANDSHAKE_SESSION_INFO.decodeHex(),
      HANDSHAKE_SESSION_INFO_TAG.decodeHex()
    )
  }

  @Test
  fun sessionInfoTagIsValid_isInvalid_returnsFalse() {
    assertFailsWith<SessionInfoAuthenticator.ResponseAuthenticationFailedException> {
      authenticator.authenticate(
        SHARED_SECRET.decodeHex(),
        VIN,
        REQUEST_UUID.decodeHex(),
        HANDSHAKE_SESSION_INFO.decodeHex(),
        "ffffffffffffff".decodeHex()
      )
    }
  }

  @Test
  fun sessionInfoTagIsValid_modifiedData_returnsFalse() {
    assertFailsWith<SessionInfoAuthenticator.ResponseAuthenticationFailedException> {
      authenticator.authenticate(
        SHARED_SECRET.decodeHex(),
        "modified VIN",
        REQUEST_UUID.decodeHex(),
        HANDSHAKE_SESSION_INFO.decodeHex(),
        HANDSHAKE_SESSION_INFO_TAG.decodeHex()
      )
    }
  }
}
