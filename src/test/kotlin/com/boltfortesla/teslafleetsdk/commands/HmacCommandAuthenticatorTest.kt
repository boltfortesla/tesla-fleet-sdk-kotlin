package com.boltfortesla.teslafleetsdk.commands

import com.boltfortesla.teslafleetsdk.crypto.HmacCalculatorImpl
import com.google.common.truth.Truth.assertThat
import com.google.protobuf.ByteString
import com.tesla.generated.signatures.Signatures.SignatureType
import com.tesla.generated.signatures.hMACPersonalizedSignatureData
import com.tesla.generated.signatures.signatureData
import com.tesla.generated.universalmessage.routableMessage
import org.junit.Test

class HmacCommandAuthenticatorTest {

  private val commandAuthenticator = HmacCommandAuthenticator(HmacCalculatorImpl())

  @Test
  fun getSignatureType_isHmac() {
    assertThat(commandAuthenticator.signatureType)
      .isEqualTo(SignatureType.SIGNATURE_TYPE_HMAC_PERSONALIZED)
  }

  @Test
  fun addAuthenticationData_addsSignatureDataToMessage() {
    val expectedResultTag = "c4626b4fac0d7350dc8bb173ec1ecd5dbd6ea09a3ce23984d87732cf92b66902"
    val authenticatedMessage =
      commandAuthenticator.addAuthenticationData(
        routableMessage {
          protobufMessageAsBytes = ByteString.copyFromUtf8("message")
          signatureData = signatureData {
            hMACPersonalizedData = hMACPersonalizedSignatureData { counter = 9999 }
          }
        },
        metadata = byteArrayOf(0, 1, 2, 3),
        sharedSecret = byteArrayOf(3, 4, 5, 6),
      )

    assertThat(authenticatedMessage)
      .isEqualTo(
        routableMessage {
          protobufMessageAsBytes = ByteString.copyFromUtf8("message")
          signatureData = signatureData {
            hMACPersonalizedData = hMACPersonalizedSignatureData {
              counter = 9999
              tag = ByteString.fromHex(expectedResultTag)
            }
          }
        }
      )
  }
}
