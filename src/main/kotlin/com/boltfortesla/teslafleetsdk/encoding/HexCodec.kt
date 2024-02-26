package com.boltfortesla.teslafleetsdk.encoding

import org.bouncycastle.util.encoders.Hex

/** Encodes and decodes hexadecimal values to/from [ByteArray] objects. */
object HexCodec {
  /** Decodes a hexadecimal string into a [ByteArray]. */
  fun String.decodeHex(): ByteArray = Hex.decode(this)

  /** Encodes a [ByteArray] into a hexadecimal string representing its content. */
  fun ByteArray.toHexString(): String = Hex.toHexString(this)
}
