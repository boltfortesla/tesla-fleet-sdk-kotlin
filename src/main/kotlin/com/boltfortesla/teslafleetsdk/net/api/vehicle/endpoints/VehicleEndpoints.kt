package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints

import com.boltfortesla.teslafleetsdk.net.api.FleetApiResponse
import com.boltfortesla.teslafleetsdk.net.api.response.Product.Vehicle
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.Driver
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.EligibleSubscriptionsResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.EligibleUpgradesResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.FleetStatusResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.FleetTelemetryModifyConfigResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.FleetTelemetryResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.Invitation
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.NearbyChargingSitesResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.OptionsResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.RecentAlertsResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.RedeemInviteResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.ReleaseNotesResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.ServiceDataResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.SignedCommandResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.VehicleDataResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.WarrantyDetailsResponse

/**
 * API for Vehicle Endpoints.
 *
 * See https://developer.tesla.com/docs/fleet-api#vehicle-endpoints for API documentation
 */
interface VehicleEndpoints {
  suspend fun getDrivers(): Result<FleetApiResponse<List<Driver>>>

  suspend fun removeDrivers(userId: Int): Result<FleetApiResponse<String>>

  suspend fun getEligibleSubscriptions(): Result<EligibleSubscriptionsResponse>

  suspend fun getEligibleUpgrades(): Result<EligibleUpgradesResponse>

  suspend fun getFleetStatus(vins: List<String>): Result<FleetApiResponse<FleetStatusResponse>>

  suspend fun createFleetTelemetryConfig(
    vins: List<String>,
    hostname: String,
    ca: String,
    fields: Map<String, Int>,
    alertTypes: List<String>,
    expirationTimeSeconds: Long
  ): Result<FleetApiResponse<FleetTelemetryModifyConfigResponse>>

  suspend fun deleteFleetTelemetryConfig():
    Result<FleetApiResponse<FleetTelemetryModifyConfigResponse>>

  suspend fun getFleetTelemetryConfig(): Result<FleetApiResponse<FleetTelemetryResponse>>

  suspend fun listVehicles(
    page: Int? = null,
    perPage: Int? = null
  ): Result<FleetApiResponse<List<Vehicle>>>

  suspend fun isMobileEnabled(): Result<FleetApiResponse<Boolean>>

  suspend fun getNearbyChargingSites(
    count: Int?,
    radius: Int?,
    detail: Boolean?
  ): Result<FleetApiResponse<NearbyChargingSitesResponse>>

  suspend fun getOptions(): Result<OptionsResponse>

  suspend fun getRecentAlerts(): Result<FleetApiResponse<RecentAlertsResponse>>

  suspend fun getReleaseNotes(
    staged: Boolean,
    language: Int
  ): Result<FleetApiResponse<ReleaseNotesResponse>>

  suspend fun getServiceData(): Result<FleetApiResponse<ServiceDataResponse>>

  suspend fun shareInvites(): Result<FleetApiResponse<List<Invitation>>>

  suspend fun createShareInvite(): Result<FleetApiResponse<Invitation>>

  suspend fun redeemShareInvite(code: String): Result<FleetApiResponse<RedeemInviteResponse>>

  suspend fun revokeShareInvites(id: String): Result<FleetApiResponse<Boolean?>>

  suspend fun signedCommand(routableMessage: String): Result<SignedCommandResponse>

  suspend fun getSubscriptions(
    deviceToken: String,
    deviceType: String
  ): Result<FleetApiResponse<List<Int>?>>

  suspend fun setSubscriptions(
    deviceToken: String,
    deviceType: String
  ): Result<FleetApiResponse<List<Int>?>>

  suspend fun getVehicle(): Result<FleetApiResponse<Vehicle>>

  suspend fun getVehicleData(
    endpoints: List<Endpoint>
  ): Result<FleetApiResponse<VehicleDataResponse>>

  suspend fun getVehicleSubscriptions(
    deviceToken: String,
    deviceType: String
  ): Result<FleetApiResponse<List<Int>?>>

  suspend fun setVehicleSubscriptions(
    deviceToken: String,
    deviceType: String
  ): Result<FleetApiResponse<List<Int>?>>

  suspend fun wakeUp(): Result<FleetApiResponse<Vehicle>>

  suspend fun getWarrantyDetails(): Result<WarrantyDetailsResponse>

  enum class Endpoint {
    CHARGE_STATE,
    CLIMATE_STATE,
    CLOSURES_STATE,
    DRIVE_STATE,
    GUI_SETTINGS,
    LOCATION_DATA,
    VEHICLE_CONFIG,
    VEHICLE_STATE,
    VEHICLE_DATA_COMBO
  }
}
