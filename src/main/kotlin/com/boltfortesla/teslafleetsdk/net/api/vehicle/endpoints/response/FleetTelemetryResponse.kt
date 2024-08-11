package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response

import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.TelemetryConfig

data class FleetTelemetryResponse(val synced: Boolean, val config: TelemetryConfig)
