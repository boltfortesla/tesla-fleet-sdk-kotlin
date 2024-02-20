package com.boltfortesla.teslafleetsdk.keys

import java.util.Base64

/** Representation of a key in PEM format. */
@JvmInline
value class Pem(private val pemString: String) {
  /** Extracts the base64 encoding of the key. */
  fun base64(): String {
    return pemString.replace(Regex("^.*\n|\n-+END .* KEY-+$|\n"), "")
  }

  /** Converts the key into a [ByteArray]. */
  fun byteArray(): ByteArray {
    return Base64.getDecoder().decode(base64())
  }
}
