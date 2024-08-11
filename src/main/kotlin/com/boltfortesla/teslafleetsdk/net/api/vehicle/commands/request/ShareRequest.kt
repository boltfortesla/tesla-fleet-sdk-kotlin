package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request

import com.google.gson.annotations.SerializedName

internal data class ShareRequest(
  val type: String = "share_ext_content_raw",
  val value: Value,
  val locale: String,
  @SerializedName("timestamp_ms") val timestampMs: String
) {
  data class Value(@SerializedName("android.intent.extra.TEXT") val text: String)
}
