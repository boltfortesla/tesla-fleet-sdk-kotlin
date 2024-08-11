package com.boltfortesla.teslafleetsdk.handshake

/** Authentications the SessionInfo from a command response. */
internal interface SessionInfoAuthenticator {
  /**
   * Verifies that the [sessionInfo] provided by a Vehicle is valid.
   *
   * @param sharedSecret the [sharedSecret] to use as the HMAC key
   * @param vin the VIN of the vehicle that sent the response
   * @param requestUuid the UUID of the request
   * @param sessionInfo the raw Session Info from the response
   * @param sessionInfoTag the raw Session Info Tag from the response
   */
  fun authenticate(
    sharedSecret: ByteArray,
    vin: String,
    requestUuid: ByteArray,
    sessionInfo: ByteArray,
    sessionInfoTag: ByteArray
  )

  /** Exception thrown when the session info response fails to authenticate. */
  class ResponseAuthenticationFailedException : IllegalStateException()
}
