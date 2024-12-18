package com.boltfortesla.teslafleetsdk.net.api

import com.boltfortesla.teslafleetsdk.net.api.response.Product

/**
 * API for the base Fleet API.
 *
 * See https://developer.tesla.com/docs/fleet-api#products for API documentation
 */
interface FleetApiEndpoints {
  suspend fun getProducts(): Result<FleetApiResponse<List<Product>>>
}
