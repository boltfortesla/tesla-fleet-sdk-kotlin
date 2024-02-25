package com.boltfortesla.teslafleetsdk.net

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.SharedSecretFetcher
import com.boltfortesla.teslafleetsdk.handshake.Handshaker
import com.boltfortesla.teslafleetsdk.handshake.SessionInfoRepository
import com.tesla.generated.universalmessage.UniversalMessage.Domain

/** [MessageFaultRecoveryStrategy] that performs a new Handshake */
internal class HandshakeRecoveryStrategy(
  private val handshaker: Handshaker,
  private val sessionInfoRepository: SessionInfoRepository,
  private val vin: String,
  private val domain: Domain,
  private val sharedSecretFetcher: SharedSecretFetcher,
) : MessageFaultRecoveryStrategy {
  override suspend fun recover() {
    sessionInfoRepository.set(
      vin,
      domain,
      handshaker.performHandshake(vin, domain, sharedSecretFetcher)
    )
  }
}
