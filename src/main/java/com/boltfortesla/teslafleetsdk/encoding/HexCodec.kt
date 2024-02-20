package com.boltfortesla.teslafleetsdk.encoding

/** Encodes and decodes hexadecimal values to/from [ByteArray] objects. */
object HexCodec {
  /** Decodes a hexadecimal string into a [ByteArray]. */
  fun String.decodeHex(): ByteArray {
    check(length % 2 == 0) { "Must have an even length" }

    return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
  }

  /** Encodes a [ByteArray] into a hexadecimal string representing its content. */
  fun ByteArray.toHexString(): String = this.joinToString("") { "%02x".format(it) }
}
