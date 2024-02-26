package com.boltfortesla.teslafleetsdk.net

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.RetryConfig
import com.boltfortesla.teslafleetsdk.log.Log
import com.tesla.generated.universalmessage.UniversalMessage
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
 * [jitterFactorCalculator] to add jitter to the backoff. [messageFaultRecoveryStrategy] is invoked
 * if there is a retryable signed message failure (see [RETRYABLE_MESSAGE_FAULTS]).
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
  private val messageFaultRecoveryStrategy: MessageFaultRecoveryStrategy,
) {
  suspend fun <T> doWithRetries(
    action: suspend () -> Result<T>,
    isRetryable: Result<T>.() -> Boolean,
  ): Result<T> {
    var retryCount = 0
    var currentDelay = retryConfig.initialBackoffDelayMs

    while (coroutineContext.isActive) {
      Log.d("Making request")
      val result = action()
      Log.d("Request complete")
      val retryable = result.isRetryable() || result.isTemporarySigningError()
      if (retryable && retryCount++ < retryConfig.maxRetries) {
        val delay =
          (result.calculateDelay(currentDelay) * jitterFactorCalculator.calculate()).toLong()
        Log.d("Retrying in $delay ms. retryCount: $retryCount")
        delay(delay)
        currentDelay =
          (currentDelay * retryConfig.backoffFactor)
            .toLong()
            .coerceAtMost(retryConfig.maxBackoffDelay)
        if (result.isTemporarySigningError()) {
          Log.d(
            "Temporary signing error (${result.exceptionOrNull()}) detected. Attempting recovery"
          )
          messageFaultRecoveryStrategy.recover()
        }
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

  private fun Result<*>.isTemporarySigningError() =
    RETRYABLE_MESSAGE_FAULTS.contains((exceptionOrNull() as? SignedMessagesFaultException)?.fault)

  companion object {
    private const val RATE_LIMITED_CODE = 429
    private const val RETRY_AFTER_HEADER = "Retry-After"
    val RETRYABLE_MESSAGE_FAULTS =
      listOf(
        UniversalMessage.MessageFault_E.MESSAGEFAULT_ERROR_BUSY,
        UniversalMessage.MessageFault_E.MESSAGEFAULT_ERROR_TIMEOUT,
        UniversalMessage.MessageFault_E.MESSAGEFAULT_ERROR_INVALID_SIGNATURE,
        UniversalMessage.MessageFault_E.MESSAGEFAULT_ERROR_INVALID_TOKEN_OR_COUNTER,
        UniversalMessage.MessageFault_E.MESSAGEFAULT_ERROR_INTERNAL,
        UniversalMessage.MessageFault_E.MESSAGEFAULT_ERROR_INCORRECT_EPOCH,
        UniversalMessage.MessageFault_E.MESSAGEFAULT_ERROR_TIME_EXPIRED,
        UniversalMessage.MessageFault_E.MESSAGEFAULT_ERROR_TIME_TO_LIVE_TOO_LONG,
      )
  }
}
