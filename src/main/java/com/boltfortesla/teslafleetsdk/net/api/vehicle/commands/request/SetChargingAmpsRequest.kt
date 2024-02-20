package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request

import com.google.gson.annotations.SerializedName

internal data class SetChargingAmpsRequest(@SerializedName("charging_amps") val chargingAmps: Int)
