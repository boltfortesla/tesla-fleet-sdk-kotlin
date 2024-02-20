package com.boltfortesla.teslafleetsdk.net.api.energy.response

import com.google.gson.annotations.SerializedName

/** Response for getChargeHistory */
data class ChargeHistoryResponse(
  @SerializedName("charge_history") val chargeHistories: List<ChargeHistory>
)

data class ChargeHistory(
  @SerializedName("charge_start_time") val chargeStartTime: ChargeStartTime,
  @SerializedName("charge_duration") val chargeDuration: ChargeDuration,
  @SerializedName("energy_added_wh") val totalEvents: Long
) {
  data class ChargeStartTime(val seconds: Long)

  data class ChargeDuration(val seconds: Long)
}
