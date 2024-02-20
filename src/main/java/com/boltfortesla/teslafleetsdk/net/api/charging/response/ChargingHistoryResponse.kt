package com.boltfortesla.teslafleetsdk.net.api.charging.response

/** Response for getChargingHistory */
data class ChargingHistoryResponse(val data: List<ChargingHistoryEntry>, val totalResults: Int) {

  data class ChargingHistoryEntry(
    val sessionId: Long,
    val vin: String,
    val siteLocationName: String,
    val chargeStartDateTime: String,
    val chargeStopDateTime: String,
    val unlatchDateTime: String,
    val countryCode: String,
    val fees: List<Fee>,
    val billingType: String,
    val invoices: List<Invoice>,
    val vehicleMakeType: String,
  ) {

    data class Fee(
      val sessionFeeId: Long,
      val feeType: String,
      val currencyCode: String,
      val pricingType: String,
      val rateBase: Double,
      val rateTier1: Int?,
      val rateTier2: Int?,
      val rateTier3: Int?,
      val rateTier4: Int?,
      val usageBase: Int,
      val usageTier1: Int?,
      val usageTier2: Int?,
      val usageTier3: Int?,
      val usageTier4: Int?,
      val totalBase: Double,
      val totalTier1: Int,
      val totalTier2: Int,
      val totalTier3: Int,
      val totalTier4: Int,
      val totalDue: Double,
      val netDue: Double,
      val uom: String,
      val isPaid: Boolean,
      val status: String,
    )

    data class Invoice(val fileName: String, val contentId: String, val invoiceType: String)
  }
}
