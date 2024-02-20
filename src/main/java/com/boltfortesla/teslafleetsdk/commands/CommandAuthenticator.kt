package com.boltfortesla.teslafleetsdk.commands

import com.tesla.generated.signatures.Signatures.SignatureType
import com.tesla.generated.universalmessage.UniversalMessage.RoutableMessage

/** Adds signed authentication data to a [RoutableMessage] to prepare for sending. */
internal interface CommandAuthenticator {
  /** The [SignatureType] relevant to this [CommandAuthenticator]. */
  val signatureType: SignatureType

  /**
   * Adds the signed authentication data to [message].
   *
   * @param message the [message] to sign
   * @param metadata the message's metadata
   * @param sharedSecret the shared secret for the client private/vehicle public key pair
   */
  fun addAuthenticationData(
    message: RoutableMessage,
    metadata: ByteArray,
    sharedSecret: ByteArray
  ): RoutableMessage
}
