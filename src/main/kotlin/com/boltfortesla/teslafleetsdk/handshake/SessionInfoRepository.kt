package com.boltfortesla.teslafleetsdk.handshake

import com.tesla.generated.universalmessage.UniversalMessage.Domain
import java.io.Serializable

/**
 * Repository that holds [SessionInfo]. A sessions "only expires when the infotainment system or
 * vehicle security controller reboot"
 * (https://github.com/teslamotors/vehicle-command/issues/48#issuecomment-1836985888).
 *
 * This means that [SessionInfo] should be held on to, unless it has become invalid.
 */
internal interface SessionInfoRepository {
  /** Returns the session info for [vin] in [domain], if present. */
  fun get(vin: String, domain: Domain): SessionInfo?

  /** Stores the session info for [vin] in [domain]. */
  fun set(vin: String, domain: Domain, sessionInfo: SessionInfo)

  /** Remove stored [SessionInfo] for [vin] and [domain]. */
  fun remove(vin: String, domain: Domain)

  /** Increment the counter for the [SessionInfo] for [vin] and [domain]. */
  fun incrementCounter(vin: String, domain: Domain)

  /** Returns all the cached [SessionInfo]. Can be used for backup to disk. */
  fun getAll(): Map<SessionInfoKey, SessionInfo>

  /**
   * Replaces existing in the repository with [sessionInfoMap]. Can be used for restore from disk.
   */
  fun load(sessionInfoMap: Map<SessionInfoKey, SessionInfo>)

  /** Composite key for [SessionInfo] made up of a [vin] and [domain]. */
  data class SessionInfoKey(val vin: String, val domain: Domain) : Serializable
}
