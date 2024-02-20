package com.boltfortesla.teslafleetsdk.net.api.energy.response

import com.google.gson.annotations.SerializedName

/** Response for getSiteInfo */
class SiteInfoResponse(
  val id: String,
  @SerializedName("site_name") val siteName: String,
  @SerializedName("backup_reserve_percent") val backupReservePercent: Int,
  @SerializedName("default_real_mode") val defaultRealMode: String,
  @SerializedName("installation_date") val installationDate: String,
  @SerializedName("user_settings") val userSettings: UserSettings,
  val components: Components,
  val version: String,
  @SerializedName("battery_count") val batteryCount: Int,
  @SerializedName("nameplate_power") val nameplatePower: Int,
  @SerializedName("nameplate_energy") val nameplateEnergy: Int,
  @SerializedName("installation_time_zone") val installationTimeZone: String,
  @SerializedName("max_site_meter_power_ac") val maxSiteMeterPowerAc: Int,
  @SerializedName("min_site_meter_power_ac") val minSiteMeterPowerAc: Double,
)

data class UserSettings(@SerializedName("storm_mode_enabled") val stormModeEnabled: Boolean)

data class Components(
  val solar: Boolean,
  @SerializedName("solar_type") val solarType: String,
  val battery: Boolean,
  val grid: Boolean,
  val backup: Boolean,
  @SerializedName("load_meter") val loadMeter: Boolean,
  @SerializedName("storm_mode_capable") val stormModeCapable: Boolean,
  @SerializedName("off_grid_vehicle_charging_reserve_supported")
  val offGridVehicleChargingReserveSupported: Boolean,
  @SerializedName("solar_value_enabled") val solarValueEnabled: Boolean,
  @SerializedName("set_islanding_mode_enabled") val setIslandingModeEnabled: Boolean,
  @SerializedName("battery_type") val batteryType: String,
  val configurable: Boolean
)
