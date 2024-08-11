package com.boltfortesla.teslafleetsdk.net.api

import com.boltfortesla.teslafleetsdk.net.NetworkExecutor

/** Implementation of [FleetApiEndpoints] */
internal class FleetApiEndpointsImpl(
  private val fleetApi: FleetApi,
  private val networkExecutor: NetworkExecutor
) : FleetApiEndpoints {
  override suspend fun getProducts() = networkExecutor.execute { fleetApi.products() }
}
