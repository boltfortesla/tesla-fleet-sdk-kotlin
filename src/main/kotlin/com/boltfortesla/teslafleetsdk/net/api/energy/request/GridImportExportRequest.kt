package com.boltfortesla.teslafleetsdk.net.api.energy.request

import com.google.gson.annotations.SerializedName

/** Request for setGridImportExport */
internal data class GridImportExportRequest(
  @SerializedName("disallow_charge_from_grid_with_solar_installed")
  val disallowChargeFromGridWithSolarInstalled: Boolean,
  @SerializedName("customer_preferred_export_rule") val customerPreferredExportRule: String,
)
