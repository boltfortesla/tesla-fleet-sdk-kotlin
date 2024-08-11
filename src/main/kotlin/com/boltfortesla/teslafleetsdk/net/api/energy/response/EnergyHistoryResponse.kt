package com.boltfortesla.teslafleetsdk.net.api.energy.response

import com.google.gson.annotations.SerializedName

/** Response for getEnergyHistory */
data class EnergyHistoryResponse(
  val period: String,
  @SerializedName("time_series") val timeSeries: List<TimeSeriesEntry>
) {
  data class TimeSeriesEntry(
    val timestamp: String,
    @SerializedName("solar_energy_exported") val solarEnergyExported: Int,
    @SerializedName("generator_energy_exported") val generatorEnergyExported: Int,
    @SerializedName("grid_energy_imported") val gridEnergyImported: Int,
    @SerializedName("grid_services_energy_imported") val gridServicesEnergyImported: Double,
    @SerializedName("grid_services_energy_exported") val gridServicesEnergyExported: Double,
    @SerializedName("grid_energy_exported_from_solar") val gridEnergyExportedFromSolar: Int,
    @SerializedName("grid_energy_exported_from_generator") val gridEnergyExportedFromGenerator: Int,
    @SerializedName("grid_energy_exported_from_battery") val gridEnergyExportedFromBattery: Int,
    @SerializedName("battery_energy_exported") val batteryEnergyExported: Int,
    @SerializedName("battery_energy_imported_from_grid") val batteryEnergyImportedFromGrid: Int,
    @SerializedName("battery_energy_imported_from_solar") val batteryEnergyImportedFromSolar: Int,
    @SerializedName("battery_energy_imported_from_generator")
    val batteryEnergyImportedFromGenerator: Int,
    @SerializedName("consumer_energy_imported_from_grid") val consumerEnergyImportedFromGrid: Int,
    @SerializedName("consumer_energy_imported_from_solar") val consumerEnergyImportedFromSolar: Int,
    @SerializedName("consumer_energy_imported_from_battery")
    val consumerEnergyImportedFromBattery: Int,
    @SerializedName("consumer_energy_imported_from_generator")
    val consumerEnergyImportedFromGenerator: Int
  )
}
