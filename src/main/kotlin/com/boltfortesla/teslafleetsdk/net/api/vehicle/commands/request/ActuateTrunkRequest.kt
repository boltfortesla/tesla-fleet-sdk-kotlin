package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request

import com.google.gson.annotations.SerializedName

internal data class ActuateTrunkRequest(@SerializedName("which_trunk") val whichTrunk: String)
