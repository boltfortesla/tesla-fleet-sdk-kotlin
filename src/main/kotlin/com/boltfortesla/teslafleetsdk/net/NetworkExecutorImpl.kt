package com.boltfortesla.teslafleetsdk.net

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.RetryConfig
import com.boltfortesla.teslafleetsdk.log.Log
import com.boltfortesla.teslafleetsdk.net.NetworkExecutor.Companion.HTTP_TOO_MANY_REQUESTS
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.UnrecoverableHttpException
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.VehicleTemporarilyUnavailableException
import com.tesla.generated.universalmessage.UniversalMessage.MessageFault_E
import java.io.IOException
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.isActive
import retrofit2.HttpException

/** Implementation of [NetworkExecutor] */
internal class NetworkExecutorImpl(
  private val retryConfig: RetryConfig,
  private val jitterFactorCalculator: JitterFactorCalculator
) : NetworkExecutor {
  override suspend fun <T> execute(
    action: suspend () -> T,
  ): Result<T> {
    return NetworkRetrier(retryConfig, jitterFactorCalculator).doWithRetries(
      {
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
      },
    ) {
      if (it.isSuccess) {
        false
      } else {
        Log.d("Network call failed with ${it.exceptionOrNull()}")
        when (val exception = it.exceptionOrNull()) {
          is IOException,
          is VehicleTemporarilyUnavailableException -> true
          is UnrecoverableHttpException -> false
          is HttpException -> RETRYABLE_STATUS_CODES.contains(exception.code())
          is SignedMessagesFaultException -> RETRYABLE_MESSAGE_FAULTS.contains(exception.fault)
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
    val RETRYABLE_STATUS_CODES = listOf(408, HTTP_TOO_MANY_REQUESTS, 503, 504)
    val RETRYABLE_MESSAGE_FAULTS =
      listOf(
        MessageFault_E.MESSAGEFAULT_ERROR_BUSY,
        MessageFault_E.MESSAGEFAULT_ERROR_TIMEOUT,
        MessageFault_E.MESSAGEFAULT_ERROR_INVALID_SIGNATURE,
        MessageFault_E.MESSAGEFAULT_ERROR_INVALID_TOKEN_OR_COUNTER,
        MessageFault_E.MESSAGEFAULT_ERROR_INTERNAL,
        MessageFault_E.MESSAGEFAULT_ERROR_INCORRECT_EPOCH,
        MessageFault_E.MESSAGEFAULT_ERROR_TIME_EXPIRED,
        MessageFault_E.MESSAGEFAULT_ERROR_TIME_TO_LIVE_TOO_LONG,
      )
  }
}
