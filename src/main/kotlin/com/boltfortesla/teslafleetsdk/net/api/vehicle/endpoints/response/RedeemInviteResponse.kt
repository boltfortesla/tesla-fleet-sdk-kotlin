package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response

import com.google.gson.annotations.SerializedName

data class RedeemInviteResponse(
  @SerializedName("vehicle_id_s") val vehicleId: String,
  val vin: String,
)
