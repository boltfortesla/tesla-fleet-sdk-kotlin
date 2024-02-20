package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response

import com.google.gson.annotations.SerializedName

data class FleetTelemetryModifyConfigResponse(
  @SerializedName("updated_vehicles") val updatedVehicles: Int
)
