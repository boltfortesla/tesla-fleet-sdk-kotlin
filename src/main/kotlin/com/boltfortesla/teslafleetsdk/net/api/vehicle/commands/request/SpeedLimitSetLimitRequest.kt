package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request

import com.google.gson.annotations.SerializedName

internal data class SpeedLimitSetLimitRequest(@SerializedName("limit_mph") val limitMph: Double)
