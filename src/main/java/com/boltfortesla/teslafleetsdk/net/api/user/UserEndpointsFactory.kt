package com.boltfortesla.teslafleetsdk.net.api.user

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.Region
import com.boltfortesla.teslafleetsdk.TeslaFleetApi.RetryConfig
import com.boltfortesla.teslafleetsdk.net.JitterFactorCalculator
import com.boltfortesla.teslafleetsdk.net.NetworkExecutorImpl
import okhttp3.OkHttpClient

/** Factory for [UserEndpoints]. */
internal class UserEndpointsFactory(
  private val jitterFactorCalculator: JitterFactorCalculator,
) {
  /**
   * Creates a [UserEndpoints] instance.
   *
   * @param region the [Region] the API calls should be made against
   * @param retryConfig a [RetryConfig]
   * @param clientBuilder a pre-configured [OkHttpClient.Builder] that will be used when making
   *   network requests
   */
  fun create(
    region: Region,
    retryConfig: RetryConfig,
    clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
  ): UserEndpoints {
    return UserEndpointsImpl(
      createUserApi(region.baseUrl, clientBuilder),
      NetworkExecutorImpl(retryConfig, jitterFactorCalculator)
    )
  }
}
