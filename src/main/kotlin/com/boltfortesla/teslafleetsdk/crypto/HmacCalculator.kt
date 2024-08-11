package com.boltfortesla.teslafleetsdk.crypto

/** Calculates a SHA256HMAC. */
internal interface HmacCalculator {
  /** Returns the SHA256 HMAC value for [key] and [data] . */
  fun calculateSha256Hmac(key: ByteArray, data: ByteArray): ByteArray
}
