package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.SharedSecretFetcher
import com.boltfortesla.teslafleetsdk.handshake.Handshaker
import com.boltfortesla.teslafleetsdk.handshake.SessionInfo
import com.boltfortesla.teslafleetsdk.log.Log
import com.boltfortesla.teslafleetsdk.net.NetworkExecutor
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.ErrorResultException.ActionFailureException
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.VehicleCommands.AutoSeat
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.VehicleCommands.ClimateKeeperMode
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.VehicleCommands.CoolerSeat
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.VehicleCommands.HeaterSeat
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.VehicleCommands.SeatClimateLevel
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.VehicleCommands.Trunk.FRONT
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.VehicleCommands.Trunk.REAR
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.VehicleCommands.WindowCommand
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.ActuateTrunkRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.AdjustVolumeRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.GuestModeRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.NavigationGpsRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.NavigationSuperchargerRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.RemoteAutoSeatClimateRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.RemoteAutoSteeringWheelHeatClimateRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.RemoteBoomboxRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.RemoteSeatCoolerRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.RemoteSeatHeaterRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.RemoteSteeringWheelHeatLevelRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.RemoteSteeringWheelHeaterRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.ScheduleSoftwareUpdateRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SetBioweaponModeRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SetCabinOverheatProtectionRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SetChargeLimitRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SetChargingAmpsRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SetClimateKeeperModeRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SetCopTempRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SetPinToDriveRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SetPreconditioningMaxRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SetScheduledChargingRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SetScheduledDepartureRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SetSentryModeRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SetTempsRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SetValetModeRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SetVehicleNameRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.ShareRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.ShareRequest.Value
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SpeedLimitActivateRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SpeedLimitClearPinRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SpeedLimitDeactivateRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SpeedLimitSetLimitRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SunRoofControlRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.TakeDrivenoteRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.TriggerHomelinkRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.UpcomingCalendarEntriesRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.WindowControlRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.response.VehicleCommandResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.response.VehicleCommandResponse.CommandResponse
import com.google.protobuf.GeneratedMessageV3
import com.tesla.generated.carserver.common.latLong
import com.tesla.generated.carserver.common.offPeakChargingTimes
import com.tesla.generated.carserver.common.preconditioningTimes
import com.tesla.generated.carserver.common.void
import com.tesla.generated.carserver.server.AutoSeatClimateActionKt.carSeat
import com.tesla.generated.carserver.server.CarServer
import com.tesla.generated.carserver.server.HvacSeatCoolerActionsKt.hvacSeatCoolerAction
import com.tesla.generated.carserver.server.HvacSeatHeaterActionsKt.hvacSeatHeaterAction
import com.tesla.generated.carserver.server.HvacTemperatureAdjustmentActionKt.temperature
import com.tesla.generated.carserver.server.action
import com.tesla.generated.carserver.server.autoSeatClimateAction
import com.tesla.generated.carserver.server.chargingSetLimitAction
import com.tesla.generated.carserver.server.chargingStartStopAction
import com.tesla.generated.carserver.server.drivingClearSpeedLimitPinAction
import com.tesla.generated.carserver.server.drivingSetSpeedLimitAction
import com.tesla.generated.carserver.server.drivingSpeedLimitAction
import com.tesla.generated.carserver.server.hvacAutoAction
import com.tesla.generated.carserver.server.hvacBioweaponModeAction
import com.tesla.generated.carserver.server.hvacClimateKeeperAction
import com.tesla.generated.carserver.server.hvacSeatCoolerActions
import com.tesla.generated.carserver.server.hvacSeatHeaterActions
import com.tesla.generated.carserver.server.hvacSetPreconditioningMaxAction
import com.tesla.generated.carserver.server.hvacSteeringWheelHeaterAction
import com.tesla.generated.carserver.server.hvacTemperatureAdjustmentAction
import com.tesla.generated.carserver.server.mediaNextFavorite
import com.tesla.generated.carserver.server.mediaNextTrack
import com.tesla.generated.carserver.server.mediaPlayAction
import com.tesla.generated.carserver.server.mediaPreviousFavorite
import com.tesla.generated.carserver.server.mediaPreviousTrack
import com.tesla.generated.carserver.server.mediaUpdateVolume
import com.tesla.generated.carserver.server.scheduledChargingAction
import com.tesla.generated.carserver.server.scheduledDepartureAction
import com.tesla.generated.carserver.server.setCabinOverheatProtectionAction
import com.tesla.generated.carserver.server.setChargingAmpsAction
import com.tesla.generated.carserver.server.setCopTempAction
import com.tesla.generated.carserver.server.setVehicleNameAction
import com.tesla.generated.carserver.server.vehicleAction
import com.tesla.generated.carserver.server.vehicleControlCancelSoftwareUpdateAction
import com.tesla.generated.carserver.server.vehicleControlFlashLightsAction
import com.tesla.generated.carserver.server.vehicleControlHonkHornAction
import com.tesla.generated.carserver.server.vehicleControlResetPinToDriveAction
import com.tesla.generated.carserver.server.vehicleControlResetValetPinAction
import com.tesla.generated.carserver.server.vehicleControlScheduleSoftwareUpdateAction
import com.tesla.generated.carserver.server.vehicleControlSetPinToDriveAction
import com.tesla.generated.carserver.server.vehicleControlSetSentryModeAction
import com.tesla.generated.carserver.server.vehicleControlSetValetModeAction
import com.tesla.generated.carserver.server.vehicleControlSunroofOpenCloseAction
import com.tesla.generated.carserver.server.vehicleControlTriggerHomelinkAction
import com.tesla.generated.carserver.server.vehicleControlWindowAction
import com.tesla.generated.carserver.vehicle.VehicleStateKt.guestMode
import com.tesla.generated.universalmessage.UniversalMessage.Domain
import com.tesla.generated.vcsec.Vcsec
import com.tesla.generated.vcsec.Vcsec.ClosureMoveType_E.CLOSURE_MOVE_TYPE_CLOSE
import com.tesla.generated.vcsec.Vcsec.ClosureMoveType_E.CLOSURE_MOVE_TYPE_MOVE
import com.tesla.generated.vcsec.Vcsec.ClosureMoveType_E.CLOSURE_MOVE_TYPE_OPEN
import com.tesla.generated.vcsec.Vcsec.RKEAction_E.RKE_ACTION_LOCK
import com.tesla.generated.vcsec.Vcsec.RKEAction_E.RKE_ACTION_REMOTE_DRIVE
import com.tesla.generated.vcsec.Vcsec.RKEAction_E.RKE_ACTION_UNLOCK
import com.tesla.generated.vcsec.closureMoveRequest
import com.tesla.generated.vcsec.unsignedMessage
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.roundToInt
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.HttpException

/**
 * Implementation of [VehicleCommands].
 *
 * @param vin the VIN of the vehicle to be commanded.
 * @param clientPublicKey the Tesla Developer Application public key
 */
internal class VehicleCommandsImpl(
  private val vin: String,
  private val clientPublicKey: ByteArray,
  private val sharedSecretFetcher: SharedSecretFetcher,
  commandProtocolSupported: Boolean,
  private val handshaker: Handshaker,
  private val vehicleCommandsApi: VehicleCommandsApi,
  private val networkExecutor: NetworkExecutor,
  private val signedCommandSender: SignedCommandSender
) : VehicleCommands {
  private val handshakeMutex = Mutex()
  private val sessionInfoMap = ConcurrentHashMap<Domain, SessionInfo>()
  private var useCommandProtocol = commandProtocolSupported

  override suspend fun actuateTrunk(trunk: VehicleCommands.Trunk): Result<VehicleCommandResponse> {
    return executeCommand(
      unsignedMessage {
        closureMoveRequest = closureMoveRequest {
          when (trunk) {
            FRONT -> frontTrunk = CLOSURE_MOVE_TYPE_MOVE
            REAR -> rearTrunk = CLOSURE_MOVE_TYPE_MOVE
          }
        }
      }
    ) {
      vehicleCommandsApi.actuateTrunk(vin, ActuateTrunkRequest(trunk.name.lowercase()))
    }
  }

  override suspend fun adjustVolume(volume: Float): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          mediaUpdateVolume = mediaUpdateVolume { volumeAbsoluteFloat = volume }
        }
      }
    ) {
      vehicleCommandsApi.adjustVolume(vin, AdjustVolumeRequest(volume))
    }
  }

  override suspend fun startAutoConditioning(): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction { hvacAutoAction = hvacAutoAction { powerOn = true } }
      }
    ) {
      vehicleCommandsApi.startAutoConditioning(vin)
    }
  }

  override suspend fun stopAutoConditioning(): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction { hvacAutoAction = hvacAutoAction { powerOn = false } }
      }
    ) {
      vehicleCommandsApi.stopAutoConditioning(vin)
    }
  }

  override suspend fun cancelSoftwareUpdate(): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          vehicleControlCancelSoftwareUpdateAction = vehicleControlCancelSoftwareUpdateAction {}
        }
      }
    ) {
      vehicleCommandsApi.cancelSoftwareUpdate(vin)
    }
  }

  override suspend fun setChargeMaxRange(): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          chargingStartStopAction = chargingStartStopAction { startMaxRange = void {} }
        }
      }
    ) {
      vehicleCommandsApi.chargeMaxRange(vin)
    }
  }

  override suspend fun closeChargePortDoor(): Result<VehicleCommandResponse> {
    return executeCommand(
      unsignedMessage {
        closureMoveRequest = closureMoveRequest { chargePort = CLOSURE_MOVE_TYPE_CLOSE }
      }
    ) {
      vehicleCommandsApi.closeChargePortDoor(vin)
    }
  }

  override suspend fun openChargePortDoor(): Result<VehicleCommandResponse> {
    return executeCommand(
      unsignedMessage {
        closureMoveRequest = closureMoveRequest { chargePort = CLOSURE_MOVE_TYPE_OPEN }
      }
    ) {
      vehicleCommandsApi.openChargePortDoor(vin)
    }
  }

  override suspend fun setChargeStandard(): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          chargingStartStopAction = chargingStartStopAction { startStandard = void {} }
        }
      }
    ) {
      vehicleCommandsApi.chargeStandard(vin)
    }
  }

  override suspend fun startCharging(): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          chargingStartStopAction = chargingStartStopAction { start = void {} }
        }
      }
    ) {
      vehicleCommandsApi.startCharging(vin)
    }
  }

  override suspend fun stopCharging(): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          chargingStartStopAction = chargingStartStopAction { stop = void {} }
        }
      }
    ) {
      vehicleCommandsApi.stopCharging(vin)
    }
  }

  override suspend fun adminClearPinToDrive(): Result<VehicleCommandResponse> {
    // Does not require Command Protocol
    return executeCommand(action = null) { vehicleCommandsApi.clearPinToDriveAdmin(vin) }
  }

  override suspend fun lockDoors(): Result<VehicleCommandResponse> {
    return executeCommand(unsignedMessage { rKEAction = RKE_ACTION_LOCK }) {
      vehicleCommandsApi.lockDoors(vin)
    }
  }

  override suspend fun unlockDoors(): Result<VehicleCommandResponse> {
    return executeCommand(unsignedMessage { rKEAction = RKE_ACTION_UNLOCK }) {
      vehicleCommandsApi.unlockDoors(vin)
    }
  }

  override suspend fun eraseUserData(): Result<VehicleCommandResponse> {
    // Does not require Command Protocol
    return executeCommand(action = null) { vehicleCommandsApi.eraseUserData(vin) }
  }

  override suspend fun flashLights(): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          vehicleControlFlashLightsAction = vehicleControlFlashLightsAction {}
        }
      }
    ) {
      vehicleCommandsApi.flashLights(vin)
    }
  }

  override suspend fun enableGuestMode() = guestMode(enable = true)

  override suspend fun disableGuestMode() = guestMode(enable = false)

  private suspend fun guestMode(enable: Boolean): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction { guestModeAction = guestMode { guestModeActive = enable } }
      }
    ) {
      vehicleCommandsApi.setGuestMode(vin, GuestModeRequest(enable = enable))
    }
  }

  override suspend fun honkHorn(): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          vehicleControlHonkHornAction = vehicleControlHonkHornAction {}
        }
      }
    ) {
      vehicleCommandsApi.honkHorn(vin)
    }
  }

  override suspend fun mediaNextFavorite(): Result<VehicleCommandResponse> {
    return executeCommand(
      action { vehicleAction = vehicleAction { mediaNextFavorite = mediaNextFavorite {} } }
    ) {
      vehicleCommandsApi.nextFavorite(vin)
    }
  }

  override suspend fun mediaNextTrack(): Result<VehicleCommandResponse> {
    return executeCommand(
      action { vehicleAction = vehicleAction { mediaNextTrack = mediaNextTrack {} } }
    ) {
      vehicleCommandsApi.nextTrack(vin)
    }
  }

  override suspend fun mediaPreviousFavorite(): Result<VehicleCommandResponse> {
    return executeCommand(
      action { vehicleAction = vehicleAction { mediaPreviousFavorite = mediaPreviousFavorite {} } }
    ) {
      vehicleCommandsApi.previousFavorite(vin)
    }
  }

  override suspend fun mediaPreviousTrack(): Result<VehicleCommandResponse> {
    return executeCommand(
      action { vehicleAction = vehicleAction { mediaPreviousTrack = mediaPreviousTrack {} } }
    ) {
      vehicleCommandsApi.previousTrack(vin)
    }
  }

  override suspend fun mediaTogglePlayback(): Result<VehicleCommandResponse> {
    return executeCommand(
      action { vehicleAction = vehicleAction { mediaPlayAction = mediaPlayAction {} } }
    ) {
      vehicleCommandsApi.toggleMediaPlayback(vin)
    }
  }

  override suspend fun mediaVolumeDown(): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction { mediaUpdateVolume = mediaUpdateVolume { volumeDelta = -1 } }
      }
    ) {
      vehicleCommandsApi.decreaseMediaVolume(vin)
    }
  }

  override suspend fun mediaVolumeUp(): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction { mediaUpdateVolume = mediaUpdateVolume { volumeDelta + 1 } }
      }
    ) {
      vehicleCommandsApi.increaseMediaVolume(vin)
    }
  }

  override suspend fun sendUrl(
    url: String,
    locale: String,
    timestampMs: String
  ): Result<VehicleCommandResponse> {
    // Does not require Command Protocol
    return executeCommand(action = null) {
      vehicleCommandsApi.shareUrl(
        vin,
        ShareRequest(value = Value(url), locale = locale, timestampMs = timestampMs)
      )
    }
  }

  override suspend fun sendNavigationGps(
    latitude: Float,
    longitude: Float,
    order: Int
  ): Result<VehicleCommandResponse> {
    // Does not require Command Protocol
    return executeCommand(action = null) {
      vehicleCommandsApi.startNavigationToCoordinates(
        vin,
        NavigationGpsRequest(latitude, longitude, order)
      )
    }
  }

  override suspend fun sendNavigationDestination(
    destination: String,
    locale: String,
    timestampMs: String
  ): Result<VehicleCommandResponse> {
    // Does not require Command Protocol
    return executeCommand(action = null) {
      vehicleCommandsApi.sendNavigationLocation(
        vin,
        ShareRequest(value = Value(destination), locale = locale, timestampMs = timestampMs)
      )
    }
  }

  override suspend fun sendNavigationSupercharger(
    id: Int,
    order: Int
  ): Result<VehicleCommandResponse> {
    // Does not require Command Protocol
    return executeCommand(action = null) {
      vehicleCommandsApi.startNavigationToSupercharger(
        vin,
        NavigationSuperchargerRequest(id, order)
      )
    }
  }

  override suspend fun enableAutomaticSeatClimateControl(seat: AutoSeat) =
    automaticSeatClimateControl(seat, autoClimateOn = true)

  override suspend fun disableAutomaticSeatClimateControl(seat: AutoSeat) =
    automaticSeatClimateControl(seat, autoClimateOn = false)

  private suspend fun automaticSeatClimateControl(
    seat: AutoSeat,
    autoClimateOn: Boolean
  ): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          autoSeatClimateAction = autoSeatClimateAction {
            carseat += carSeat {
              seatPosition = seat.position
              on = autoClimateOn
            }
          }
        }
      }
    ) {
      vehicleCommandsApi.setAutoSeatClimate(
        vin,
        RemoteAutoSeatClimateRequest(seat.value, autoClimateOn = autoClimateOn)
      )
    }
  }

  override suspend fun enableAutomaticSteeringWheelClimateControl() =
    automaticSteeringWheelClimateControl(on = true)

  override suspend fun disableAutomaticSteeringWheelClimateControl() =
    automaticSteeringWheelClimateControl(on = false)

  private suspend fun automaticSteeringWheelClimateControl(
    on: Boolean
  ): Result<VehicleCommandResponse> {
    return executeCommand(
      action =
        action {
          vehicleAction = vehicleAction {
            hvacSteeringWheelHeaterAction = hvacSteeringWheelHeaterAction { powerOn = on }
          }
        }
    ) {
      vehicleCommandsApi.setAutoSteeringWheelHeat(
        vin,
        RemoteAutoSteeringWheelHeatClimateRequest(on = on)
      )
    }
  }

  override suspend fun remoteBoombox(sound: Int): Result<VehicleCommandResponse> {
    // Does not require Command Protocol
    return executeCommand(action = null) {
      vehicleCommandsApi.playBoomboxSound(vin, RemoteBoomboxRequest(sound))
    }
  }

  override suspend fun setSeatCooler(
    seat: CoolerSeat,
    level: SeatClimateLevel
  ): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          hvacSeatCoolerActions = hvacSeatCoolerActions {
            hvacSeatCoolerAction += hvacSeatCoolerAction {
              seatPosition = seat.position
              seatCoolerLevel = level.coolerLevel
            }
          }
        }
      }
    ) {
      vehicleCommandsApi.setSeatCooler(vin, RemoteSeatCoolerRequest(seat.value, level.value))
    }
  }

  override suspend fun setSeatHeater(
    seat: HeaterSeat,
    level: SeatClimateLevel
  ): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          hvacSeatHeaterActions = hvacSeatHeaterActions {
            hvacSeatHeaterAction += hvacSeatHeaterAction {
              when (seat) {
                HeaterSeat.FRONT_LEFT -> cARSEATFRONTLEFT = void {}
                HeaterSeat.FRONT_RIGHT -> cARSEATFRONTRIGHT = void {}
                HeaterSeat.REAR_LEFT -> cARSEATREARLEFT = void {}
                HeaterSeat.REAR_LEFT_BACK -> cARSEATREARLEFTBACK = void {}
                HeaterSeat.REAR_CENTER -> cARSEATREARCENTER = void {}
                HeaterSeat.REAR_RIGHT -> cARSEATREARRIGHT = void {}
                HeaterSeat.REAR_RIGHT_BACK -> cARSEATREARRIGHTBACK = void {}
                HeaterSeat.THIRD_ROW_LEFT -> cARSEATTHIRDROWLEFT = void {}
                HeaterSeat.THIRD_ROW_RIGHT -> cARSEATTHIRDROWRIGHT = void {}
              }
              when (level) {
                SeatClimateLevel.OFF -> sEATHEATEROFF = void {}
                SeatClimateLevel.LOW -> sEATHEATERLOW = void {}
                SeatClimateLevel.MEDIUM -> sEATHEATERMED = void {}
                SeatClimateLevel.HIGH -> sEATHEATERHIGH = void {}
              }
            }
          }
        }
      }
    ) {
      vehicleCommandsApi.setSeatHeater(vin, RemoteSeatHeaterRequest(seat.value, level.value))
    }
  }

  override suspend fun remoteStart(): Result<VehicleCommandResponse> {
    return executeCommand(unsignedMessage { rKEAction = RKE_ACTION_REMOTE_DRIVE }) {
      vehicleCommandsApi.remoteStart(vin)
    }
  }

  override suspend fun setSteeringWheelHeatLevel(
    level: SeatClimateLevel
  ): Result<VehicleCommandResponse> {
    return executeCommand(
      // Not supported via Command Protocol
      action = null
    ) {
      vehicleCommandsApi.setSteeringWheelHeatLevel(
        vin,
        RemoteSteeringWheelHeatLevelRequest(level.value)
      )
    }
  }

  override suspend fun enableSteeringWheelHeater() = steeringWheelHeater(true)

  override suspend fun disableSteeringWheelHeater() = steeringWheelHeater(false)

  private suspend fun steeringWheelHeater(on: Boolean): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          hvacSteeringWheelHeaterAction = hvacSteeringWheelHeaterAction { powerOn = on }
        }
      }
    ) {
      vehicleCommandsApi.setSteeringWheelHeater(vin, RemoteSteeringWheelHeaterRequest(on = on))
    }
  }

  override suspend fun resetPinToDrive(): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          vehicleControlResetPinToDriveAction = vehicleControlResetPinToDriveAction {}
        }
      }
    ) {
      vehicleCommandsApi.resetPinToDrivePin(vin)
    }
  }

  override suspend fun resetValetPin(): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          vehicleControlResetValetPinAction = vehicleControlResetValetPinAction {}
        }
      }
    ) {
      vehicleCommandsApi.resetValetPin(vin)
    }
  }

  override suspend fun scheduleSoftwareUpdate(offsetSeconds: Int): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          vehicleControlScheduleSoftwareUpdateAction = vehicleControlScheduleSoftwareUpdateAction {
            offsetSec = offsetSeconds
          }
        }
      }
    ) {
      vehicleCommandsApi.scheduleSoftwareUpdate(vin, ScheduleSoftwareUpdateRequest(offsetSeconds))
    }
  }

  override suspend fun enableBioweaponDefenseMode(manualOverride: Boolean) =
    bioweaponDefenseMode(on = true, manualOverride)

  override suspend fun disableBioweaponDefenseMode() =
    bioweaponDefenseMode(on = false, manualOverride = false)

  private suspend fun bioweaponDefenseMode(
    on: Boolean,
    manualOverride: Boolean
  ): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          hvacBioweaponModeAction = hvacBioweaponModeAction {
            this.on = on
            this.manualOverride = manualOverride
          }
        }
      }
    ) {
      vehicleCommandsApi.setBioweaponMode(vin, SetBioweaponModeRequest(on, manualOverride))
    }
  }

  override suspend fun enableCabinOverheatProtection(fanOnly: Boolean) =
    cabinOverheatProtection(on = true, fanOnly)

  override suspend fun disableCabinOverheatProtection(fanOnly: Boolean) =
    cabinOverheatProtection(on = false, fanOnly)

  private suspend fun cabinOverheatProtection(
    on: Boolean,
    fanOnly: Boolean
  ): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          setCabinOverheatProtectionAction = setCabinOverheatProtectionAction {
            this.on = on
            this.fanOnly = fanOnly
          }
        }
      }
    ) {
      vehicleCommandsApi.setCabinOverheatProtection(
        vin,
        SetCabinOverheatProtectionRequest(on, fanOnly)
      )
    }
  }

  override suspend fun setChargeLimit(chargePercent: Int): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          chargingSetLimitAction = chargingSetLimitAction { percent = chargePercent }
        }
      }
    ) {
      vehicleCommandsApi.setChargeLimit(vin, SetChargeLimitRequest(chargePercent))
    }
  }

  override suspend fun setChargingAmps(chargingAmps: Int): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          setChargingAmpsAction = setChargingAmpsAction { this.chargingAmps = chargingAmps }
        }
      }
    ) {
      vehicleCommandsApi.setChargingAmps(vin, SetChargingAmpsRequest(chargingAmps))
    }
  }

  override suspend fun setClimateKeeperMode(
    mode: ClimateKeeperMode,
  ): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          hvacClimateKeeperAction = hvacClimateKeeperAction { climateKeeperAction = mode.action }
        }
      }
    ) {
      vehicleCommandsApi.setClimateKeeperMode(vin, SetClimateKeeperModeRequest(mode.value))
    }
  }

  override suspend fun setCabinOverheatProtectionTemperature(
    temperature: VehicleCommands.CopTemperature
  ): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          setCopTempAction = setCopTempAction { copActivationTemp = temperature.copActivationTemp }
        }
      }
    ) {
      vehicleCommandsApi.setCopTemp(vin, SetCopTempRequest(temperature.value))
    }
  }

  override suspend fun enablePinToDrive(pin: String) = pinToDrive(enable = true, pin)

  override suspend fun disablePinToDrive(pin: String) = pinToDrive(enable = false, pin)

  private suspend fun pinToDrive(
    enable: Boolean,
    pin: String,
  ): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          vehicleControlSetPinToDriveAction = vehicleControlSetPinToDriveAction {
            on = enable
            password = pin
          }
        }
      }
    ) {
      vehicleCommandsApi.setPinToDrive(vin, SetPinToDriveRequest(enable, pin))
    }
  }

  override suspend fun enableMaxPreconditioning(manualOverride: Boolean?) =
    maximumPreconditioning(on = true, manualOverride)

  override suspend fun disableMaxPreconditioning() =
    maximumPreconditioning(on = false, manualOverride = false)

  private suspend fun maximumPreconditioning(
    on: Boolean,
    manualOverride: Boolean?
  ): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          hvacSetPreconditioningMaxAction = hvacSetPreconditioningMaxAction {
            this.on = on
            if (manualOverride != null) this.manualOverride = manualOverride
          }
        }
      }
    ) {
      vehicleCommandsApi.setPreconditioningMax(
        vin,
        SetPreconditioningMaxRequest(on, manualOverride)
      )
    }
  }

  override suspend fun enableScheduledCharging(minutesAfterMidnight: Int) =
    scheduledCharging(on = true, minutesAfterMidnight)

  override suspend fun disableScheduledCharging(minutesAfterMidnight: Int) =
    scheduledCharging(on = false, minutesAfterMidnight)

  private suspend fun scheduledCharging(
    on: Boolean,
    minutesAfterMidnight: Int? = null
  ): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          scheduledChargingAction = scheduledChargingAction {
            enabled = on
            minutesAfterMidnight?.let { chargingTime = minutesAfterMidnight }
          }
        }
      }
    ) {
      vehicleCommandsApi.setScheduledCharging(
        vin,
        SetScheduledChargingRequest(on, minutesAfterMidnight)
      )
    }
  }

  override suspend fun enableScheduledDeparture(
    minutesAfterMidnight: Int,
    preconditioningEnabled: Boolean,
    preconditioningWeekdaysOnly: Boolean,
    offPeakChargingEnabled: Boolean,
    offPeakChargingWeekdaysOnly: Boolean,
    endOffPeakTime: Int
  ) =
    scheduledDeparture(
      on = true,
      minutesAfterMidnight,
      preconditioningEnabled,
      preconditioningWeekdaysOnly,
      offPeakChargingEnabled,
      offPeakChargingWeekdaysOnly,
      endOffPeakTime
    )

  override suspend fun disableScheduledDeparture() = scheduledDeparture(on = false)

  private suspend fun scheduledDeparture(
    on: Boolean,
    minutesAfterMidnight: Int? = null,
    preconditioningEnabled: Boolean? = null,
    preconditioningWeekdaysOnly: Boolean? = null,
    offPeakChargingEnabled: Boolean? = null,
    offPeakChargingWeekdaysOnly: Boolean? = null,
    endOffPeakTime: Int? = null
  ): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          scheduledDepartureAction = scheduledDepartureAction {
            enabled = on
            minutesAfterMidnight?.let { departureTime = minutesAfterMidnight }
            if (preconditioningEnabled == true) {
              if (preconditioningWeekdaysOnly == false) {
                preconditioningTimes = preconditioningTimes { allWeek = void {} }
              } else {
                preconditioningTimes = preconditioningTimes { weekdays = void {} }
              }
            } else if (preconditioningEnabled == false) {
              preconditioningTimes = preconditioningTimes {}
            }
            if (offPeakChargingEnabled == true) {
              if (offPeakChargingWeekdaysOnly == false) {
                offPeakChargingTimes = offPeakChargingTimes { allWeek = void {} }
              } else {
                offPeakChargingTimes = offPeakChargingTimes { weekdays = void {} }
              }
            } else if (offPeakChargingEnabled == false) {
              offPeakChargingTimes = offPeakChargingTimes {}
            }
            endOffPeakTime?.let { offPeakHoursEndTime = endOffPeakTime }
          }
        }
      }
    ) {
      vehicleCommandsApi.setScheduledDeparture(
        vin,
        SetScheduledDepartureRequest(
          on,
          minutesAfterMidnight,
          preconditioningEnabled,
          preconditioningWeekdaysOnly,
          offPeakChargingEnabled,
          offPeakChargingWeekdaysOnly,
          endOffPeakTime
        )
      )
    }
  }

  override suspend fun enableSentryMode() = sentryMode(on = true)

  override suspend fun disableSentryMode() = sentryMode(on = false)

  private suspend fun sentryMode(
    on: Boolean,
  ): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          vehicleControlSetSentryModeAction = vehicleControlSetSentryModeAction { this.on = on }
        }
      }
    ) {
      vehicleCommandsApi.setSentryMode(vin, SetSentryModeRequest(on))
    }
  }

  override suspend fun setTemperaturesF(
    driverTempF: Float,
    passengerTempF: Float
  ): Result<VehicleCommandResponse> {
    fun Float.fToC(): Float {
      return ((this - 32) * (5f / 9f) * 2).roundToInt() / 2f
    }

    return setTemperaturesC(driverTempF.fToC(), passengerTempF.fToC())
  }

  override suspend fun setTemperaturesC(
    driverTempC: Float,
    passengerTempC: Float
  ): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          hvacTemperatureAdjustmentAction = hvacTemperatureAdjustmentAction {
            driverTempCelsius = driverTempC
            passengerTempCelsius = passengerTempC
            level = temperature { tEMPMAX = void {} }
          }
        }
      }
    ) {
      vehicleCommandsApi.setTemps(vin, SetTempsRequest(driverTempC, passengerTempC))
    }
  }

  override suspend fun enableValetMode(pin: String) = valetMode(on = true, pin)

  override suspend fun disableValetMode() = valetMode(on = false)

  private suspend fun valetMode(on: Boolean, pin: String? = null): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          vehicleControlSetValetModeAction = vehicleControlSetValetModeAction {
            this.on = on
            pin?.let { password = pin }
          }
        }
      }
    ) {
      vehicleCommandsApi.setValetMode(vin, SetValetModeRequest(on, password = null))
    }
  }

  override suspend fun setVehicleName(name: String): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          setVehicleNameAction = setVehicleNameAction { vehicleName = name }
        }
      }
    ) {
      vehicleCommandsApi.setVehicleName(vin, SetVehicleNameRequest(name))
    }
  }

  override suspend fun activateSpeedLimit(pin: String): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          drivingSpeedLimitAction = drivingSpeedLimitAction {
            activate = true
            this.pin = pin
          }
        }
      }
    ) {
      vehicleCommandsApi.speedLimitActivate(vin, SpeedLimitActivateRequest(pin))
    }
  }

  override suspend fun clearSpeedLimitPin(pin: String): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          drivingClearSpeedLimitPinAction = drivingClearSpeedLimitPinAction { this.pin = pin }
        }
      }
    ) {
      vehicleCommandsApi.speedLimitClearPin(vin, SpeedLimitClearPinRequest(pin))
    }
  }

  override suspend fun adminClearSpeedLimitPin(): Result<VehicleCommandResponse> {
    // Does not require Command Protocol
    return executeCommand(action = null) { vehicleCommandsApi.speedLimitClearPinAdmin(vin) }
  }

  override suspend fun deactivateSpeedLimit(pin: String): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          drivingSpeedLimitAction = drivingSpeedLimitAction {
            activate = false
            this.pin = pin
          }
        }
      }
    ) {
      vehicleCommandsApi.speedLimitDeactivate(vin, SpeedLimitDeactivateRequest(pin))
    }
  }

  override suspend fun setSpeedLimit(limitMph: Double): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          drivingSetSpeedLimitAction = drivingSetSpeedLimitAction { this.limitMph = limitMph }
        }
      }
    ) {
      vehicleCommandsApi.speedLimitSetLimit(vin, SpeedLimitSetLimitRequest(limitMph))
    }
  }

  override suspend fun setSunroofState(
    state: VehicleCommands.SunroofState
  ): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          vehicleControlSunroofOpenCloseAction = vehicleControlSunroofOpenCloseAction {
            when (state) {
              VehicleCommands.SunroofState.OPEN -> open = void {}
              VehicleCommands.SunroofState.CLOSE -> close = void {}
              VehicleCommands.SunroofState.VENT -> vent = void {}
            }
          }
        }
      }
    ) {
      vehicleCommandsApi.sunRoofControl(vin, SunRoofControlRequest(state.name.lowercase()))
    }
  }

  override suspend fun takeDrivenote(note: String): Result<VehicleCommandResponse> {
    // Does not require Command Protocol
    return executeCommand(action = null) {
      vehicleCommandsApi.takeDrivenote(vin, TakeDrivenoteRequest(note))
    }
  }

  override suspend fun triggerHomelink(
    latitude: Float,
    longitude: Float
  ): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          vehicleControlTriggerHomelinkAction = vehicleControlTriggerHomelinkAction {
            location = latLong {
              this.latitude = latitude
              this.longitude = longitude
            }
          }
        }
      }
    ) {
      vehicleCommandsApi.triggerHomelink(vin, TriggerHomelinkRequest(latitude, longitude))
    }
  }

  override suspend fun setUpcomingCalendarEntries(
    calendarData: String
  ): Result<VehicleCommandResponse> {
    // Does not require Command Protocol
    return executeCommand(action = null) {
      vehicleCommandsApi.upcomingCalendarEntries(vin, UpcomingCalendarEntriesRequest(calendarData))
    }
  }

  override suspend fun controlWindows(
    latitude: Float,
    longitude: Float,
    command: WindowCommand
  ): Result<VehicleCommandResponse> {
    return executeCommand(
      action {
        vehicleAction = vehicleAction {
          vehicleControlWindowAction = vehicleControlWindowAction {
            location = latLong {
              this.latitude = latitude
              this.longitude = longitude
            }
            when (command) {
              WindowCommand.VENT -> vent = void {}
              WindowCommand.CLOSE -> close = void {}
            }
          }
        }
      }
    ) {
      vehicleCommandsApi.windowControl(
        vin,
        WindowControlRequest(latitude, longitude, command.name.lowercase())
      )
    }
  }

  override suspend fun supportsCommandSigning(): Result<Boolean> {
    try {
      ensureSessionStarted(Domain.DOMAIN_INFOTAINMENT)
    } catch (e: Exception) {
      Log.e("Could not check if command signing is supported", e)
      return Result.failure(e)
    }
    return Result.success(useCommandProtocol)
  }

  private suspend fun executeCommand(
    action: GeneratedMessageV3?,
    apiCall: suspend VehicleCommandsApi.() -> CommandResponse
  ): Result<VehicleCommandResponse> {
    Log.d("Executing Command")
    if (action != null) {
      val domain =
        when (action) {
          is CarServer.Action -> Domain.DOMAIN_INFOTAINMENT
          is Vcsec.UnsignedMessage -> Domain.DOMAIN_VEHICLE_SECURITY
          else ->
            return Result.failure(
              IllegalArgumentException("Unexpected action: ${action::class.qualifiedName}")
            )
        }
      try {
        val sessionInfo = ensureSessionStarted(domain)
        if (sessionInfo != null && useCommandProtocol) {
          Log.d("Signing command and sending via command protocol")
          return signedCommandSender.signAndSend(action, sessionInfo, clientPublicKey)
        }
      } catch (e: Exception) {
        return Result.failure(e)
      }
    }

    Log.d("Sending command via legacy API")
    return networkExecutor.execute {
      val response = vehicleCommandsApi.apiCall()
      if (!response.response.result) {
        if (response.response.reason == COULD_NOT_WAKE_BUSES) {
          Log.d("COULD_NOT_WAKE_BUSES")
          throw VehicleTemporarilyUnavailableException()
        } else {
          Log.d("Response failure")
          throw ActionFailureException(response.response.reason)
        }
      }
      Log.d("Command Success")
      response
    }
  }

  private suspend fun ensureSessionStarted(domain: Domain): SessionInfo? {
    Log.d("Starting session for $domain if needed")
    handshakeMutex.withLock {
      if (useCommandProtocol && sessionInfoMap[domain] == null) {
        try {
          val sessionInfo = handshaker.performHandshake(vin, domain, sharedSecretFetcher)
          sessionInfoMap[domain] = sessionInfo
          return sessionInfo
        } catch (httpException: HttpException) {
          if (httpException.code() == 422) {
            Log.d("Vehicle does not support the command protocol")
            useCommandProtocol = false
          } else {
            throw httpException
          }
        }
      } else {
        Log.d(
          "Not starting session for $domain.useCommandProtocol: $useCommandProtocol. Has existing session info? ${sessionInfoMap[domain] != null}"
        )
        return sessionInfoMap[domain]
      }
    }
    return sessionInfoMap[domain]
  }

  private companion object {
    private const val COULD_NOT_WAKE_BUSES = "could_not_wake_buses"
  }
}
