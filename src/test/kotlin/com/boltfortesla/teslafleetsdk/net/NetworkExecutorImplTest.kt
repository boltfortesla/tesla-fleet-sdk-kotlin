package com.boltfortesla.teslafleetsdk.net

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.RetryConfig
import com.boltfortesla.teslafleetsdk.net.NetworkExecutorImpl.Companion.RETRYABLE_STATUS_CODES
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.VehicleTemporarilyUnavailableException
import com.google.common.truth.Truth.assertThat
import java.io.IOException
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class NetworkExecutorImplTest {
  private val jitterFactorCalculator = JitterFactorCalculatorImpl()

  @Test
  fun execute_success_returnsSuccessResponse() = runTest {
    val networkExecutorImpl = NetworkExecutorImpl(RetryConfig(), jitterFactorCalculator)

    val response = networkExecutorImpl.execute { "result" }

    assertThat(response).isEqualTo(Result.success("result"))
  }

  @Test
  fun execute_exceptionThrown_returnsFailureResponse() = runTest {
    val networkExecutorImpl = NetworkExecutorImpl(RetryConfig(), jitterFactorCalculator)
    val exception = IllegalStateException()

    val response = networkExecutorImpl.execute { throw exception }

    assertThat(response).isEqualTo(Result.failure<Any>(exception))
  }

  @Test
  fun execute_success_neverRetries() = runTest {
    val networkExecutorImpl = NetworkExecutorImpl(RetryConfig(), jitterFactorCalculator)
    var executionCount = 0

    networkExecutorImpl.execute {
      executionCount++
      "result"
    }

    assertThat(executionCount).isEqualTo(1)
  }

  @Test
  fun execute_retryableFailure_thenSuccess_retriesOnce() = runTest {
    val networkExecutorImpl = NetworkExecutorImpl(RetryConfig(), jitterFactorCalculator)
    var executionCount = 0

    networkExecutorImpl.execute {
      if (executionCount++ == 0) {
        throw IOException()
      } else {
        ""
      }
    }

    assertThat(executionCount).isEqualTo(2)
  }

  @Test
  fun execute_errorResultException_thenSuccess_retriesOnce() = runTest {
    val networkExecutorImpl = NetworkExecutorImpl(RetryConfig(), jitterFactorCalculator)
    var executionCount = 0

    networkExecutorImpl.execute {
      if (executionCount++ == 0) {
        throw VehicleTemporarilyUnavailableException()
      } else {
        ""
      }
    }

    assertThat(executionCount).isEqualTo(2)
  }

  @Test
  fun fails_retryableStatusCodes_retries() = runTest {
    val networkExecutorImpl = NetworkExecutorImpl(RetryConfig(), jitterFactorCalculator)
    var executionCount = 0

    val response =
      networkExecutorImpl.execute {
        val code = RETRYABLE_STATUS_CODES.getOrNull(executionCount++) ?: 500
        throw HttpException(Response.error<Any>(code, "".toResponseBody()))
      }

    assertThat(executionCount).isEqualTo(RETRYABLE_STATUS_CODES.size + 1)
    assertThat(response.isFailure).isTrue()
    val exception = response.exceptionOrNull() as HttpException
    assertThat(exception.code()).isEqualTo(500)
  }

  @Test
  fun fails_retryable_then_fatalStatusCode_retriesOnce() = runTest {
    val networkExecutorImpl = NetworkExecutorImpl(RetryConfig(), jitterFactorCalculator)
    var executionCount = 0

    val response =
      networkExecutorImpl.execute {
        val code = if (executionCount++ == 0) 408 else 500
        throw HttpException(Response.error<Any>(code, "".toResponseBody()))
      }

    assertThat(executionCount).isEqualTo(2)
    assertThat(response.isFailure).isTrue()
    val exception = response.exceptionOrNull() as HttpException
    assertThat(exception.code()).isEqualTo(500)
  }
}
