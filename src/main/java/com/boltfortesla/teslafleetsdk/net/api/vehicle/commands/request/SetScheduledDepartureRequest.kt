package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request

import com.google.gson.annotations.SerializedName

internal data class SetScheduledDepartureRequest(
  val enable: Boolean,
  @SerializedName("departure_time") val time: Int?,
  @SerializedName("preconditioning_enabled") val preconditioningEnabled: Boolean?,
  @SerializedName("preconditioning_weekdays_only") val preconditioningWeekdaysOnly: Boolean?,
  @SerializedName("off_peak_charging_enabled") val offPeakChargingEnabled: Boolean?,
  @SerializedName("off_peak_charging_weekdays_only") val offPeakChargingWeekdaysOnly: Boolean?,
  @SerializedName("end_off_peak_time") val endOffPeakTime: Int?
)
