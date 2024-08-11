package com.boltfortesla.teslafleetsdk.handshake

import com.boltfortesla.teslafleetsdk.handshake.SessionInfoRepository.SessionInfoKey
import com.tesla.generated.universalmessage.UniversalMessage.Domain

/** HashMap-backed implementation of [SessionInfoRepository]. All operations are synchronized. */
internal class SessionInfoRepositoryImpl : SessionInfoRepository {
  private val sessionInfoMap = mutableMapOf<SessionInfoKey, SessionInfo>()

  @Synchronized
  override fun get(vin: String, domain: Domain): SessionInfo? =
    sessionInfoMap[SessionInfoKey(vin, domain)]

  @Synchronized
  override fun set(vin: String, domain: Domain, sessionInfo: SessionInfo) {
    sessionInfoMap[SessionInfoKey(vin, domain)] = sessionInfo
  }

  @Synchronized
  override fun remove(vin: String, domain: Domain) {
    sessionInfoMap.remove(SessionInfoKey(vin, domain))
  }

  @Synchronized
  override fun incrementCounter(vin: String, domain: Domain) {
    val existingSession = get(vin, domain) ?: return
    set(vin, domain, existingSession.copy(counter = existingSession.counter + 1))
  }

  @Synchronized override fun getAll() = sessionInfoMap.toMap()

  @Synchronized
  override fun load(sessionInfoMap: Map<SessionInfoKey, SessionInfo>) {
    this.sessionInfoMap.clear()
    this.sessionInfoMap.putAll(sessionInfoMap)
  }
}
