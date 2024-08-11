package com.boltfortesla.teslafleetsdk.commands

import com.boltfortesla.teslafleetsdk.crypto.HmacCalculator
import com.boltfortesla.teslafleetsdk.log.Log
import com.google.protobuf.ByteString
import com.tesla.generated.signatures.Signatures.SignatureType
import com.tesla.generated.signatures.copy
import com.tesla.generated.universalmessage.UniversalMessage.RoutableMessage
import com.tesla.generated.universalmessage.copy

/** Implementation of [CommandAuthenticator] when using HMAC-SHA256 authentication. */
internal class HmacCommandAuthenticator(private val hmacCalculator: HmacCalculator) :
  CommandAuthenticator {
  override val signatureType: SignatureType = SignatureType.SIGNATURE_TYPE_HMAC_PERSONALIZED

  override fun addAuthenticationData(
    message: RoutableMessage,
    metadata: ByteArray,
    sharedSecret: ByteArray
  ): RoutableMessage {
    val key = hmacCalculator.calculateSha256Hmac(sharedSecret, DATA.toByteArray())
    val tag =
      hmacCalculator.calculateSha256Hmac(
        key,
        metadata + message.protobufMessageAsBytes.toByteArray()
      )

    Log.d("calculated hmac tag has length ${tag.size}")
    return message.copy {
      signatureData =
        message.signatureData.copy {
          hMACPersonalizedData =
            message.signatureData.hmacPersonalizedData.copy { this.tag = ByteString.copyFrom(tag) }
        }
    }
  }

  private companion object {
    const val DATA = "authenticated command"
  }
}
