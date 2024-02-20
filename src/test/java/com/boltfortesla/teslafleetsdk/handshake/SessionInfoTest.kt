package com.boltfortesla.teslafleetsdk.handshake

import com.google.common.truth.Truth.assertThat
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Test

class SessionInfoTest {

  @Test
  fun equals() {
    val sessionInfo =
      SessionInfo(
        "epoch".toByteArray(),
        clockTime = 0,
        counter = AtomicInteger(0),
        "sharedSecret".toByteArray()
      )

    assertThat(sessionInfo).isNotEqualTo(sessionInfo.copy(epoch = "other".toByteArray()))
    assertThat(sessionInfo).isNotEqualTo(sessionInfo.copy(clockTime = 1))
    assertThat(sessionInfo).isNotEqualTo(sessionInfo.copy(counter = AtomicInteger(1)))
    assertThat(sessionInfo).isNotEqualTo(sessionInfo.copy(sharedSecret = "other".toByteArray()))
  }
}
