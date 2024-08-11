package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands

import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.response.VehicleCommandResponse
import com.tesla.generated.carserver.server.CarServer.AutoSeatClimateAction.AutoSeatPosition_E
import com.tesla.generated.carserver.server.CarServer.HvacClimateKeeperAction.ClimateKeeperAction_E
import com.tesla.generated.carserver.server.CarServer.HvacSeatCoolerActions.HvacSeatCoolerLevel_E
import com.tesla.generated.carserver.server.CarServer.HvacSeatCoolerActions.HvacSeatCoolerPosition_E
import com.tesla.generated.carserver.vehicle.Vehicle.ClimateState.CopActivationTemp

/**
 * API for User Endpoints.
 *
 * See https://developer.tesla.com/docs/fleet-api#vehicle-commands for API documentation
 */
interface VehicleCommands {
  suspend fun actuateTrunk(trunk: Trunk): Result<VehicleCommandResponse>

  /** Control opens a Cybetruck's tonneau. Has no effect on other vehicles. */
  suspend fun controlTonneau(action: TonneauAction): Result<VehicleCommandResponse>

  suspend fun adjustVolume(volume: Float): Result<VehicleCommandResponse>

  suspend fun startAutoConditioning(): Result<VehicleCommandResponse>

  suspend fun stopAutoConditioning(): Result<VehicleCommandResponse>

  suspend fun cancelSoftwareUpdate(): Result<VehicleCommandResponse>

  suspend fun setChargeMaxRange(): Result<VehicleCommandResponse>

  suspend fun closeChargePortDoor(): Result<VehicleCommandResponse>

  suspend fun openChargePortDoor(): Result<VehicleCommandResponse>

  suspend fun setChargeStandard(): Result<VehicleCommandResponse>

  suspend fun startCharging(): Result<VehicleCommandResponse>

  suspend fun stopCharging(): Result<VehicleCommandResponse>

  suspend fun adminClearPinToDrive(): Result<VehicleCommandResponse>

  suspend fun lockDoors(): Result<VehicleCommandResponse>

  suspend fun unlockDoors(): Result<VehicleCommandResponse>

  suspend fun eraseGuestData(): Result<VehicleCommandResponse>

  suspend fun flashLights(): Result<VehicleCommandResponse>

  suspend fun enableGuestMode(): Result<VehicleCommandResponse>

  suspend fun disableGuestMode(): Result<VehicleCommandResponse>

  suspend fun honkHorn(): Result<VehicleCommandResponse>

  suspend fun mediaNextFavorite(): Result<VehicleCommandResponse>

  suspend fun mediaNextTrack(): Result<VehicleCommandResponse>

  suspend fun mediaPreviousFavorite(): Result<VehicleCommandResponse>

  suspend fun mediaPreviousTrack(): Result<VehicleCommandResponse>

  suspend fun mediaTogglePlayback(): Result<VehicleCommandResponse>

  suspend fun mediaVolumeDown(): Result<VehicleCommandResponse>

  suspend fun mediaVolumeUp(): Result<VehicleCommandResponse>

  suspend fun sendUrl(
    url: String,
    locale: String,
    timestampMs: String,
  ): Result<VehicleCommandResponse>

  suspend fun sendNavigationGps(
    latitude: Float,
    longitude: Float,
    order: Int,
  ): Result<VehicleCommandResponse>

  suspend fun sendNavigationDestination(
    destination: String,
    locale: String,
    timestampMs: String,
  ): Result<VehicleCommandResponse>

  suspend fun sendNavigationSupercharger(id: Int, order: Int): Result<VehicleCommandResponse>

  suspend fun enableAutomaticSeatClimateControl(seat: AutoSeat): Result<VehicleCommandResponse>

  suspend fun disableAutomaticSeatClimateControl(seat: AutoSeat): Result<VehicleCommandResponse>

  suspend fun enableAutomaticSteeringWheelClimateControl(): Result<VehicleCommandResponse>

  suspend fun disableAutomaticSteeringWheelClimateControl(): Result<VehicleCommandResponse>

  suspend fun remoteBoombox(sound: Int): Result<VehicleCommandResponse>

  suspend fun setSeatCooler(
    seat: CoolerSeat,
    level: SeatClimateLevel,
  ): Result<VehicleCommandResponse>

  suspend fun setSeatHeater(
    seat: HeaterSeat,
    level: SeatClimateLevel,
  ): Result<VehicleCommandResponse>

  suspend fun remoteStart(): Result<VehicleCommandResponse>

  suspend fun setSteeringWheelHeatLevel(level: SeatClimateLevel): Result<VehicleCommandResponse>

  suspend fun enableSteeringWheelHeater(): Result<VehicleCommandResponse>

  suspend fun disableSteeringWheelHeater(): Result<VehicleCommandResponse>

  suspend fun resetPinToDrive(): Result<VehicleCommandResponse>

  suspend fun resetValetPin(): Result<VehicleCommandResponse>

  suspend fun scheduleSoftwareUpdate(offsetSeconds: Int): Result<VehicleCommandResponse>

  suspend fun enableBioweaponDefenseMode(manualOverride: Boolean): Result<VehicleCommandResponse>

  suspend fun disableBioweaponDefenseMode(): Result<VehicleCommandResponse>

  suspend fun enableCabinOverheatProtection(fanOnly: Boolean): Result<VehicleCommandResponse>

  suspend fun disableCabinOverheatProtection(fanOnly: Boolean): Result<VehicleCommandResponse>

  suspend fun setChargeLimit(chargePercent: Int): Result<VehicleCommandResponse>

  suspend fun setChargingAmps(chargingAmps: Int): Result<VehicleCommandResponse>

  suspend fun setClimateKeeperMode(mode: ClimateKeeperMode): Result<VehicleCommandResponse>

  suspend fun setCabinOverheatProtectionTemperature(
    temperature: CopTemperature
  ): Result<VehicleCommandResponse>

  suspend fun enablePinToDrive(pin: String): Result<VehicleCommandResponse>

  suspend fun disablePinToDrive(pin: String): Result<VehicleCommandResponse>

  suspend fun enableMaxPreconditioning(manualOverride: Boolean?): Result<VehicleCommandResponse>

  suspend fun disableMaxPreconditioning(): Result<VehicleCommandResponse>

  suspend fun enableScheduledCharging(minutesAfterMidnight: Int): Result<VehicleCommandResponse>

  suspend fun disableScheduledCharging(minutesAfterMidnight: Int): Result<VehicleCommandResponse>

  suspend fun enableScheduledDeparture(
    minutesAfterMidnight: Int,
    preconditioningEnabled: Boolean,
    preconditioningWeekdaysOnly: Boolean,
    offPeakChargingEnabled: Boolean,
    offPeakChargingWeekdaysOnly: Boolean,
    endOffPeakTime: Int,
  ): Result<VehicleCommandResponse>

  suspend fun disableScheduledDeparture(): Result<VehicleCommandResponse>

  suspend fun enableSentryMode(): Result<VehicleCommandResponse>

  suspend fun disableSentryMode(): Result<VehicleCommandResponse>

  suspend fun setTemperaturesF(
    driverTempF: Float,
    passengerTempF: Float,
  ): Result<VehicleCommandResponse>

  suspend fun setTemperaturesC(
    driverTempC: Float,
    passengerTempC: Float,
  ): Result<VehicleCommandResponse>

  suspend fun enableValetMode(pin: String): Result<VehicleCommandResponse>

  suspend fun disableValetMode(): Result<VehicleCommandResponse>

  suspend fun setVehicleName(name: String): Result<VehicleCommandResponse>

  suspend fun activateSpeedLimit(pin: String): Result<VehicleCommandResponse>

  suspend fun clearSpeedLimitPin(pin: String): Result<VehicleCommandResponse>

  suspend fun adminClearSpeedLimitPin(): Result<VehicleCommandResponse>

  suspend fun deactivateSpeedLimit(pin: String): Result<VehicleCommandResponse>

  suspend fun setSpeedLimit(limitMph: Double): Result<VehicleCommandResponse>

  suspend fun setSunroofState(state: SunroofState): Result<VehicleCommandResponse>

  suspend fun takeDrivenote(note: String): Result<VehicleCommandResponse>

  suspend fun triggerHomelink(latitude: Float, longitude: Float): Result<VehicleCommandResponse>

  suspend fun setUpcomingCalendarEntries(calendarData: String): Result<VehicleCommandResponse>

  suspend fun controlWindows(
    latitude: Float,
    longitude: Float,
    command: WindowCommand,
  ): Result<VehicleCommandResponse>

  suspend fun supportsCommandSigning(): Result<Boolean>

  enum class Trunk {
    FRONT,
    REAR,
  }

  enum class TonneauAction {
    OPEN,
    CLOSE,
    STOP,
  }

  enum class HeaterSeat(val value: Int) {
    FRONT_LEFT(0),
    FRONT_RIGHT(1),
    REAR_LEFT(2),
    REAR_LEFT_BACK(3),
    REAR_CENTER(4),
    REAR_RIGHT(5),
    REAR_RIGHT_BACK(6),
    THIRD_ROW_LEFT(7),
    THIRD_ROW_RIGHT(8),
  }

  enum class CoolerSeat(val value: Int, val position: HvacSeatCoolerPosition_E) {
    FRONT_LEFT(0, HvacSeatCoolerPosition_E.HvacSeatCoolerPosition_FrontLeft),
    FRONT_RIGHT(1, HvacSeatCoolerPosition_E.HvacSeatCoolerPosition_FrontRight),
  }

  enum class AutoSeat(val value: Int, val position: AutoSeatPosition_E) {
    FRONT_LEFT(0, AutoSeatPosition_E.AutoSeatPosition_FrontLeft),
    FRONT_RIGHT(1, AutoSeatPosition_E.AutoSeatPosition_FrontRight),
  }

  enum class SeatClimateLevel(val value: Int, val coolerLevel: HvacSeatCoolerLevel_E) {
    OFF(0, HvacSeatCoolerLevel_E.HvacSeatCoolerLevel_Off),
    LOW(1, HvacSeatCoolerLevel_E.HvacSeatCoolerLevel_Low),
    MEDIUM(2, HvacSeatCoolerLevel_E.HvacSeatCoolerLevel_Med),
    HIGH(3, HvacSeatCoolerLevel_E.HvacSeatCoolerLevel_High),
  }

  enum class ClimateKeeperMode(val value: Int, val action: ClimateKeeperAction_E) {
    OFF(0, ClimateKeeperAction_E.ClimateKeeperAction_Off),
    KEEP(1, ClimateKeeperAction_E.ClimateKeeperAction_On),
    DOG(2, ClimateKeeperAction_E.ClimateKeeperAction_Dog),
    CAMP(3, ClimateKeeperAction_E.ClimateKeeperAction_Camp),
  }

  enum class CopTemperature(val value: Int, val copActivationTemp: CopActivationTemp) {
    LOW(0, CopActivationTemp.CopActivationTempLow),
    MEDIUM(1, CopActivationTemp.CopActivationTempMedium),
    HIGH(2, CopActivationTemp.CopActivationTempHigh),
  }

  enum class SunroofState {
    OPEN,
    CLOSE,
    VENT,
  }

  enum class WindowCommand {
    VENT,
    CLOSE,
  }
}
