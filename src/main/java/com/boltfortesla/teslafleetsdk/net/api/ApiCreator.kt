package com.boltfortesla.teslafleetsdk.net.api

import com.boltfortesla.teslafleetsdk.net.api.response.ProductTypeAdapterFactory
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

internal object ApiCreator {
  /**
   * Creates a Retrofit API of type [T]
   *
   * @param baseUrl the Base URL for requests made by this API
   * @param clientBuilder a pre-configured [OkHttpClient.Builder] that will be used when making
   *   network requests
   */
  inline fun <reified T> createApi(baseUrl: String, clientBuilder: OkHttpClient.Builder): T {
    val customGson = GsonBuilder().registerTypeAdapterFactory(ProductTypeAdapterFactory()).create()

    return Retrofit.Builder()
      .baseUrl(baseUrl)
      .client(clientBuilder.build())
      .addConverterFactory(GsonConverterFactory.create(customGson))
      .addConverterFactory(ScalarsConverterFactory.create())
      .addConverterFactory(GsonConverterFactory.create())
      .build()
      .create(T::class.java)
  }
}
