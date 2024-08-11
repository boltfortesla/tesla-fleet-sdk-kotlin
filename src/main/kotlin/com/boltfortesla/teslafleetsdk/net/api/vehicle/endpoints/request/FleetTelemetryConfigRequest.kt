package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.request

import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.TelemetryConfig

internal data class FleetTelemetryConfigRequest(
  val vins: List<String>,
  val config: TelemetryConfig,
)
