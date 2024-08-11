package com.boltfortesla.teslafleetsdk.keys

/** Encodes an EC public key to its uncompressed curve point. */
internal interface PublicKeyEncoder {
  /** Returns the encoded version of the public key in [publicKeyBytes]. */
  fun encodedPublicKey(publicKeyBytes: ByteArray): ByteArray
}
