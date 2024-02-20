package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.Region
import com.boltfortesla.teslafleetsdk.net.api.ApiCreator
import com.boltfortesla.teslafleetsdk.net.api.FleetApiResponse
import com.boltfortesla.teslafleetsdk.net.api.response.Product.Vehicle
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.request.FleetStatusRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.request.FleetTelemetryConfigRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.request.RedeemInviteRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.request.SignedCommandRequest
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
import okhttp3.OkHttpClient
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/** Retrofit API for Vehicle Endpoints. */
internal interface VehicleEndpointsApi {
  @GET("/api/1/vehicles/{vehicle_tag}/drivers")
  suspend fun getDrivers(@Path(VEHICLE_TAG) vehicleTag: String): FleetApiResponse<List<Driver>>

  @DELETE("/api/1/vehicles/{vehicle_tag}/drivers")
  suspend fun removeDriver(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Query("share_user_id") userId: Int
  ): FleetApiResponse<String>

  @GET("/api/1/dx/vehicles/subscriptions/eligibility")
  suspend fun getEligibleSubscriptions(@Query("vin") vin: String): EligibleSubscriptionsResponse

  @GET("/api/1/dx/vehicles/upgrades/eligibility")
  suspend fun getEligibleUpgrades(@Query("vin") vin: String): EligibleUpgradesResponse

  @POST("/api/1/vehicles/fleet_status")
  suspend fun fleetStatus(@Body request: FleetStatusRequest): FleetApiResponse<FleetStatusResponse>

  @POST("/api/1/vehicles/fleet_telemetry_config")
  suspend fun createFleetTelemetryConfig(
    @Body request: FleetTelemetryConfigRequest
  ): FleetApiResponse<FleetTelemetryModifyConfigResponse>

  @DELETE("/api/1/vehicles/{vehicle_tag}/fleet_telemetry_config")
  suspend fun deleteFleetTelemetryConfig(
    @Path(VEHICLE_TAG) vehicleTag: String
  ): FleetApiResponse<FleetTelemetryModifyConfigResponse>

  @GET("/api/1/vehicles/{vehicle_tag}/fleet_telemetry_config")
  suspend fun getFleetTelemetryConfig(
    @Path(VEHICLE_TAG) vehicleTag: String
  ): FleetApiResponse<FleetTelemetryResponse>

  @GET("/api/1/vehicles")
  suspend fun listVehicles(
    @Query("page") page: Int?,
    @Query("per_page") perPage: Int?
  ): FleetApiResponse<List<Vehicle>>

  @GET("/api/1/vehicles/{vehicle_tag}/mobile_enabled")
  suspend fun mobileEnabled(@Path(VEHICLE_TAG) vehicleTag: String): FleetApiResponse<Boolean>

  @GET("/api/1/vehicles/{vehicle_tag}/nearby_charging_sites")
  suspend fun nearbyChargingSites(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Query("count") count: Int?,
    @Query("radius") radius: Int?,
    @Query("detail") detail: Boolean?
  ): FleetApiResponse<NearbyChargingSitesResponse>

  @GET("/api/1/dx/vehicles/options")
  suspend fun getOptions(@Query("vin") vin: String): OptionsResponse

  @GET("/api/1/vehicles/{vehicle_tag}/recent_alerts")
  suspend fun getRecentAlerts(
    @Path(VEHICLE_TAG) vehicleTag: String
  ): FleetApiResponse<RecentAlertsResponse>

  @GET("/api/1/vehicles/{vehicle_tag}/release_notes")
  suspend fun getReleaseNotes(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Query("staged") staged: Boolean,
    @Query("language") language: Int
  ): FleetApiResponse<ReleaseNotesResponse>

  @GET("/api/1/vehicles/{vehicle_tag}/service_data")
  suspend fun getServiceData(
    @Path(VEHICLE_TAG) vehicleTag: String
  ): FleetApiResponse<ServiceDataResponse>

  @GET("/api/1/vehicles/{vehicle_tag}/invitations")
  suspend fun getShareInvites(
    @Path(VEHICLE_TAG) vehicleTag: String
  ): FleetApiResponse<List<Invitation>>

  @POST("/api/1/vehicles/{vehicle_tag}/invitations")
  suspend fun createShareInvite(@Path(VEHICLE_TAG) vehicleTag: String): FleetApiResponse<Invitation>

  @POST("/api/1/invitations/redeem")
  suspend fun redeemShareInvite(
    @Body request: RedeemInviteRequest
  ): FleetApiResponse<RedeemInviteResponse>

  @POST("/api/1/vehicles/{vehicle_tag}/invitations/{id}/revoke")
  suspend fun revokeShareInvite(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Path("id") id: String
  ): FleetApiResponse<Boolean?>

  @POST("/api/1/vehicles/{vehicle_tag}/signed_command")
  suspend fun sendSignedCommand(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: SignedCommandRequest
  ): SignedCommandResponse

  @GET("/api/1/subscriptions")
  suspend fun getSubscriptions(
    @Query("device_token") deviceToken: String,
    @Query("device_type") deviceType: String
  ): FleetApiResponse<List<Int>?>

  @POST("/api/1/subscriptions")
  suspend fun setSubscriptions(
    @Query("device_token") deviceToken: String,
    @Query("device_type") deviceType: String
  ): FleetApiResponse<List<Int>?>

  @GET("/api/1/vehicles/{vehicle_tag}")
  suspend fun getVehicle(@Path(VEHICLE_TAG) vehicleTag: String): FleetApiResponse<Vehicle>

  @GET("/api/1/vehicles/{vehicle_tag}/vehicle_data")
  suspend fun getVehicleData(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Query("endpoints") endpoints: String
  ): FleetApiResponse<VehicleDataResponse>

  @GET("/api/1/vehicle_subscriptions")
  suspend fun getVehicleSubscriptions(
    @Query("device_token") deviceToken: String,
    @Query("device_type") deviceType: String
  ): FleetApiResponse<List<Int>?>

  @POST("/api/1/vehicle_subscriptions")
  suspend fun setVehicleSubscriptions(
    @Query("device_token") deviceToken: String,
    @Query("device_type") deviceType: String
  ): FleetApiResponse<List<Int>?>

  @POST("/api/1/vehicles/{vehicle_tag}/wake_up")
  suspend fun wakeUpVehicle(@Path(VEHICLE_TAG) vehicleTag: String): FleetApiResponse<Vehicle>

  @GET("/api/1/dx/warranty/details")
  suspend fun getWarrantyDetails(@Query("vin") vin: String): WarrantyDetailsResponse

  private companion object {
    const val VEHICLE_TAG = "vehicle_tag"
  }
}

internal fun createVehicleEndpointsApi(
  baseUrl: String = Region.NA_APAC.baseUrl,
  clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
) = ApiCreator.createApi<VehicleEndpointsApi>(baseUrl, clientBuilder)
