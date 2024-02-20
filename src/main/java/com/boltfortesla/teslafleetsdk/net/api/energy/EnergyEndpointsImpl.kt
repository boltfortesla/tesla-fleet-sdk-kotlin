package com.boltfortesla.teslafleetsdk.net.api.energy

import com.boltfortesla.teslafleetsdk.net.NetworkExecutor
import com.boltfortesla.teslafleetsdk.net.api.energy.EnergyEndpoints.Period
import com.boltfortesla.teslafleetsdk.net.api.energy.request.BackupRequest
import com.boltfortesla.teslafleetsdk.net.api.energy.request.GridImportExportRequest
import com.boltfortesla.teslafleetsdk.net.api.energy.request.OffGridVehicleChargingReserveRequest
import com.boltfortesla.teslafleetsdk.net.api.energy.request.OperationRequest
import com.boltfortesla.teslafleetsdk.net.api.energy.request.StormModeRequest
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/** Implementation of [EnergyEndpoints] */
internal class EnergyEndpointsImpl(
  private val energySiteId: Int,
  private val energyApi: EnergyApi,
  private val networkExecutor: NetworkExecutor,
) : EnergyEndpoints {
  override suspend fun setBackup(backupReservePercent: Int) =
    networkExecutor.execute { energyApi.backup(energySiteId, BackupRequest(backupReservePercent)) }

  override suspend fun getBackupHistory(
    startDate: ZonedDateTime,
    endDate: ZonedDateTime,
    period: Period,
    timeZone: String
  ) =
    networkExecutor.execute {
      energyApi.backupHistory(
        energySiteId,
        startDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
        endDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
        period.name.lowercase(),
        timeZone,
      )
    }

  override suspend fun getChargeHistory(
    startDate: ZonedDateTime,
    endDate: ZonedDateTime,
    timeZone: String
  ) =
    networkExecutor.execute {
      energyApi.chargeHistory(
        energySiteId,
        startDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
        endDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
        timeZone
      )
    }

  override suspend fun getEnergyHistory(
    startDate: ZonedDateTime,
    endDate: ZonedDateTime,
    period: Period,
    timeZone: String
  ) =
    networkExecutor.execute {
      energyApi.energyHistory(
        energySiteId,
        startDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
        endDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
        period.name.lowercase(),
        timeZone,
      )
    }

  override suspend fun setGridImportExport(
    disallowChargeFromGridWithSolarInstalled: Boolean,
    customerPreferredExportRule: EnergyEndpoints.ExportRule
  ) =
    networkExecutor.execute {
      energyApi.gridImportExport(
        energySiteId,
        GridImportExportRequest(
          disallowChargeFromGridWithSolarInstalled,
          customerPreferredExportRule.name.lowercase()
        )
      )
    }

  override suspend fun getLiveStatus() =
    networkExecutor.execute { energyApi.liveStatus(energySiteId) }

  override suspend fun setOffGridVehicleChargingReserve(offGridVehicleChargingReservePercent: Int) =
    networkExecutor.execute {
      energyApi.offGridVehicleChargingReserve(
        energySiteId,
        OffGridVehicleChargingReserveRequest(offGridVehicleChargingReservePercent)
      )
    }

  override suspend fun setOperation(defaultRealMode: EnergyEndpoints.OperationMode) =
    networkExecutor.execute {
      energyApi.operation(energySiteId, OperationRequest(defaultRealMode.name.lowercase()))
    }

  override suspend fun enableStormMode() =
    networkExecutor.execute { energyApi.stormMode(energySiteId, StormModeRequest(enabled = true)) }

  override suspend fun disableStormMode() =
    networkExecutor.execute { energyApi.stormMode(energySiteId, StormModeRequest(enabled = false)) }

  override suspend fun getSiteInfo() = networkExecutor.execute { energyApi.siteInfo(energySiteId) }
}
