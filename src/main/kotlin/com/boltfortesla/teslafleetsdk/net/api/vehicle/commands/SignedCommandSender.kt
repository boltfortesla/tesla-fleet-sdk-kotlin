package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands

import com.boltfortesla.teslafleetsdk.handshake.SessionInfo
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.response.VehicleCommandResponse.CommandProtocolResponse
import com.google.protobuf.GeneratedMessageV3

/** Signs and sends Command Protocol messages */
internal interface SignedCommandSender {
  /**
   * Signs [message] using [sessionInfo] and [clientPublicKey], and sends the request.
   *
   * Returns a [Result] containing a [CommandProtocolResponse]
   */
  suspend fun signAndSend(
    message: GeneratedMessageV3,
    sessionInfo: SessionInfo,
    clientPublicKey: ByteArray
  ): Result<CommandProtocolResponse>
}
