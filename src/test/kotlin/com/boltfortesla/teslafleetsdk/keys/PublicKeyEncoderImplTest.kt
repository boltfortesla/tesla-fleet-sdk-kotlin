package com.boltfortesla.teslafleetsdk.keys

import com.boltfortesla.teslafleetsdk.TestKeys
import com.google.common.truth.Truth.assertThat
import org.bouncycastle.util.encoders.Hex
import org.junit.Test

class PublicKeyEncoderImplTest {
  @Test
  fun encodedPublicKey_encodesKey() {
    val expectedEncodedKey = Hex.decode(TestKeys.ENCODED_CLIENT_PUBLIC_KEY_HEX)

    assertThat(PublicKeyEncoderImpl().encodedPublicKey(TestKeys.CLIENT_PUBLIC_KEY_BYTES))
      .isEqualTo(expectedEncodedKey)
  }
}
