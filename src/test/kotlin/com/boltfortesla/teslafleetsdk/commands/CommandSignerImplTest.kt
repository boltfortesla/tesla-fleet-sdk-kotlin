package com.boltfortesla.teslafleetsdk.commands

import com.boltfortesla.teslafleetsdk.TestKeys
import com.boltfortesla.teslafleetsdk.crypto.HmacCalculatorImpl
import com.boltfortesla.teslafleetsdk.encoding.HexCodec.decodeHex
import com.boltfortesla.teslafleetsdk.encoding.TlvEncoderImpl
import com.boltfortesla.teslafleetsdk.fixtures.Constants
import com.boltfortesla.teslafleetsdk.fixtures.Constants.SHARED_SECRET
import com.boltfortesla.teslafleetsdk.fixtures.fakes.FakeIdentifiers
import com.boltfortesla.teslafleetsdk.handshake.SessionInfo
import com.boltfortesla.teslafleetsdk.keys.Pem
import com.boltfortesla.teslafleetsdk.keys.PublicKeyEncoderImpl
import com.google.common.truth.Truth.assertThat
import com.google.protobuf.ByteString
import com.tesla.generated.carserver.server.action
import com.tesla.generated.signatures.hMACPersonalizedSignatureData
import com.tesla.generated.signatures.keyIdentity
import com.tesla.generated.signatures.signatureData
import com.tesla.generated.universalmessage.UniversalMessage.Domain
import com.tesla.generated.universalmessage.destination
import com.tesla.generated.universalmessage.routableMessage
import java.util.Base64
import kotlin.time.Duration.Companion.seconds
import org.junit.Test

class CommandSignerImplTest {
  private val identifiers = FakeIdentifiers()
  private val commandAuthenticator = HmacCommandAuthenticator(HmacCalculatorImpl())
  private val publicKeyEncoder = PublicKeyEncoderImpl()
  private val commandSigner =
    CommandSignerImpl(
      commandAuthenticator,
      TlvEncoderImpl(),
      publicKeyEncoder,
      identifiers,
      commandExpiration = 15.seconds,
    )

  @Test
  fun sign_returnsSignedMessage() {
    val signedMessage =
      commandSigner.sign(
        Constants.VIN,
        action {},
        SessionInfo(Constants.EPOCH.decodeHex(), TIMER_START, 7, SHARED_SECRET.decodeHex()),
        Domain.DOMAIN_INFOTAINMENT,
        Base64.getDecoder().decode(Pem(TestKeys.CLIENT_PUBLIC_KEY).base64()),
      )

    assertThat(signedMessage)
      .isEqualTo(
        routableMessage {
          toDestination = destination { this.domain = Domain.DOMAIN_INFOTAINMENT }
          fromDestination = destination {
            routingAddress = ByteString.copyFrom(identifiers.routingAddress)
          }
          protobufMessageAsBytes = action {}.toByteString()
          uuid = ByteString.copyFrom(identifiers.uuid)
          signatureData = signatureData {
            signerIdentity = keyIdentity {
              publicKey =
                ByteString.copyFrom(
                  publicKeyEncoder.encodedPublicKey(TestKeys.CLIENT_PUBLIC_KEY_BYTES)
                )
            }
            hMACPersonalizedData = hMACPersonalizedSignatureData {
              epoch = ByteString.copyFrom(Constants.EPOCH.decodeHex())
              this.counter = 7
              expiresAt = TIMER_START + 15
              tag =
                ByteString.fromHex(
                  "acfe33afcf4dfbbba1b25a112619ddb0e3ae517e0e2f448f5e43575e60732f99"
                )
            }
          }
        }
      )
  }

  val TIMER_START = 2640
}
