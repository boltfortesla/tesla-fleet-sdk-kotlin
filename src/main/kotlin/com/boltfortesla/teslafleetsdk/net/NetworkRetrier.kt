package com.boltfortesla.teslafleetsdk.net

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.RetryConfig
import com.boltfortesla.teslafleetsdk.log.Log
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import retrofit2.HttpException

/**
 * Manages retrying network requests
 *
 * The [RetryConfig] configures how retries are performed. Retries are backed off, using
 * [jitterFactorCalculator] to add jitter to the backoff
 *
 * Requests are only retried if they come back with a [RATE_LIMITED_CODE], or the caller determines
 * it is retryable (see [doWithRetries] isRetryable parameter).
 *
 * Requests are backed off according to [RetryConfig], however if a request comes back with a 429
 * (rate limited), the "Retry-After" header is used to determine when the request should be retried.
 */
internal class NetworkRetrier(
  private val retryConfig: RetryConfig,
  private val jitterFactorCalculator: JitterFactorCalculator,
) {
  suspend fun <T> doWithRetries(
    action: suspend () -> Result<T>,
    isRetryable: (Result<T>) -> Boolean,
  ): Result<T> {
    var retryCount = 0
    var currentDelay = retryConfig.initialBackoffDelay

    while (coroutineContext.isActive) {
      Log.d("Making request")
      val result = action()
      Log.d("Request complete. Retrying if necessary")

      val retryAfter = result.getRetryAfter()
      val retryableRateLimit = retryAfter <= retryConfig.maxRetryAfter
      val delay = maxOf(currentDelay * jitterFactorCalculator.calculate(), retryAfter)

      if (isRetryable(result) && retryableRateLimit && retryCount++ < retryConfig.maxRetries) {
        Log.d("Retrying in $delay ms. retryCount: $retryCount")
        delay(delay)
        currentDelay =
          (currentDelay * retryConfig.backoffFactor).coerceAtMost(retryConfig.maxBackoffDelay)
      } else {
        Log.d("Not retrying")
        return result
      }
    }

    return Result.failure(CancellationException("Coroutine cancelled"))
  }

  private fun Result<*>.getRetryAfter(): Duration {
    if (isSuccess) {
      return Duration.ZERO
    }

    val exception = exceptionOrNull() as? HttpException
    if (exception?.code() != RATE_LIMITED_CODE) {
      return Duration.ZERO
    }

    return exception.response()?.headers()?.get(RETRY_AFTER_HEADER)?.toLongOrNull()?.seconds
      ?: Duration.ZERO
  }

  companion object {
    private const val RATE_LIMITED_CODE = 429
    private const val RETRY_AFTER_HEADER = "Retry-After"
  }
}
