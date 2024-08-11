package com.boltfortesla.teslafleetsdk.net.api.charging.response

import com.google.gson.annotations.SerializedName

/** Response for getChargingSessions */
data class ChargingSessionsResponse(
  val data: List<ChargingSession>,
  @SerializedName("status_code") val statusCode: Int,
  @SerializedName("status_message") val statusMessage: String,
  val timestamp: Timestamp
) {
  data class Timestamp(@SerializedName("time.Time") val time: String)

  data class ChargingSession(
    @SerializedName("charging_periods") val chargingPeriods: List<ChargingPeriod>,
    val id: String,
    val location: Location,
    val model: String,
    @SerializedName("start_date_time") val startDateTime: String,
    @SerializedName("stop_date_time") val stopDateTime: String,
    val tariffs: Tariff,
    @SerializedName("total_cost") val totalCost: TotalCost,
    @SerializedName("total_energy") val totalEnergy: Int,
    @SerializedName("total_time") val totalTine: Long,
    val vin: String,
  )

  data class ChargingPeriod(
    val dimensions: List<Dimension>,
    @SerializedName("start_date_time") val startDateTime: String
  ) {
    data class Dimension(val type: String, val volume: Int)
  }

  data class Location(val country: String, val name: String)

  data class Tariff(val currency: String, val elements: List<TariffElement>) {
    data class TariffElement(
      @SerializedName("price_components") val priceComponents: List<PriceComponent>,
      val restrictions: Map<String, Any>
    ) {
      data class PriceComponent(
        val price: Int,
        @SerializedName("step_size") val stepSize: Int,
        val type: String
      )

      override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TariffElement

        if (priceComponents != other.priceComponents) return false
        if (restrictions.keys != other.restrictions.keys) return false
        return restrictions.values.map { it.toString() } ==
          other.restrictions.values.map { it.toString() }
      }

      override fun hashCode(): Int {
        var result = priceComponents.hashCode()
        result = 31 * result + restrictions.hashCode()
        return result
      }
    }
  }

  data class TotalCost(
    @SerializedName("excl_vat") val excludingVat: Int,
    @SerializedName("incl_vat") val includingVat: Int,
    val vat: Int
  )
}
