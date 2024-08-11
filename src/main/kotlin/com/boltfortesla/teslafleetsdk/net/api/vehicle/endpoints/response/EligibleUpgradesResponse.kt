package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response

data class EligibleUpgradesResponse(
  val vin: String,
  val country: String,
  val type: String,
  val eligible: List<EligibleOption>,
) {
  data class EligibleOption(
    val optionCode: String,
    val optionGroup: String,
    val currentOptionCode: String,
    val pricing: List<PricingOption>,
  ) {
    data class PricingOption(
      val price: Int,
      val total: Int,
      val currencyCode: String,
      val isPrimary: Boolean,
    )
  }
}
