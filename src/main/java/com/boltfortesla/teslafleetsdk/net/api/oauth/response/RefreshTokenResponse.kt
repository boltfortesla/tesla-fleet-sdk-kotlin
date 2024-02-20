package com.boltfortesla.teslafleetsdk.net.api.oauth.response

import com.google.gson.annotations.SerializedName

/** Response for refreshToken */
data class RefreshTokenResponse(
  @SerializedName("access_token") val accessToken: String,
  @SerializedName("token_type") val tokenType: String,
  @SerializedName("expires_in") val expiresIn: Int,
  @SerializedName("refresh_token") val refreshToken: String,
  @SerializedName("id_token") val idToken: String,
  val state: String
)
