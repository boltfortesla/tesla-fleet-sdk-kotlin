package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request

import com.google.gson.annotations.SerializedName

internal data class SetTempsRequest(
  @SerializedName("driver_temp") val driverTemp: Float,
  @SerializedName("passenger_temp") val passengerTemp: Float
)
