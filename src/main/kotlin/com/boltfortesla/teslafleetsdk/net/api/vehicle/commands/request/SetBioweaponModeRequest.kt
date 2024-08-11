package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request

import com.google.gson.annotations.SerializedName

internal data class SetBioweaponModeRequest(
  val on: Boolean,
  @SerializedName("manual_override") val manualOverride: Boolean
)
