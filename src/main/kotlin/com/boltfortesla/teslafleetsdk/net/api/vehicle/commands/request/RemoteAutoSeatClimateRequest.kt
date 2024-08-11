package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request

import com.google.gson.annotations.SerializedName

internal data class RemoteAutoSeatClimateRequest(
  @SerializedName("auto_seat_position") val autoSeatPosition: Int,
  @SerializedName("auto_climate_on") val autoClimateOn: Boolean
)
