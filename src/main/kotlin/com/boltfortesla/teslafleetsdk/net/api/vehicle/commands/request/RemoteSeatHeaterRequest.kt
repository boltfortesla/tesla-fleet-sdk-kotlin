package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request

import com.google.gson.annotations.SerializedName

internal data class RemoteSeatHeaterRequest(
  @SerializedName("seat_position") val seatPosition: Int,
  @SerializedName("level") val seatHeaterLevel: Int
)
