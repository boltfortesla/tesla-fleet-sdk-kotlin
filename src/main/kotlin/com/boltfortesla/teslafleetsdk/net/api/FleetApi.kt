package com.boltfortesla.teslafleetsdk.net.api

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.Region
import com.boltfortesla.teslafleetsdk.net.api.response.Product
import okhttp3.OkHttpClient
import retrofit2.http.GET

/** Retrofit API Declaration for Vehicle Commands */
internal interface FleetApi {
  @GET("api/1/products") suspend fun products(): List<Product>
}

internal fun createFleetApi(
  baseUrl: String = Region.NA_APAC.baseUrl,
  clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
) = ApiCreator.createApi<FleetApi>(baseUrl, clientBuilder)
