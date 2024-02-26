package com.boltfortesla.teslafleetsdk.net

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.RetryConfig
import com.boltfortesla.teslafleetsdk.log.Log
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.VehicleTemporarilyUnavailableException
import java.io.IOException
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.isActive
import retrofit2.HttpException

/** Implementation of [NetworkExecutor] */
internal class NetworkExecutorImpl(
  private val retryConfig: RetryConfig,
  private val jitterFactorCalculator: JitterFactorCalculator,
) : NetworkExecutor {
  override suspend fun <T> execute(
    messageFaultRecoveryStrategy: MessageFaultRecoveryStrategy,
    action: suspend () -> T,
  ): Result<T> {
    return NetworkRetrier(retryConfig, jitterFactorCalculator, messageFaultRecoveryStrategy)
      .doWithRetries({
        if (!coroutineContext.isActive) {
          Log.d("Coroutine not active, not executing request")
          Result.failure(CancellationException("coroutine not active"))
        } else {
          try {
            Result.success(action()).also { Log.d("Request succeeded") }
          } catch (throwable: Throwable) {
            Log.e("Request failed", throwable)
            Result.failure(throwable)
          }
        }
      }) {
        if (isSuccess) {
          false
        } else {
          Log.d("Network call failed with ${exceptionOrNull()}")
          when (val exception = exceptionOrNull()) {
            is IOException,
            is VehicleTemporarilyUnavailableException -> true
            is HttpException -> RETRYABLE_STATUS_CODES.contains(exception.code())
            else -> false
          }
        }
      }
  }

  internal companion object {
    /**
     * A list of HTTP status codes that are considered retryable. Any other status code will not
     * result in a retry.
     */
    val RETRYABLE_STATUS_CODES = listOf(408, 429, 503, 504)
  }
}
