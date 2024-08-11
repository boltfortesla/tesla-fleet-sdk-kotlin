package com.boltfortesla.teslafleetsdk.net.api.energy.response

import com.google.gson.annotations.SerializedName

/** Response for getLiveStatus */
data class LiveStatusResponse(
  @SerializedName("solar_power") val solarPower: Int,
  @SerializedName("energy_left") val energyLeft: Double,
  @SerializedName("total_pack_energy") val totalPackEnergy: Int,
  @SerializedName("percentage_charged") val percentageCharged: Double,
  @SerializedName("backup_capable") val backupCapable: Boolean,
  @SerializedName("battery_power") val batteryPower: Int,
  @SerializedName("load_power") val loadPower: Int,
  @SerializedName("grid_status") val gridStatus: String,
  @SerializedName("grid_power") val gridPower: Int,
  @SerializedName("island_status") val islandStatus: String,
  @SerializedName("storm_mode_active") val stormModeActive: Boolean,
  val timestamp: String,
)
