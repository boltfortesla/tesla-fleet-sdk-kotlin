package com.boltfortesla.teslafleetsdk

import kotlin.random.Random

/** Implementation of [Identifiers]. */
internal class IdentifiersImpl : Identifiers {
  override fun randomUuid() = generateRandomIdentifier()

  override fun randomRoutingAddress() = generateRandomIdentifier()

  private fun generateRandomIdentifier() = Random.nextBytes(16)
}
