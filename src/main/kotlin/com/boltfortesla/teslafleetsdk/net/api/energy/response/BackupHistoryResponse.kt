package com.boltfortesla.teslafleetsdk.net.api.energy.response

import com.google.gson.annotations.SerializedName

/** Response for getBackupHistory */
data class BackupHistoryResponse(
  val events: List<Event>,
  @SerializedName("total_events") val totalEvents: Int,
) {
  data class Event(val timestamp: String, val duration: Long)
}
