package com.boltfortesla.teslafleetsdk.net.api.user

import com.boltfortesla.teslafleetsdk.net.api.FleetApiResponse
import com.boltfortesla.teslafleetsdk.net.api.user.response.BackupKeyResponse
import com.boltfortesla.teslafleetsdk.net.api.user.response.FeatureConfigResponse
import com.boltfortesla.teslafleetsdk.net.api.user.response.MeResponse
import com.boltfortesla.teslafleetsdk.net.api.user.response.Order
import com.boltfortesla.teslafleetsdk.net.api.user.response.RegionResponse

/**
 * API for User Endpoints.
 *
 * See https://developer.tesla.com/docs/fleet-api#user-endpoints for API documentation
 */
interface UserEndpoints {
  suspend fun getBackupKey(): Result<FleetApiResponse<BackupKeyResponse>>

  suspend fun getFeatureConfig(): Result<FleetApiResponse<FeatureConfigResponse>>

  suspend fun getMe(): Result<FleetApiResponse<MeResponse>>

  suspend fun getOrders(): Result<FleetApiResponse<List<Order>>>

  suspend fun getRegion(): Result<FleetApiResponse<RegionResponse>>
}
