package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.SharedSecretFetcher
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.response.VehicleCommandResponse.CommandProtocolResponse
import com.google.protobuf.ByteString
import com.tesla.generated.universalmessage.UniversalMessage.Domain

/** Signs and sends Command Protocol messages */
internal interface SignedCommandSender {
  /**
   * Signs [message] using [clientPublicKey], and sends the request. [sharedSecretFetcher] is used
   * if there is a signed message fault and a new handshake needs to be performed.
   *
   * Returns a [Result] containing a [CommandProtocolResponse]
   */
  suspend fun signAndSend(
    domain: Domain,
    message: ByteString,
    clientPublicKey: ByteArray,
    sharedSecretFetcher: SharedSecretFetcher,
  ): Result<CommandProtocolResponse>
}
