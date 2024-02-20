package com.boltfortesla.teslafleetsdk.net.api.charging

import com.boltfortesla.teslafleetsdk.net.NetworkExecutor
import com.boltfortesla.teslafleetsdk.net.api.charging.ChargingEndpoints.SortOrder
import com.boltfortesla.teslafleetsdk.net.api.charging.ChargingEndpoints.SortableField
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/** Implementation of [ChargingEndpoints] */
internal class ChargingEndpointsImpl(
  private val chargingApi: ChargingApi,
  private val networkExecutor: NetworkExecutor,
) : ChargingEndpoints {
  override suspend fun getChargingHistory(
    vin: String,
    startTime: ZonedDateTime,
    endTime: ZonedDateTime,
    pageNumber: Int,
    pageSize: Int,
    sortBy: SortableField,
    sortOrder: SortOrder
  ) =
    networkExecutor.execute {
      chargingApi.chargingHistory(
        vin,
        startTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
        endTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
        pageNumber,
        pageSize,
        sortBy.field,
        sortOrder.name
      )
    }

  override suspend fun getChargingInvoice(id: String) =
    networkExecutor.execute { chargingApi.chargingInvoice(id) }

  override suspend fun getChargingSessions(
    vin: String,
    dateFrom: ZonedDateTime,
    dateTo: ZonedDateTime,
    limit: Int,
    offset: Int
  ) =
    networkExecutor.execute {
      chargingApi.chargingSessions(
        vin,
        dateFrom.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
        dateTo.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
        limit,
        offset
      )
    }
}
