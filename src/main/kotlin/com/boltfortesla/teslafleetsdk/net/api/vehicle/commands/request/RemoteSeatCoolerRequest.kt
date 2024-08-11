package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request

import com.google.gson.annotations.SerializedName

internal data class RemoteSeatCoolerRequest(
  @SerializedName("seat_position") val seatPosition: Int,
  @SerializedName("seat_cooler_level") val seatCoolerLevel: Int
)
