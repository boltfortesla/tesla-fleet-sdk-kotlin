package com.boltfortesla.teslafleetsdk.commands

import com.boltfortesla.teslafleetsdk.handshake.SessionInfo
import com.google.protobuf.ByteString
import com.tesla.generated.universalmessage.UniversalMessage.Domain
import com.tesla.generated.universalmessage.UniversalMessage.RoutableMessage

/** Signs a command */
internal interface CommandSigner {
  /**
   * Signs a command.
   *
   * @param vin VIN of the vehicle the command will be sent to
   * @param message a [ByteString] to be encoded in the [RoutableMessage]
   * @param sessionInfo [SessionInfo] session info acquired from the handshake
   * @param domain the [Domain] for the [message].
   * @param clientPublicKey the public key for the Tesla Developer Application
   */
  fun sign(
    vin: String,
    message: ByteString,
    sessionInfo: SessionInfo,
    domain: Domain,
    clientPublicKey: ByteArray,
  ): RoutableMessage
}
