package com.boltfortesla.teslafleetsdk.handshake

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SessionInfoTest {

  @Test
  fun equals() {
    val sessionInfo =
      SessionInfo("epoch".toByteArray(), clockTime = 0, counter = 0, "sharedSecret".toByteArray())

    assertThat(sessionInfo).isNotEqualTo(sessionInfo.copy(epoch = "other".toByteArray()))
    assertThat(sessionInfo).isNotEqualTo(sessionInfo.copy(clockTime = 1))
    assertThat(sessionInfo).isNotEqualTo(sessionInfo.copy(counter = 1))
    assertThat(sessionInfo).isNotEqualTo(sessionInfo.copy(sharedSecret = "other".toByteArray()))
  }
}
