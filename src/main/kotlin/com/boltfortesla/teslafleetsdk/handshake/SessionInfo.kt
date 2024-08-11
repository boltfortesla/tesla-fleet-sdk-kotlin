package com.boltfortesla.teslafleetsdk.handshake

import java.io.Serializable

/**
 * Information needed for sending signed commands for a specific session.
 *
 * @param epoch a random 16-byte value generated at vehicle boot
 * @param clockTime the number of seconds since the start of the [epoch]
 * @param counter a monotonically increasing counter. Must be incremented for every command sent
 * @param sharedSecret the generated HMAC shared secret for this session, used to sign commands.
 *   This is sensitive information and should be handled like a raw credential
 */
internal data class SessionInfo(
  val epoch: ByteArray,
  val clockTime: Int,
  val counter: Int,
  val sharedSecret: ByteArray,
) : Serializable {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as SessionInfo

    if (!epoch.contentEquals(other.epoch)) return false
    if (clockTime != other.clockTime) return false
    if (counter != other.counter) return false
    return sharedSecret.contentEquals(other.sharedSecret)
  }

  override fun hashCode(): Int {
    var result = epoch.contentHashCode()
    result = 31 * result + clockTime
    result = 31 * result + counter
    result = 31 * result + sharedSecret.contentHashCode()
    return result
  }
}
