package com.boltfortesla.teslafleetsdk.net.api

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.Region
import com.boltfortesla.teslafleetsdk.TeslaFleetApi.RetryConfig
import com.boltfortesla.teslafleetsdk.net.JitterFactorCalculator
import com.boltfortesla.teslafleetsdk.net.NetworkExecutorImpl
import okhttp3.OkHttpClient

/** Factory for [FleetApiEndpoints]. */
internal class FleetApiEndpointsFactory(
  private val jitterFactorCalculator: JitterFactorCalculator,
) {
  /**
   * Creates a [FleetApiEndpoints] instance.
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
  ): FleetApiEndpoints {
    return FleetApiEndpointsImpl(
      createFleetApi(region.authBaseUrl, clientBuilder),
      NetworkExecutorImpl(retryConfig, jitterFactorCalculator)
    )
  }
}
