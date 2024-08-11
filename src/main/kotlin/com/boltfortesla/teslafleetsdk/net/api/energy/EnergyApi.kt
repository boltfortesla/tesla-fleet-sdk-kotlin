package com.boltfortesla.teslafleetsdk.net.api.energy

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.Region
import com.boltfortesla.teslafleetsdk.net.api.ApiCreator
import com.boltfortesla.teslafleetsdk.net.api.FleetApiResponse
import com.boltfortesla.teslafleetsdk.net.api.energy.request.BackupRequest
import com.boltfortesla.teslafleetsdk.net.api.energy.request.GridImportExportRequest
import com.boltfortesla.teslafleetsdk.net.api.energy.request.OffGridVehicleChargingReserveRequest
import com.boltfortesla.teslafleetsdk.net.api.energy.request.OperationRequest
import com.boltfortesla.teslafleetsdk.net.api.energy.request.StormModeRequest
import com.boltfortesla.teslafleetsdk.net.api.energy.response.BackupHistoryResponse
import com.boltfortesla.teslafleetsdk.net.api.energy.response.ChargeHistoryResponse
import com.boltfortesla.teslafleetsdk.net.api.energy.response.EnergyCommandResponse
import com.boltfortesla.teslafleetsdk.net.api.energy.response.EnergyHistoryResponse
import com.boltfortesla.teslafleetsdk.net.api.energy.response.LiveStatusResponse
import com.boltfortesla.teslafleetsdk.net.api.energy.response.SiteInfoResponse
import okhttp3.OkHttpClient
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/** Retrofit API for Energy Endpoints. */
internal interface EnergyApi {
  @POST("/api/1/energy_sites/{energy_site_id}/backup")
  suspend fun backup(
    @Path(ENERGY_SITE_ID) energySiteId: Int,
    @Body request: BackupRequest,
  ): FleetApiResponse<EnergyCommandResponse>

  @GET("/api/1/energy_sites/{energy_site_id}/calendar_history?kind=backup")
  suspend fun backupHistory(
    @Path(ENERGY_SITE_ID) energySiteId: Int,
    @Query("start_date") startDate: String,
    @Query("end_date") endDate: String,
    @Query("period") period: String,
    @Query("time_zone") timeZone: String,
  ): FleetApiResponse<BackupHistoryResponse>

  @GET("/api/1/energy_sites/{energy_site_id}/telemetry_history?kind=charge")
  suspend fun chargeHistory(
    @Path(ENERGY_SITE_ID) energySiteId: Int,
    @Query("start_date") startDate: String,
    @Query("end_date") endDate: String,
    @Query("time_zone") timeZone: String,
  ): FleetApiResponse<ChargeHistoryResponse>

  @GET("/api/1/energy_sites/{energy_site_id}/calendar_history?kind=energy")
  suspend fun energyHistory(
    @Path(ENERGY_SITE_ID) energySiteId: Int,
    @Query("start_date") startDate: String,
    @Query("end_date") endDate: String,
    @Query("period") period: String,
    @Query("time_zone") timeZone: String,
  ): FleetApiResponse<EnergyHistoryResponse>

  @POST("/api/1/energy_sites/{energy_site_id}/grid_import_export")
  suspend fun gridImportExport(
    @Path(ENERGY_SITE_ID) energySiteId: Int,
    @Body request: GridImportExportRequest,
  ): FleetApiResponse<EnergyCommandResponse>

  @GET("/api/1/energy_sites/{energy_site_id}/live_status")
  suspend fun liveStatus(
    @Path(ENERGY_SITE_ID) energySiteId: Int
  ): FleetApiResponse<LiveStatusResponse>

  @POST("/api/1/energy_sites/{energy_site_id}/off_grid_vehicle_charging_reserve")
  suspend fun offGridVehicleChargingReserve(
    @Path(ENERGY_SITE_ID) energySiteId: Int,
    @Body request: OffGridVehicleChargingReserveRequest,
  ): FleetApiResponse<EnergyCommandResponse>

  @POST("/api/1/energy_sites/{energy_site_id}/operation")
  suspend fun operation(
    @Path(ENERGY_SITE_ID) energySiteId: Int,
    @Body request: OperationRequest,
  ): FleetApiResponse<EnergyCommandResponse>

  @GET("/api/1/energy_sites/{energy_site_id}/site_info")
  suspend fun siteInfo(@Path(ENERGY_SITE_ID) energySiteId: Int): FleetApiResponse<SiteInfoResponse>

  @POST("/api/1/energy_sites/{energy_site_id}/storm_mode")
  suspend fun stormMode(
    @Path(ENERGY_SITE_ID) energySiteId: Int,
    @Body request: StormModeRequest,
  ): FleetApiResponse<EnergyCommandResponse>

  private companion object {
    private const val ENERGY_SITE_ID = "energy_site_id"
  }
}

internal fun createEnergyApi(
  baseUrl: String = Region.NA_APAC.baseUrl,
  clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
) = ApiCreator.createApi<EnergyApi>(baseUrl, clientBuilder)
