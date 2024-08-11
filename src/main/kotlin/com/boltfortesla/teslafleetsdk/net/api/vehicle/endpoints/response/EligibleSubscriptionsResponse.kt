package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response

data class EligibleSubscriptionsResponse(
  val country: String,
  val eligible: List<Eligible>,
  val vin: String,
) {
  data class Eligible(
    val addons: List<Addon>,
    val billingOptions: List<BillingOption>,
    val optionCode: String,
    val product: String,
    val startDate: String,
  ) {
    data class Addon(
      val billingPeriod: String,
      val currencyCode: String,
      val optionCode: String,
      val price: Double,
      val tax: Double,
      val total: Double,
    )

    data class BillingOption(
      val billingPeriod: String,
      val currencyCode: String,
      val optionCode: String,
      val price: Double,
      val tax: Double,
      val total: Double,
    )
  }
}
