package com.boltfortesla.teslafleetsdk.net.api.energy

import com.boltfortesla.teslafleetsdk.net.api.FleetApiResponse
import com.boltfortesla.teslafleetsdk.net.api.energy.response.BackupHistoryResponse
import com.boltfortesla.teslafleetsdk.net.api.energy.response.ChargeHistoryResponse
import com.boltfortesla.teslafleetsdk.net.api.energy.response.EnergyCommandResponse
import com.boltfortesla.teslafleetsdk.net.api.energy.response.EnergyHistoryResponse
import com.boltfortesla.teslafleetsdk.net.api.energy.response.LiveStatusResponse
import com.boltfortesla.teslafleetsdk.net.api.energy.response.SiteInfoResponse
import java.time.ZonedDateTime

/**
 * API for Energy Endpoints.
 *
 * See https://developer.tesla.com/docs/fleet-api#energy-endpoints for API documentation
 */
interface EnergyEndpoints {
  suspend fun setBackup(backupReservePercent: Int): Result<FleetApiResponse<EnergyCommandResponse>>

  suspend fun getBackupHistory(
    startDate: ZonedDateTime,
    endDate: ZonedDateTime,
    period: Period,
    timeZone: String
  ): Result<FleetApiResponse<BackupHistoryResponse>>

  suspend fun getChargeHistory(
    startDate: ZonedDateTime,
    endDate: ZonedDateTime,
    timeZone: String
  ): Result<FleetApiResponse<ChargeHistoryResponse>>

  suspend fun getEnergyHistory(
    startDate: ZonedDateTime,
    endDate: ZonedDateTime,
    period: Period,
    timeZone: String
  ): Result<FleetApiResponse<EnergyHistoryResponse>>

  suspend fun setGridImportExport(
    disallowChargeFromGridWithSolarInstalled: Boolean,
    customerPreferredExportRule: ExportRule
  ): Result<FleetApiResponse<EnergyCommandResponse>>

  suspend fun getLiveStatus(): Result<FleetApiResponse<LiveStatusResponse>>

  suspend fun setOffGridVehicleChargingReserve(
    offGridVehicleChargingReservePercent: Int
  ): Result<FleetApiResponse<EnergyCommandResponse>>

  suspend fun setOperation(
    defaultRealMode: OperationMode
  ): Result<FleetApiResponse<EnergyCommandResponse>>

  suspend fun enableStormMode(): Result<FleetApiResponse<EnergyCommandResponse>>

  suspend fun disableStormMode(): Result<FleetApiResponse<EnergyCommandResponse>>

  suspend fun getSiteInfo(): Result<FleetApiResponse<SiteInfoResponse>>

  enum class Period {
    DAY,
    WEEK,
    MONTH,
    YEAR,
    LIFETIME
  }

  enum class ExportRule {
    BATTERY_OK,
    PV_ONLY,
    NEVER
  }

  enum class OperationMode {
    AUTONOMOUS,
    SELF_CONSUMPTION
  }
}
