package com.boltfortesla.teslafleetsdk.encoding

import com.boltfortesla.teslafleetsdk.encoding.HexCodec.decodeHex
import com.boltfortesla.teslafleetsdk.encoding.HexCodec.toHexString
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class HexCodecTest {
  @Test
  fun decodeHex() {
    assertThat("1b2fce19".decodeHex())
      .isEqualTo(byteArrayOf(0x1b.toByte(), 0x2f.toByte(), 0xce.toByte(), 0x19.toByte()))
  }

  @Test
  fun toHexString() {
    assertThat(
        byteArrayOf(0x1b.toByte(), 0x2f.toByte(), 0xce.toByte(), 0x19.toByte()).toHexString()
      )
      .isEqualTo("1b2fce19")
  }
}
