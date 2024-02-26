@file:OptIn(ExperimentalCoroutinesApi::class, ExperimentalCoroutinesApi::class)

package com.boltfortesla.teslafleetsdk.net

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.RetryConfig
import com.google.common.truth.Truth.assertThat
import com.tesla.generated.universalmessage.UniversalMessage.MessageFault_E
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
    val networkRetrier =
      NetworkRetrier(RetryConfig(), jitterFactorCalculator, MessageFaultRecoveryStrategy.NONE)

    val result = networkRetrier.doWithRetries(fakeAction) { false }

    assertThat(actionCount).isEqualTo(1)
    assertThat(result).isEqualTo(expectedResult)
  }

  @Test
  fun doWithRetries_fails_retriesMaxRetries() = runTest {
    val networkRetrier =
      NetworkRetrier(
        RetryConfig(maxRetries = 5),
        jitterFactorCalculator,
        MessageFaultRecoveryStrategy.NONE
      )

    val result = networkRetrier.doWithRetries(fakeAction) { true }

    assertThat(actionCount).isEqualTo(6)
    assertThat(result).isEqualTo(expectedResult)
  }

  @Test
  fun doWithRetries_failsThenSucceeds_retriesUntilSuccess() = runTest {
    val networkRetrier =
      NetworkRetrier(
        RetryConfig(maxRetries = 5),
        jitterFactorCalculator,
        MessageFaultRecoveryStrategy.NONE
      )

    val result = networkRetrier.doWithRetries(fakeAction) { actionCount <= 3 }

    assertThat(actionCount).isEqualTo(4)
    assertThat(result).isEqualTo(expectedResult)
  }

  @Test
  fun doWithRetries_fails_noRetries_callsActionOnce() = runTest {
    val networkRetrier =
      NetworkRetrier(
        RetryConfig(maxRetries = 0),
        jitterFactorCalculator,
        MessageFaultRecoveryStrategy.NONE
      )

    val result =
      networkRetrier.doWithRetries(
        fakeAction,
      ) {
        true
      }

    assertThat(actionCount).isEqualTo(1)
    assertThat(result).isEqualTo(expectedResult)
  }

  @Test
  fun doWithRetries_backsOffExponentially() {
    val networkRetrier =
      NetworkRetrier(
        RetryConfig(maxRetries = 5, initialBackoffDelayMs = 100, backoffFactor = 1.5),
        jitterFactorCalculator,
        MessageFaultRecoveryStrategy.NONE
      )

    val result =
      scope.async {
        networkRetrier.doWithRetries(
          fakeAction,
        ) {
          true
        }
      }
    dispatcher.scheduler.runCurrent()

    assertThat(actionCount).isEqualTo(1)
    for (delaysAndCounts in listOf(100L to 2, 150L to 3, 225L to 4, 338L to 5, 507L to 6)) {
      dispatcher.advanceTimeByAndRun(delaysAndCounts.first)
      assertThat(actionCount).isEqualTo(delaysAndCounts.second)
    }
    runTest { assertThat(result.await()).isEqualTo(expectedResult) }
  }

  @Test
  fun doWithReties_429_usesRetryAfterHeader() {
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
              .build()
          )
        )
      )
    val networkRetrier =
      NetworkRetrier(
        RetryConfig(maxRetries = 5),
        jitterFactorCalculator,
        MessageFaultRecoveryStrategy.NONE
      )

    val result =
      scope.async {
        networkRetrier.doWithRetries(
          fakeAction,
        ) {
          true
        }
      }
    dispatcher.scheduler.runCurrent()

    assertThat(actionCount).isEqualTo(1)
    val retryAfterMs = (12345 * 1000).toLong()
    for (delaysAndCounts in
      listOf(
        retryAfterMs to 2,
        retryAfterMs to 3,
        retryAfterMs to 4,
        retryAfterMs to 5,
        retryAfterMs to 6
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
          initialBackoffDelayMs = 100,
          backoffFactor = 1.75,
          maxBackoffDelay = 200
        ),
        jitterFactorCalculator,
        MessageFaultRecoveryStrategy.NONE
      )

    val result =
      CoroutineScope(dispatcher).async {
        networkRetrier.doWithRetries(
          fakeAction,
        ) {
          true
        }
      }
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
        },
        MessageFaultRecoveryStrategy.NONE
      )

    val result =
      CoroutineScope(dispatcher).async {
        networkRetrier.doWithRetries(
          fakeAction,
        ) {
          true
        }
      }
    dispatcher.scheduler.runCurrent()

    assertThat(actionCount).isEqualTo(1)
    for (delaysAndCounts in
      listOf(100L to 1, 10L to 2, 150L to 2, 15L to 3, 200L to 3, 47L to 4, 300L to 4, 71L to 5)) {
      dispatcher.advanceTimeByAndRun(delaysAndCounts.first)
      assertThat(actionCount).isEqualTo(delaysAndCounts.second)
    }

    runTest { assertThat(result.await()).isEqualTo(expectedResult) }
  }

  @Test
  fun doWithRetries_retryableMessageFault_attemptsRecovery() = runTest {
    var recoveryAttempts = 0
    val networkRetrier =
      NetworkRetrier(RetryConfig(maxRetries = 5), jitterFactorCalculator) { recoveryAttempts++ }
    expectedResult =
      Result.failure(
        SignedMessagesFaultException(MessageFault_E.MESSAGEFAULT_ERROR_INVALID_SIGNATURE)
      )

    val result = networkRetrier.doWithRetries(fakeAction) { false }

    assertThat(actionCount).isEqualTo(6)
    assertThat(recoveryAttempts).isEqualTo(5)
    assertThat(result).isEqualTo(expectedResult)
  }

  @Test
  fun doWithRetries_nonRetryableMessageFault_doesNotAttemptRecovery() = runTest {
    var recoveryAttempts = 0
    val networkRetrier =
      NetworkRetrier(RetryConfig(maxRetries = 5), jitterFactorCalculator) { recoveryAttempts++ }
    expectedResult =
      Result.failure(SignedMessagesFaultException(MessageFault_E.MESSAGEFAULT_ERROR_UNKNOWN_KEY_ID))

    val result = networkRetrier.doWithRetries(fakeAction) { false }

    assertThat(actionCount).isEqualTo(1)
    assertThat(recoveryAttempts).isEqualTo(0)
    assertThat(result).isEqualTo(expectedResult)
  }

  private fun TestDispatcher.advanceTimeByAndRun(delayTimeMillis: Long) {
    scheduler.advanceTimeBy(delayTimeMillis)
    scheduler.runCurrent()
  }
}
