package com.boltfortesla.teslafleetsdk.net.api.user.response

/** Response for getOrders */
data class Order(
  val vehicleMapId: Long,
  val referenceNumber: String,
  val vin: String,
  val orderStatus: String,
  val orderSubstatus: String,
  val modelCode: String,
  val countryCode: String,
  val locale: String,
  val mktOptions: String,
  val isB2b: Boolean
)
