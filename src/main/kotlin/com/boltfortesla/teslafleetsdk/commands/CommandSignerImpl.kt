package com.boltfortesla.teslafleetsdk.commands

import com.boltfortesla.teslafleetsdk.Identifiers
import com.boltfortesla.teslafleetsdk.encoding.HexCodec.toHexString
import com.boltfortesla.teslafleetsdk.encoding.TlvEncoder
import com.boltfortesla.teslafleetsdk.handshake.SessionInfo
import com.boltfortesla.teslafleetsdk.keys.PublicKeyEncoder
import com.boltfortesla.teslafleetsdk.log.Log
import com.google.protobuf.ByteString
import com.google.protobuf.GeneratedMessageV3
import com.tesla.generated.signatures.Signatures.Tag
import com.tesla.generated.signatures.hMACPersonalizedSignatureData
import com.tesla.generated.signatures.keyIdentity
import com.tesla.generated.signatures.signatureData
import com.tesla.generated.universalmessage.UniversalMessage
import com.tesla.generated.universalmessage.UniversalMessage.Domain
import com.tesla.generated.universalmessage.destination
import com.tesla.generated.universalmessage.routableMessage
import java.nio.ByteBuffer
import java.time.Duration

/** Implementation of [CommandSigner] */
internal class CommandSignerImpl(
  // TODO: Allow for swapping between HmacCommandAuthenticator and AesCommandAuthenticator
  private val commandAuthenticator: CommandAuthenticator,
  private val tlvEncoder: TlvEncoder,
  private val publicKeyEncoder: PublicKeyEncoder,
  private val identifiers: Identifiers,
) : CommandSigner {
  override fun sign(
    vin: String,
    message: GeneratedMessageV3,
    sessionInfo: SessionInfo,
    domain: Domain,
    clientPublicKey: ByteArray,
  ): UniversalMessage.RoutableMessage {
    val currentTimeSeconds = (System.currentTimeMillis() / 1000).toInt()
    val zeroTime = currentTimeSeconds - sessionInfo.clockTime
    val expirationTime = currentTimeSeconds + COMMAND_EXPIRATION.seconds.toInt() - zeroTime
    val counter = sessionInfo.counter

    val metadata =
      tlvEncoder.encodeTlv(
        mapOf(
          Tag.TAG_SIGNATURE_TYPE_VALUE to
            byteArrayOf(commandAuthenticator.signatureType.number.toByte()),
          Tag.TAG_DOMAIN_VALUE to byteArrayOf(domain.number.toByte()),
          Tag.TAG_PERSONALIZATION_VALUE to vin.toByteArray(),
          Tag.TAG_EPOCH_VALUE to sessionInfo.epoch,
          Tag.TAG_EXPIRES_AT_VALUE to
            ByteBuffer.allocate(Int.SIZE_BYTES).putInt(expirationTime).array(),
          Tag.TAG_COUNTER_VALUE to ByteBuffer.allocate(Int.SIZE_BYTES).putInt(counter).array()
        ),
        0xff.toByte()
      )
    Log.d("metadataTLV: ${metadata.toHexString()}")

    val signedMessage = routableMessage {
      toDestination = destination { this.domain = domain }
      fromDestination = destination {
        routingAddress = ByteString.copyFrom(identifiers.randomRoutingAddress())
      }
      protobufMessageAsBytes = message.toByteString()
      uuid = ByteString.copyFrom(identifiers.randomUuid())
      signatureData = signatureData {
        signerIdentity = keyIdentity {
          publicKey = ByteString.copyFrom(publicKeyEncoder.encodedPublicKey(clientPublicKey))
        }
        hMACPersonalizedData = hMACPersonalizedSignatureData {
          epoch = ByteString.copyFrom(sessionInfo.epoch)
          this.counter = counter
          expiresAt = expirationTime
        }
      }
    }
    Log.d("Authenticating message ${signedMessage.toByteArray().toHexString()}")
    return commandAuthenticator.addAuthenticationData(
      signedMessage,
      metadata,
      sessionInfo.sharedSecret
    )
  }

  private companion object {
    private val COMMAND_EXPIRATION = Duration.ofSeconds(15)
  }
}
