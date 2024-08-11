package com.boltfortesla.teslafleetsdk.net.api.charging

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.Region
import com.boltfortesla.teslafleetsdk.TeslaFleetApi.RetryConfig
import com.boltfortesla.teslafleetsdk.net.JitterFactorCalculator
import com.boltfortesla.teslafleetsdk.net.NetworkExecutorImpl
import okhttp3.OkHttpClient

/** Factory for [ChargingEndpoints]. */
internal class ChargingEndpointsFactory(
  private val jitterFactorCalculator: JitterFactorCalculator
) {
  /**
   * Creates a [ChargingEndpoints] instance.
   *
   * @param region the [Region] the API calls should be made against
   * @param retryConfig a [RetryConfig]
   * @param clientBuilder a pre-configured [OkHttpClient.Builder] that will be used when making
   *   network requests
   */
  fun create(
    region: Region,
    retryConfig: RetryConfig,
    clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
  ): ChargingEndpoints {
    return ChargingEndpointsImpl(
      createChargingApi(region.baseUrl, clientBuilder),
      NetworkExecutorImpl(retryConfig, jitterFactorCalculator),
    )
  }
}
