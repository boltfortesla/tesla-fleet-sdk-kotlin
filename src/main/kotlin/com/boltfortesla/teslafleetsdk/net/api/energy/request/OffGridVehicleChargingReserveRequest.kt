package com.boltfortesla.teslafleetsdk.net.api.energy.request

import com.google.gson.annotations.SerializedName

/** Request for setOffGridVehicleChargingReserve */
internal data class OffGridVehicleChargingReserveRequest(
  @SerializedName("off_grid_vehicle_charging_reserve_percent")
  val offGridVehicleChargingReservePercent: Int
)
