package com.boltfortesla.teslafleetsdk.net

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.SharedSecretFetcher
import com.boltfortesla.teslafleetsdk.handshake.Handshaker
import com.boltfortesla.teslafleetsdk.handshake.SessionInfoRepository
import com.tesla.generated.universalmessage.UniversalMessage

/** Factory for [HandshakeRecoveryStrategy] */
internal class HandshakeRecoveryStrategyFactory(
  private val handshaker: Handshaker,
  private val sessionInfoRepository: SessionInfoRepository
) {
  /**
   * @param vin VIN of the vehicle the command will be sent to
   * @param domain the [Domain] the handshake should be performed for.
   * @param sharedSecretFetcher an implementation of [SharedSecretFetcher]
   */
  fun create(
    vin: String,
    domain: UniversalMessage.Domain,
    sharedSecretFetcher: SharedSecretFetcher
  ) = HandshakeRecoveryStrategy(handshaker, sessionInfoRepository, vin, domain, sharedSecretFetcher)
}
