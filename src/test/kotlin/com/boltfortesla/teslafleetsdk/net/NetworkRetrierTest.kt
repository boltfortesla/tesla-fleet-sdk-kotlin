@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package com.boltfortesla.teslafleetsdk.net

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.RetryConfig
import com.google.common.truth.Truth.assertThat
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class NetworkRetrierTest {

  private val jitterFactorCalculator =
    object : JitterFactorCalculator {
      override fun calculate(): Double {
        return 1.0
      }
    }

  private var actionCount = 0
  private var expectedResult = Result.success(Unit)
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
        RetryConfig(maxRetries = 5, initialBackoffDelay = 100.milliseconds, backoffFactor = 1.5),
        jitterFactorCalculator,
      )

    val result = scope.async { networkRetrier.doWithRetries(fakeAction) { true } }
    dispatcher.scheduler.runCurrent()

    assertThat(actionCount).isEqualTo(1)
    for (delaysAndCounts in
      listOf(
        100.milliseconds to 2,
        150.milliseconds to 3,
        225.milliseconds to 4,
        338.milliseconds to 5,
        507.milliseconds to 6,
      )) {
      dispatcher.advanceTimeByAndRun(delaysAndCounts.first)
      assertThat(actionCount).isEqualTo(delaysAndCounts.second)
    }
    runTest { assertThat(result.await()).isEqualTo(expectedResult) }
  }

  @Test
  fun doWithRetries_429_usesRetryAfterHeader() {
    expectedResult =
      Result.failure(
        HttpException(
          Response.error<Any>(
            "".toResponseBody(),
            okhttp3.Response.Builder() //
              .body("".toResponseBody())
              .code(429)
              .message("Response.error()")
              .protocol(Protocol.HTTP_1_1)
              .addHeader("Retry-After", "12345")
              .request(Request.Builder().url("http://localhost/").build())
              .build(),
          )
        )
      )
    val networkRetrier = NetworkRetrier(RetryConfig(maxRetries = 5), jitterFactorCalculator)

    val result = scope.async { networkRetrier.doWithRetries(fakeAction) { true } }
    dispatcher.scheduler.runCurrent()

    assertThat(actionCount).isEqualTo(1)
    val retryAfterMs = 12345.seconds
    for (delaysAndCounts in
      listOf(
        retryAfterMs to 2,
        retryAfterMs to 3,
        retryAfterMs to 4,
        retryAfterMs to 5,
        retryAfterMs to 6,
      )) {
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
          initialBackoffDelay = 100.milliseconds,
          backoffFactor = 1.75,
          maxBackoffDelay = 200.milliseconds,
        ),
        jitterFactorCalculator,
      )

    val result =
      CoroutineScope(dispatcher).async { networkRetrier.doWithRetries(fakeAction) { true } }
    dispatcher.scheduler.runCurrent()

    assertThat(actionCount).isEqualTo(1)
    for (delaysAndCounts in
      listOf(
        100.milliseconds to 2,
        175.milliseconds to 3,
        200.milliseconds to 4,
        200.milliseconds to 5,
      )) {
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
          initialBackoffDelay = 100.milliseconds,
          backoffFactor = 1.5,
          maxBackoffDelay = 10.seconds,
        ),
        object : JitterFactorCalculator {
          override fun calculate(): Double {
            return 1.10
          }
        },
      )

    val result =
      CoroutineScope(dispatcher).async { networkRetrier.doWithRetries(fakeAction) { true } }
    dispatcher.scheduler.runCurrent()

    assertThat(actionCount).isEqualTo(1)
    for (delaysAndCounts in
      listOf(
        100.milliseconds to 1,
        10.milliseconds to 2,
        150.milliseconds to 2,
        15.milliseconds to 3,
        225.milliseconds to 3,
        47.5.milliseconds to 4,
        337.5.milliseconds to 4,
        33.75.milliseconds to 5,
      )) {
      dispatcher.advanceTimeByAndRun(delaysAndCounts.first)
      assertThat(actionCount).isEqualTo(delaysAndCounts.second)
    }

    runTest { assertThat(result.await()).isEqualTo(expectedResult) }
  }

  private fun TestDispatcher.advanceTimeByAndRun(delayTimeMillis: Duration) {
    scheduler.advanceTimeBy(delayTimeMillis)
    scheduler.runCurrent()
  }
}
