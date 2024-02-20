package com.boltfortesla.teslafleetsdk

/** Generates random identifiers, used for UUIds and Routing Addresses. */
interface Identifiers {
  /** Generates a [ByteArray] to use as a UUID. */
  fun randomUuid(): ByteArray

  /** Generates a [ByteArray] to use as a Routing Address. */
  fun randomRoutingAddress(): ByteArray
}
