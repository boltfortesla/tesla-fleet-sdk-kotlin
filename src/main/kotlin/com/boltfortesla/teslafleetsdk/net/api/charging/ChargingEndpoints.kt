package com.boltfortesla.teslafleetsdk.net.api.charging

import com.boltfortesla.teslafleetsdk.net.api.charging.response.ChargingHistoryResponse
import com.boltfortesla.teslafleetsdk.net.api.charging.response.ChargingSessionsResponse
import java.time.ZonedDateTime
import okhttp3.ResponseBody

/**
 * API for Charging Endpoints.
 *
 * See https://developer.tesla.com/docs/fleet-api#charging-endpoints for API documentation
 */
interface ChargingEndpoints {
  suspend fun getChargingHistory(
    vin: String,
    startTime: ZonedDateTime,
    endTime: ZonedDateTime,
    pageNumber: Int,
    pageSize: Int,
    sortBy: SortableField,
    sortOrder: SortOrder
  ): Result<ChargingHistoryResponse>

  suspend fun getChargingInvoice(id: String): Result<ResponseBody>

  suspend fun getChargingSessions(
    vin: String,
    dateFrom: ZonedDateTime,
    dateTo: ZonedDateTime,
    limit: Int,
    offset: Int
  ): Result<ChargingSessionsResponse>

  enum class SortOrder {
    ASC,
    DESC
  }

  enum class SortableField(val field: String) {
    SESSION_ID("sessionId"),
    VIN("vin"),
    SITE_LOCATION_NAME("siteLocationName"),
    CHARGE_START_DATE_TIME("chargeStartDateTime"),
    CHARGE_STOP_DATE_TIME("chargeStopDateTime"),
    UNLATCH_DATE_TIME("unlatchDateTime"),
    COUNTRY_CODE("countryCode"),
    BILLING_TYPE("billingType"),
    VEHICLE_MAKE_TYPE("vehicleMakeType"),
  }
}
