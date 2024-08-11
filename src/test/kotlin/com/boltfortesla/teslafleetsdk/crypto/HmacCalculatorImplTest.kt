package com.boltfortesla.teslafleetsdk.crypto

import com.boltfortesla.teslafleetsdk.encoding.HexCodec.decodeHex
import com.boltfortesla.teslafleetsdk.fixtures.Constants.HANDSHAKE_HMAC
import com.boltfortesla.teslafleetsdk.fixtures.Constants.SHARED_SECRET
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HmacCalculatorImplTest {
  private val hmacCalculator = HmacCalculatorImpl()

  @Test
  fun calculateSha256Hmac_calculatesSha256Hmac() {
    val hmac =
      hmacCalculator.calculateSha256Hmac(
        key = SHARED_SECRET.decodeHex(),
        data = "session info".toByteArray()
      )

    assertThat(hmac).isEqualTo(HANDSHAKE_HMAC.decodeHex())
  }
}
