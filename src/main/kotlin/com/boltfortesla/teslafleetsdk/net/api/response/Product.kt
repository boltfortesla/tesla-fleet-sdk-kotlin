package com.boltfortesla.teslafleetsdk.net.api.response

import com.google.gson.annotations.SerializedName

/** Response for getProducts */
sealed interface Product {
  data class Vehicle(
    val id: Long,
    @SerializedName("user_id") val userId: Long?,
    @SerializedName("vehicle_id") val vehicleId: Long,
    val vin: String,
    val color: String?,
    @SerializedName("access_type") val accessType: String,
    @SerializedName("display_name") val displayName: String?,
    @SerializedName("option_codes") val optionCodes: String?,
    @SerializedName("cached_data") val cachedData: String?,
    @SerializedName("granular_access") val granularAccess: GranularAccess,
    val tokens: List<String>?,
    val state: String?,
    @SerializedName("in_service") val inService: Boolean,
    @SerializedName("id_s") val idString: String,
    @SerializedName("calendar_enabled") val calendarEnabled: Boolean,
    @SerializedName("api_version") val apiVersion: String?,
    @SerializedName("backseat_token") val backseatToken: String?,
    @SerializedName("backseat_token_updated_at") val backseatTokenUpdatedAt: String?,
    @SerializedName("command_signing") val commandSigning: String?,
  ) : Product {
    data class GranularAccess(@SerializedName("hide_private") val hidePrivate: Boolean)
  }

  data class EnergySite(
    @SerializedName("energy_site_id") val energySiteId: Long,
    @SerializedName("resource_type") val resourceType: String,
    @SerializedName("site_name") val siteName: String,
    val id: String,
    @SerializedName("gateway_id") val gatewayId: String,
    @SerializedName("energy_left") val energyLeft: Int,
    @SerializedName("total_pack_energy") val totalPackEnergy: Int,
    @SerializedName("percentage_charged") val percentageCharged: Int,
    @SerializedName("battery_power") val batteryPower: Int,
  ) : Product
}
