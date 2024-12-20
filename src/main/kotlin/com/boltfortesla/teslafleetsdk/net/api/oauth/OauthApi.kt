package com.boltfortesla.teslafleetsdk.net.api.oauth

import com.boltfortesla.teslafleetsdk.net.api.ApiCreator
import com.boltfortesla.teslafleetsdk.net.api.oauth.response.RefreshTokenResponse
import okhttp3.OkHttpClient
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/** Retrofit API for Oauth endpoints. */
internal interface OauthApi {
  @FormUrlEncoded
  @POST("oauth2/v3/token")
  suspend fun refreshToken(
    @Field("client_id") clientId: String,
    @Field("refresh_token") refreshToken: String,
    @Field("grant_type") grantType: String = "refresh_token",
  ): RefreshTokenResponse
}

internal fun createOauthApi(
  baseUrl: String = "https://fleet-auth.prd.vn.cloud.tesla.com",
  clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
) = ApiCreator.createApi<OauthApi>(baseUrl, clientBuilder)
