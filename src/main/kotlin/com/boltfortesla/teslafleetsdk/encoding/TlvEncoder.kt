package com.boltfortesla.teslafleetsdk.encoding

/** Encodes data in TLV (Type-length-value) format. */
internal interface TlvEncoder {
  /**
   * Returns a [ByteArray] containing the concatenated TLV values of [data], suffixed with [suffix].
   */
  fun encodeTlv(data: Map<Int, ByteArray>, suffix: Byte): ByteArray
}
