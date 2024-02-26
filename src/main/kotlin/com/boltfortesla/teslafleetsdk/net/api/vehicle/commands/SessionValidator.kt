package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands

import com.boltfortesla.teslafleetsdk.handshake.SessionInfo
import com.tesla.generated.universalmessage.UniversalMessage

/** Determines if a session is valid. */
internal interface SessionValidator {
  /**
   * Checks if the existing session for [vin], composed of [requestUuid], [requestSessionInfo],
   * matches the session in [responseMessage].
   */
  fun isSessionValid(
    responseMessage: UniversalMessage.RoutableMessage,
    requestUuid: ByteArray,
    requestSessionInfo: SessionInfo,
    vin: String,
  ): Boolean
}
