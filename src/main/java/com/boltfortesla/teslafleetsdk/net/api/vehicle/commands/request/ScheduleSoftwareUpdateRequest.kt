package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request

import com.google.gson.annotations.SerializedName

internal data class ScheduleSoftwareUpdateRequest(@SerializedName("offset_sec") val offsetSec: Int)
