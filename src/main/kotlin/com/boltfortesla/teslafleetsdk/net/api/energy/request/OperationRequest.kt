package com.boltfortesla.teslafleetsdk.net.api.energy.request

import com.google.gson.annotations.SerializedName

/** Request for setOperation */
internal data class OperationRequest(
  @SerializedName("default_real_mode") val defaultRealMode: String
)
