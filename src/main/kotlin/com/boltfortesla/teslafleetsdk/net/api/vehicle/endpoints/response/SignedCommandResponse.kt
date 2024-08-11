package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response

import com.google.gson.annotations.SerializedName

data class SignedCommandResponse(@SerializedName("response") val responseBase64: String)
