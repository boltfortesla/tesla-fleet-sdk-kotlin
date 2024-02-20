package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request

import com.google.gson.annotations.SerializedName

internal data class SetCabinOverheatProtectionRequest(
  val on: Boolean,
  @SerializedName("fan_only") val fanOnly: Boolean
)
