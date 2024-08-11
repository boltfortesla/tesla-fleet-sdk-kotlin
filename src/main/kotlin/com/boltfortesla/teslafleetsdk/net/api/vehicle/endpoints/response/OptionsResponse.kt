package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response

data class OptionsResponse(val codes: List<OptionCode>) {
  data class OptionCode(
    val code: String,
    val colorCode: String? = null,
    val displayName: String,
    val isActive: Boolean
  )
}
