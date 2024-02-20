package com.boltfortesla.teslafleetsdk.net.api.user.response

import com.google.gson.annotations.SerializedName

/** Response for getFeatureConfig */
data class FeatureConfigResponse(
  val signaling: Signaling,
  @SerializedName("auth_rejection") val authRejection: AuthRejection
) {
  data class Signaling(
    val enabled: Boolean,
    @SerializedName("subscribe_connectivity") val subscribeConnectivity: Boolean,
    @SerializedName("use_auth_token") val useAuthToken: Boolean
  )

  data class AuthRejection(
    val enabled: Boolean,
    @SerializedName("min_version") val minVersion: Int
  )
}
