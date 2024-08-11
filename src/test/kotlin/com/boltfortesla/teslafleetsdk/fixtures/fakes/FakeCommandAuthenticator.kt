package com.boltfortesla.teslafleetsdk.fixtures.fakes

import com.boltfortesla.teslafleetsdk.commands.CommandAuthenticator
import com.tesla.generated.signatures.Signatures
import com.tesla.generated.universalmessage.UniversalMessage

class FakeCommandAuthenticator : CommandAuthenticator {
  override val signatureType: Signatures.SignatureType =
    Signatures.SignatureType.SIGNATURE_TYPE_HMAC
  var authenticatedMessage: UniversalMessage.RoutableMessage? = null
  var metadata: ByteArray? = null
  var sharedSecret: ByteArray? = null

  override fun addAuthenticationData(
    message: UniversalMessage.RoutableMessage,
    metadata: ByteArray,
    sharedSecret: ByteArray
  ): UniversalMessage.RoutableMessage {
    authenticatedMessage = message
    this.metadata = metadata
    this.sharedSecret = sharedSecret
    return message
  }
}
