package com.boltfortesla.teslafleetsdk.handshake

import com.boltfortesla.teslafleetsdk.crypto.HmacCalculator
import com.boltfortesla.teslafleetsdk.encoding.HexCodec.toHexString
import com.boltfortesla.teslafleetsdk.encoding.TlvEncoder
import com.boltfortesla.teslafleetsdk.handshake.SessionInfoAuthenticator.ResponseAuthenticationFailedException
import com.boltfortesla.teslafleetsdk.log.Log
import com.tesla.generated.signatures.Signatures.SignatureType
import com.tesla.generated.signatures.Signatures.Tag
import java.security.MessageDigest

/** Implementation of [SessionInfoAuthenticator] */
internal class SessionInfoAuthenticatorImpl(
  private val tlvEncoder: TlvEncoder,
  private val hmacCalculator: HmacCalculator
) : SessionInfoAuthenticator {
  override fun authenticate(
    sharedSecret: ByteArray,
    vin: String,
    handshakeUuid: ByteArray,
    sessionInfo: ByteArray,
    sessionInfoTag: ByteArray,
  ) {
    val sessionInfoKey = hmacCalculator.calculateSha256Hmac(sharedSecret, DATA.toByteArray())

    val metadata =
      tlvEncoder.encodeTlv(
        mapOf(
          Tag.TAG_SIGNATURE_TYPE_VALUE to
            byteArrayOf(SignatureType.SIGNATURE_TYPE_HMAC.number.toByte()),
          Tag.TAG_PERSONALIZATION_VALUE to vin.toByteArray(),
          Tag.TAG_CHALLENGE_VALUE to handshakeUuid,
        ),
        suffix = 0xff.toByte()
      )
    Log.d("metadataTLV ${metadata.toHexString()}")
    // Constant time comparison!
    val digestsMatch =
      MessageDigest.isEqual(
        sessionInfoTag,
        hmacCalculator.calculateSha256Hmac(sessionInfoKey, metadata + sessionInfo)
      )
    if (!digestsMatch) {
      Log.d("handshake failed, digests do not match")
      throw ResponseAuthenticationFailedException()
    }
    Log.d("digests match!")
  }

  companion object {
    private const val DATA = "session info"
  }
}
