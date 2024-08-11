package com.boltfortesla.teslafleetsdk.net.api.user.response

import com.google.gson.annotations.SerializedName

/** Response for getRegion */
data class RegionResponse(
  val region: String,
  @SerializedName("fleet_api_base_url") val fleetApiBaseUrl: String,
)
