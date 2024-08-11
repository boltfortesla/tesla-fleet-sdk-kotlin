package com.boltfortesla.teslafleetsdk.net.api.user

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.Region
import com.boltfortesla.teslafleetsdk.net.api.ApiCreator
import com.boltfortesla.teslafleetsdk.net.api.FleetApiResponse
import com.boltfortesla.teslafleetsdk.net.api.user.response.BackupKeyResponse
import com.boltfortesla.teslafleetsdk.net.api.user.response.FeatureConfigResponse
import com.boltfortesla.teslafleetsdk.net.api.user.response.MeResponse
import com.boltfortesla.teslafleetsdk.net.api.user.response.Order
import com.boltfortesla.teslafleetsdk.net.api.user.response.RegionResponse
import okhttp3.OkHttpClient
import retrofit2.http.GET

/** Retrofit API for User Endpoints. */
internal interface UserApi {
  @GET("/api/1/users/backup_key") suspend fun backupKey(): FleetApiResponse<BackupKeyResponse>

  @GET("/api/1/users/feature_config")
  suspend fun featureConfig(): FleetApiResponse<FeatureConfigResponse>

  @GET("/api/1/users/me") suspend fun me(): FleetApiResponse<MeResponse>

  @GET("/api/1/users/orders") suspend fun orders(): FleetApiResponse<List<Order>>

  @GET("/api/1/users/region") suspend fun region(): FleetApiResponse<RegionResponse>
}

internal fun createUserApi(
  baseUrl: String = Region.NA_APAC.baseUrl,
  clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
) = ApiCreator.createApi<UserApi>(baseUrl, clientBuilder)
