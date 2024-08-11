package com.boltfortesla.teslafleetsdk.keys

import com.boltfortesla.teslafleetsdk.TestKeys
import com.boltfortesla.teslafleetsdk.TestKeys.CLIENT_PUBLIC_KEY_BASE_64
import com.boltfortesla.teslafleetsdk.TestKeys.CLIENT_PUBLIC_KEY_BYTES
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PemTest {
  @Test
  fun base64_extractsBase64() {
    assertThat(Pem(TestKeys.CLIENT_PUBLIC_KEY).base64()).isEqualTo(CLIENT_PUBLIC_KEY_BASE_64)
  }

  @Test
  fun byteArray_convertsToByteArray() {
    assertThat(Pem(TestKeys.CLIENT_PUBLIC_KEY).byteArray()).isEqualTo(CLIENT_PUBLIC_KEY_BYTES)
  }
}
