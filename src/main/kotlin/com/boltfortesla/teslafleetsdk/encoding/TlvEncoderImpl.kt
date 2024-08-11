package com.boltfortesla.teslafleetsdk.encoding

/** Implementation of [TlvEncoder] */
internal class TlvEncoderImpl : TlvEncoder {

  override fun encodeTlv(data: Map<Int, ByteArray>, suffix: Byte): ByteArray {
    return data.entries.fold(initial = byteArrayOf()) { value: ByteArray, entry ->
      value + encodeTlv(entry.key, entry.value)
    } + byteArrayOf(suffix)
  }

  private fun encodeTlv(tag: Int, value: ByteArray): ByteArray {
    return byteArrayOf(tag.toByte(), value.size.toByte()) + value
  }
}
