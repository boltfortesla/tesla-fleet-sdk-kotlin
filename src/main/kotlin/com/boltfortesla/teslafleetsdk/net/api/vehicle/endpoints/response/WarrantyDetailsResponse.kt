package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response

data class WarrantyDetailsResponse(
  val activeWarranty: List<Warranty>,
  val upcomingWarranty: List<Warranty>,
  val expiredWarranty: List<Warranty>,
) {
  data class Warranty(
    val warrantyType: String,
    val warrantyDisplayName: String,
    val expirationDate: String,
    val expirationOdometer: Int,
    val odometerUnit: String,
    val warrantyExpiredOn: String?,
    val coverageAgeInYears: Int,
  )
}
