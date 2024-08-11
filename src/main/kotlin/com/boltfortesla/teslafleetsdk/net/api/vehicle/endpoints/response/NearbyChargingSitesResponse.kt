package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response

import com.google.gson.annotations.SerializedName

data class NearbyChargingSitesResponse(
  @SerializedName("congestion_sync_time_utc_secs") val congestionSyncTimeUtcSecs: Long,
  @SerializedName("destination_charging") val destinationCharging: List<ChargingStation>,
  val superchargers: List<ChargingStation>,
  val timestamp: Long
) {
  data class ChargingStation(
    val location: Location,
    val name: String,
    val type: String,
    @SerializedName("distance_miles") val distanceMiles: Double,
    val amenities: String? = null,
    @SerializedName("available_stalls") val availableStalls: Int? = null,
    @SerializedName("total_stalls") val totalStalls: Int? = null,
    @SerializedName("site_closed") val siteClosed: Boolean? = null,
    @SerializedName("billing_info") val billingInfo: String? = null,
    val id: Int,
  ) {
    data class Location(val lat: Double, val long: Double)
  }
}
