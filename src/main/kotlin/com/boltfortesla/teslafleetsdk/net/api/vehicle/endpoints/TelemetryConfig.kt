package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints

import com.google.gson.annotations.SerializedName

data class TelemetryConfig(
  val hostname: String,
  val ca: String,
  val exp: Long,
  val fields: Map<String, FieldConfig>,
  @SerializedName("alert_types") val alertTypes: List<String>,
) {
  data class FieldConfig(@SerializedName("interval_seconds") val intervalSeconds: Int)
}
