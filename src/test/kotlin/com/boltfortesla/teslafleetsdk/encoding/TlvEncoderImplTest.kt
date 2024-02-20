package com.boltfortesla.teslafleetsdk.encoding

import com.boltfortesla.teslafleetsdk.encoding.HexCodec.decodeHex
import com.boltfortesla.teslafleetsdk.fixtures.Constants.HANDSHAKE_REQUEST_UUID
import com.boltfortesla.teslafleetsdk.fixtures.Constants.HANDSHAKE_TLV
import com.boltfortesla.teslafleetsdk.fixtures.Constants.VIN
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class TlvEncoderImplTest {
  private val encoder = TlvEncoderImpl()

  @Test
  fun encodeTlv_encodesTlvMap() {
    val tlv =
      encoder.encodeTlv(
        mapOf(
          0 to byteArrayOf(6.toByte()),
          2 to VIN.toByteArray(),
          6 to HANDSHAKE_REQUEST_UUID.decodeHex()
        ),
        0xff.toByte()
      )

    assertThat(tlv).isEqualTo(HANDSHAKE_TLV.decodeHex())
  }
}
