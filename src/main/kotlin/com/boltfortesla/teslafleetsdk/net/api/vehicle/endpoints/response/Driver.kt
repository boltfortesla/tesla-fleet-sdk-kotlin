package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response

import com.google.gson.annotations.SerializedName

data class Driver(
  @SerializedName("my_tesla_unique_id") val myTeslaUniqueId: Long,
  @SerializedName("user_id") val userId: Long,
  @SerializedName("user_id_s") val userIdS: String,
  @SerializedName("driver_first_name") val driverFirstName: String,
  @SerializedName("driver_last_name") val driverLastName: String,
  @SerializedName("granular_access") val granularAccess: GranularAccess,
  @SerializedName("active_pubkeys") val activePubkeys: List<String>,
  @SerializedName("public_key") val publicKey: String,
) {
  data class GranularAccess(@SerializedName("hide_private") val hidePrivate: Boolean)
}
