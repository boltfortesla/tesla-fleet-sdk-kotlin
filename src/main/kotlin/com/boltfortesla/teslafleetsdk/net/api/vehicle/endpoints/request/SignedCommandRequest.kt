package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.request

import com.google.gson.annotations.SerializedName

internal data class SignedCommandRequest(
  @SerializedName("routable_message") val routableMessage: String
)
