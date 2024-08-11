package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request

import com.google.gson.annotations.SerializedName

internal data class SetVehicleNameRequest(@SerializedName("vehicle_name") val vehicleName: String)
