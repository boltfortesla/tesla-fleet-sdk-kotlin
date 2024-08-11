package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request

import com.google.gson.annotations.SerializedName

internal data class UpcomingCalendarEntriesRequest(
  @SerializedName("calendar_data") val calendarData: String
)
