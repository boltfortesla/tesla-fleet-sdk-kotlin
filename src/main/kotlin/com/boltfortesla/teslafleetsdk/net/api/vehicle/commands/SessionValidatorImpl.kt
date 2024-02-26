package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands

import com.boltfortesla.teslafleetsdk.handshake.SessionInfo
import com.boltfortesla.teslafleetsdk.handshake.SessionInfoAuthenticator
import com.boltfortesla.teslafleetsdk.log.Log
import com.google.protobuf.ByteString
import com.google.protobuf.kotlin.isNotEmpty
import com.tesla.generated.signatures.Signatures
import com.tesla.generated.universalmessage.UniversalMessage.RoutableMessage

/** Implementation of [SessionValidator] */
internal class SessionValidatorImpl(
  private val sessionInfoAuthenticator: SessionInfoAuthenticator
) : SessionValidator {
  override fun isSessionValid(
    responseMessage: RoutableMessage,
    requestUuid: ByteArray,
    requestSessionInfo: SessionInfo,
    vin: String,
  ): Boolean {
    val responseSessionInfo = Signatures.SessionInfo.parseFrom(responseMessage.sessionInfo)
    if (responseMessage.sessionInfo.isEmpty) return true

    return if (
      responseMessage.requestUuid.isNotEmpty() &&
        responseMessage.requestUuid != ByteString.copyFrom(requestUuid)
    ) {
      Log.w("UUIDs did not match")
      false
    } else if (responseSessionInfo.clockTime < requestSessionInfo.clockTime) {
      Log.w(
        "Response clock time (${responseSessionInfo.clockTime} was before the request clock time (${requestSessionInfo.clockTime})"
      )
      false
    } else {
      Log.d("Authenticating response")
      try {
        sessionInfoAuthenticator.authenticate(
          requestSessionInfo.sharedSecret,
          vin,
          requestUuid,
          responseSessionInfo.toByteArray(),
          responseMessage.signatureData.sessionInfoTag.tag.toByteArray(),
        )
        true
      } catch (exception: SessionInfoAuthenticator.ResponseAuthenticationFailedException) {
        Log.e("Response failed to authenticate", exception)
        false
      }
    }
  }
}
