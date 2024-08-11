package com.boltfortesla.teslafleetsdk.net.api.charging

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.Region
import com.boltfortesla.teslafleetsdk.net.api.ApiCreator
import com.boltfortesla.teslafleetsdk.net.api.charging.response.ChargingHistoryResponse
import com.boltfortesla.teslafleetsdk.net.api.charging.response.ChargingSessionsResponse
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** Retrofit API for Charging Endpoints. */
internal interface ChargingApi {
  @GET("/api/1/dx/charging/history")
  suspend fun chargingHistory(
    @Query("vin") vin: String?,
    @Query("startTime") startTime: String?,
    @Query("endTime") endTime: String?,
    @Query("pageNo") pageNumber: Int?,
    @Query("pageSize") pageSize: Int?,
    @Query("sortBy") sortBy: String?,
    @Query("sortOrder") sortOrder: String?,
  ): ChargingHistoryResponse

  @GET("/api/1/dx/charging/invoice/{id}")
  suspend fun chargingInvoice(@Path("id") invoiceId: String): ResponseBody

  @GET("/api/1/dx/charging/sessions")
  suspend fun chargingSessions(
    @Query("vin") vin: String?,
    @Query("date_from") dateFrom: String?,
    @Query("date_to") dateTo: String?,
    @Query("limit") limit: Int?,
    @Query("offset") offset: Int?,
  ): ChargingSessionsResponse
}

internal fun createChargingApi(
  baseUrl: String = Region.NA_APAC.baseUrl,
  clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
) = ApiCreator.createApi<ChargingApi>(baseUrl, clientBuilder)
