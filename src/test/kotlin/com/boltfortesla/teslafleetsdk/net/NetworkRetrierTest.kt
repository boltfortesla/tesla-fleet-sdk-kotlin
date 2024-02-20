@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package com.boltfortesla.teslafleetsdk.net

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.RetryConfig
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test

class NetworkRetrierTest {

  private val jitterFactorCalculator =
    object : JitterFactorCalculator {
      override fun calculate(): Double {
        return 1.0
      }
    }

  private var actionCount = 0
  private val expectedResult = Result.success(Unit)
  private val fakeAction: () -> Result<Unit> = {
    actionCount++
    expectedResult
  }
  private val dispatcher = StandardTestDispatcher()
  private val scope = CoroutineScope(dispatcher)

  @Test
  fun doWithRetries_success_callsActionOnce() = runTest {
    val networkRetrier = NetworkRetrier(RetryConfig(), jitterFactorCalculator)

    val result = networkRetrier.doWithRetries(fakeAction) { false }

    assertThat(actionCount).isEqualTo(1)
    assertThat(result).isEqualTo(expectedResult)
  }

  @Test
  fun doWithRetries_fails_retriesMaxRetries() = runTest {
    val networkRetrier = NetworkRetrier(RetryConfig(maxRetries = 5), jitterFactorCalculator)

    val result = networkRetrier.doWithRetries(fakeAction) { true }

    assertThat(actionCount).isEqualTo(6)
    assertThat(result).isEqualTo(expectedResult)
  }

  @Test
  fun doWithRetries_failsThenSucceeds_retriesUntilSuccess() = runTest {
    val networkRetrier = NetworkRetrier(RetryConfig(maxRetries = 5), jitterFactorCalculator)

    val result = networkRetrier.doWithRetries(fakeAction) { actionCount <= 3 }

    assertThat(actionCount).isEqualTo(4)
    assertThat(result).isEqualTo(expectedResult)
  }

  @Test
  fun doWithRetries_fails_noRetries_callsActionOnce() = runTest {
    val networkRetrier = NetworkRetrier(RetryConfig(maxRetries = 0), jitterFactorCalculator)

    val result = networkRetrier.doWithRetries(fakeAction) { true }

    assertThat(actionCount).isEqualTo(1)
    assertThat(result).isEqualTo(expectedResult)
  }

  @Test
  fun doWithRetries_backsOffExponentially() {
    val networkRetrier =
      NetworkRetrier(
        RetryConfig(maxRetries = 5, initialBackoffDelayMs = 100, backoffFactor = 1.5),
        jitterFactorCalculator
      )

    val result = scope.async { networkRetrier.doWithRetries(fakeAction) { true } }
    dispatcher.scheduler.runCurrent()

    assertThat(actionCount).isEqualTo(1)
    for (delaysAndCounts in listOf(100L to 2, 150L to 3, 225L to 4, 338L to 5, 507L to 6)) {
      dispatcher.advanceTimeByAndRun(delaysAndCounts.first)
      assertThat(actionCount).isEqualTo(delaysAndCounts.second)
    }
    runTest { assertThat(result.await()).isEqualTo(expectedResult) }
  }

  @Test
  fun doWithRetries_limitsDelayToMaxBackoffDelay() {
    val networkRetrier =
      NetworkRetrier(
        RetryConfig(
          maxRetries = 4,
          initialBackoffDelayMs = 100,
          backoffFactor = 1.75,
          maxBackoffDelay = 200
        ),
        jitterFactorCalculator
      )

    val result =
      CoroutineScope(dispatcher).async { networkRetrier.doWithRetries(fakeAction) { true } }
    dispatcher.scheduler.runCurrent()

    assertThat(actionCount).isEqualTo(1)
    for (delaysAndCounts in listOf(100L to 2, 175L to 3, 200L to 4, 200L to 5)) {
      dispatcher.advanceTimeByAndRun(delaysAndCounts.first)
      assertThat(actionCount).isEqualTo(delaysAndCounts.second)
    }
    runTest { assertThat(result.await()).isEqualTo(expectedResult) }
  }

  @Test
  fun doWithRetries_usesJitter() {
    val networkRetrier =
      NetworkRetrier(
        RetryConfig(
          maxRetries = 4,
          initialBackoffDelayMs = 100,
          backoffFactor = 1.5,
          maxBackoffDelay = 10000
        ),
        object : JitterFactorCalculator {
          override fun calculate(): Double {
            return 1.10
          }
        }
      )

    val result =
      CoroutineScope(dispatcher).async { networkRetrier.doWithRetries(fakeAction) { true } }
    dispatcher.scheduler.runCurrent()

    assertThat(actionCount).isEqualTo(1)
    for (delaysAndCounts in
      listOf(100L to 1, 10L to 2, 150L to 2, 15L to 3, 200L to 3, 47L to 4, 300L to 4, 71L to 5)) {
      dispatcher.advanceTimeByAndRun(delaysAndCounts.first)
      assertThat(actionCount).isEqualTo(delaysAndCounts.second)
    }

    runTest { assertThat(result.await()).isEqualTo(expectedResult) }
  }

  private fun TestDispatcher.advanceTimeByAndRun(delayTimeMillis: Long) {
    scheduler.advanceTimeBy(delayTimeMillis)
    scheduler.runCurrent()
  }
}
