package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request

import com.google.gson.annotations.SerializedName

internal data class SetClimateKeeperModeRequest(
  @SerializedName("climate_keeper_mode") val climateKeeperMode: Int
)
