package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request

import com.google.gson.annotations.SerializedName

internal data class SetCopTempRequest(@SerializedName("cop_temp") val copTemp: Int)
