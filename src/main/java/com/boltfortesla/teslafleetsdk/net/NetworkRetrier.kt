package com.boltfortesla.teslafleetsdk.net

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.RetryConfig
import com.boltfortesla.teslafleetsdk.log.Log
import java.time.Duration
import kotlin.coroutines.coroutineContext
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
  private val jitterFactorCalculator: JitterFactorCalculator
) {
  suspend fun <T> doWithRetries(
    action: suspend () -> Result<T>,
    isRetryable: (Result<T>) -> Boolean,
  ): Result<T> {
    var retryCount = 0
    var currentDelay = retryConfig.initialBackoffDelayMs

    while (coroutineContext.isActive) {
      Log.d("Making request")
      val result = action()
      Log.d("Request complete. Retrying if necessary")
      if (isRetryable(result) && retryCount++ < retryConfig.maxRetries) {
        val delay =
          (result.calculateDelay(currentDelay) * jitterFactorCalculator.calculate()).toLong()
        Log.d("Retrying in $delay ms. retryCount: $retryCount")
        delay(delay)
        currentDelay =
          (currentDelay * retryConfig.backoffFactor)
            .toLong()
            .coerceAtMost(retryConfig.maxBackoffDelay)
      } else {
        Log.d("Not retrying")
        return result
      }
    }

    return Result.failure(CancellationException("Coroutine cancelled"))
  }

  private fun Result<*>.calculateDelay(currentDelay: Long): Long {
    onFailure {
      if (it is HttpException && it.code() == RATE_LIMITED_CODE) {
        val retryAfter =
          Duration.ofSeconds(
            it.response()?.headers()?.get(RETRY_AFTER_HEADER)?.toLongOrNull() ?: currentDelay
          )
        Log.d("Rate limited. Retrying in: $retryAfter")
        return retryAfter.toMillis()
      }
    }
    return currentDelay
  }

  companion object {
    private const val RATE_LIMITED_CODE = 429
    private const val RETRY_AFTER_HEADER = "Retry-After"
  }
}
