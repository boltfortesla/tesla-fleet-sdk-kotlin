package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response

import com.google.gson.annotations.SerializedName

data class RecentAlertsResponse(@SerializedName("recent_alerts") val alerts: List<Alert>) {
  data class Alert(
    val name: String,
    val time: String,
    val audience: List<String>,
    @SerializedName("user_text") val userText: String
  )
}
