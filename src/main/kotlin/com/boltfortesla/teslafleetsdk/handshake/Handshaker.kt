package com.boltfortesla.teslafleetsdk.handshake

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.SharedSecretFetcher
import com.tesla.generated.universalmessage.UniversalMessage.Domain

/** Handles Tesla Fleet API Handshakes */
internal interface Handshaker {
  /**
   * Performs a Handshake with the Tesla Fleet API for the vehicle identified by [vin], using
   * [sharedSecretFetcher] for response authentication purposes
   */
  suspend fun performHandshake(
    vin: String,
    domain: Domain,
    sharedSecretFetcher: SharedSecretFetcher
  ): SessionInfo
}
