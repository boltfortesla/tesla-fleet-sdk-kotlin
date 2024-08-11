package com.boltfortesla.teslafleetsdk.net.api.energy

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.Region
import com.boltfortesla.teslafleetsdk.TeslaFleetApi.RetryConfig
import com.boltfortesla.teslafleetsdk.net.JitterFactorCalculator
import com.boltfortesla.teslafleetsdk.net.NetworkExecutorImpl
import com.boltfortesla.teslafleetsdk.net.api.FleetApiEndpoints
import okhttp3.OkHttpClient

/** Factory for [EnergyEndpoints]. */
internal class EnergyEndpointsFactory(private val jitterFactorCalculator: JitterFactorCalculator) {
  /**
   * Creates a [EnergyEndpoints] instance.
   *
   * @param energySiteId the ID of the energy site to execute API requests for. This ID can be found
   *   by calling [FleetApiEndpoints.getProducts]
   * @param region the [Region] the API calls should be made against
   * @param retryConfig a [RetryConfig]
   * @param clientBuilder a pre-configured [OkHttpClient.Builder] that will be used when making
   *   network requests
   */
  fun create(
    energySiteId: Int,
    region: Region,
    retryConfig: RetryConfig,
    clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
  ): EnergyEndpoints {
    return EnergyEndpointsImpl(
      energySiteId,
      createEnergyApi(region.baseUrl, clientBuilder),
      NetworkExecutorImpl(retryConfig, jitterFactorCalculator),
    )
  }
}
