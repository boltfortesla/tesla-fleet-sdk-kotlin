package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.RetryConfig
import com.boltfortesla.teslafleetsdk.TestKeys
import com.boltfortesla.teslafleetsdk.commands.CommandAuthenticator
import com.boltfortesla.teslafleetsdk.commands.CommandSignerImpl
import com.boltfortesla.teslafleetsdk.crypto.HmacCalculatorImpl
import com.boltfortesla.teslafleetsdk.encoding.HexCodec.decodeHex
import com.boltfortesla.teslafleetsdk.encoding.TlvEncoderImpl
import com.boltfortesla.teslafleetsdk.fixtures.Constants
import com.boltfortesla.teslafleetsdk.fixtures.Responses
import com.boltfortesla.teslafleetsdk.fixtures.Responses.INFOTAINMENT_COMMAND_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.SECURITY_COMMAND_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.signedCommandJson
import com.boltfortesla.teslafleetsdk.fixtures.fakes.FakeIdentifiers
import com.boltfortesla.teslafleetsdk.handshake.HandshakerImpl
import com.boltfortesla.teslafleetsdk.handshake.SessionInfoAuthenticatorImpl
import com.boltfortesla.teslafleetsdk.handshake.SessionInfoRepositoryImpl
import com.boltfortesla.teslafleetsdk.keys.Pem
import com.boltfortesla.teslafleetsdk.keys.PublicKeyEncoderImpl
import com.boltfortesla.teslafleetsdk.net.JitterFactorCalculatorImpl
import com.boltfortesla.teslafleetsdk.net.NetworkExecutorImpl
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.VehicleCommands.AutoSeat
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.VehicleCommands.ClimateKeeperMode
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.VehicleCommands.CoolerSeat
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.VehicleCommands.CopTemperature
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.VehicleCommands.HeaterSeat
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.VehicleCommands.SeatClimateLevel
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.VehicleCommands.SunroofState
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.VehicleCommands.Trunk
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.VehicleCommands.WindowCommand
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.VehicleEndpointsImpl
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.createVehicleEndpointsApi
import com.google.common.truth.Truth.assertThat
import com.google.protobuf.ByteString
import com.google.protobuf.GeneratedMessageV3
import com.tesla.generated.carserver.common.latLong
import com.tesla.generated.carserver.common.offPeakChargingTimes
import com.tesla.generated.carserver.common.preconditioningTimes
import com.tesla.generated.carserver.common.void
import com.tesla.generated.carserver.server.AutoSeatClimateActionKt
import com.tesla.generated.carserver.server.CarServer.AutoSeatClimateAction.AutoSeatPosition_E
import com.tesla.generated.carserver.server.CarServer.HvacClimateKeeperAction.ClimateKeeperAction_E
import com.tesla.generated.carserver.server.CarServer.HvacSeatCoolerActions.HvacSeatCoolerLevel_E
import com.tesla.generated.carserver.server.CarServer.HvacSeatCoolerActions.HvacSeatCoolerPosition_E
import com.tesla.generated.carserver.server.CarServer.VehicleAction
import com.tesla.generated.carserver.server.HvacSeatCoolerActionsKt
import com.tesla.generated.carserver.server.HvacSeatHeaterActionsKt
import com.tesla.generated.carserver.server.HvacTemperatureAdjustmentActionKt
import com.tesla.generated.carserver.server.action
import com.tesla.generated.carserver.server.autoSeatClimateAction
import com.tesla.generated.carserver.server.chargingSetLimitAction
import com.tesla.generated.carserver.server.chargingStartStopAction
import com.tesla.generated.carserver.server.drivingClearSpeedLimitPinAction
import com.tesla.generated.carserver.server.drivingSetSpeedLimitAction
import com.tesla.generated.carserver.server.drivingSpeedLimitAction
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
import com.tesla.generated.carserver.vehicle.Vehicle.ClimateState.CopActivationTemp
import com.tesla.generated.carserver.vehicle.VehicleStateKt.guestMode
import com.tesla.generated.signatures.Signatures.SignatureType
import com.tesla.generated.signatures.copy
import com.tesla.generated.signatures.hMACPersonalizedSignatureData
import com.tesla.generated.signatures.keyIdentity
import com.tesla.generated.signatures.signatureData
import com.tesla.generated.universalmessage.UniversalMessage.Domain
import com.tesla.generated.universalmessage.UniversalMessage.RoutableMessage
import com.tesla.generated.universalmessage.copy
import com.tesla.generated.universalmessage.destination
import com.tesla.generated.universalmessage.routableMessage
import com.tesla.generated.vcsec.Vcsec
import com.tesla.generated.vcsec.Vcsec.RKEAction_E
import com.tesla.generated.vcsec.Vcsec.UnsignedMessage
import com.tesla.generated.vcsec.closureMoveRequest
import com.tesla.generated.vcsec.unsignedMessage
import java.util.Base64
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONObject
import org.junit.After
import org.junit.Test

class VehicleCommandsImplTest {
  private val server = MockWebServer()
  private val fakeIdentifiers = FakeIdentifiers()
  private val vehicleEndpointsApi = createVehicleEndpointsApi(server.url("/").toString())
  private val vehicleCommandsApi = createVehicleCommandsApi(server.url("/").toString())
  private val publicKeyEncoder = PublicKeyEncoderImpl()
  private val tlvEncoder = TlvEncoderImpl()
  private val hmacCalculator = HmacCalculatorImpl()
  private val retryConfig = RetryConfig(maxRetries = 0)
  private val jitterFactorCalculator = JitterFactorCalculatorImpl()
  private val networkExecutor = NetworkExecutorImpl(retryConfig, jitterFactorCalculator)
  private val sessionInfoRepository = SessionInfoRepositoryImpl()
  private val sessionInfoAuthenticator = SessionInfoAuthenticatorImpl(tlvEncoder, hmacCalculator)
  private val commandSigner =
    CommandSignerImpl(
      object : CommandAuthenticator {
        override val signatureType = SignatureType.SIGNATURE_TYPE_HMAC

        override fun addAuthenticationData(
          message: RoutableMessage,
          metadata: ByteArray,
          sharedSecret: ByteArray
        ): RoutableMessage {
          return message.copy {
            signatureData =
              message.signatureData.copy {
                hMACPersonalizedData =
                  message.signatureData.hmacPersonalizedData.copy { this.tag = FAKE_SIGNATURE_TAG }
              }
          }
        }
      },
      tlvEncoder,
      publicKeyEncoder,
      fakeIdentifiers
    )
  private val handshaker =
    HandshakerImpl(
      TestKeys.CLIENT_PUBLIC_KEY_BYTES,
      publicKeyEncoder,
      vehicleEndpointsApi,
      sessionInfoAuthenticator,
      fakeIdentifiers,
      networkExecutor,
    )

  private val signedCommandSender =
    SignedCommandSenderImpl(
      commandSigner,
      VehicleEndpointsImpl(Constants.VIN, vehicleEndpointsApi, networkExecutor),
      networkExecutor,
      SessionValidatorImpl(sessionInfoAuthenticator),
      sessionInfoRepository,
      handshaker,
      Constants.VIN,
    )

  private val vehicleCommands =
    VehicleCommandsImpl(
      vin = Constants.VIN,
      Pem(TestKeys.CLIENT_PUBLIC_KEY).byteArray(),
      sharedSecretFetcher = { Constants.SHARED_SECRET.decodeHex() },
      commandProtocolSupported = true,
      HandshakerImpl(
        TestKeys.CLIENT_PUBLIC_KEY_BYTES,
        publicKeyEncoder,
        vehicleEndpointsApi,
        SessionInfoAuthenticatorImpl(tlvEncoder, hmacCalculator),
        fakeIdentifiers,
        networkExecutor
      ),
      vehicleCommandsApi,
      networkExecutor,
      signedCommandSender,
      sessionInfoRepository
    )

  private val commandProtocolUnsupportedVehicleCommands =
    VehicleCommandsImpl(
      vin = Constants.VIN,
      Pem(TestKeys.CLIENT_PUBLIC_KEY).byteArray(),
      sharedSecretFetcher = { Constants.SHARED_SECRET.decodeHex() },
      commandProtocolSupported = false,
      HandshakerImpl(
        TestKeys.CLIENT_PUBLIC_KEY_BYTES,
        publicKeyEncoder,
        vehicleEndpointsApi,
        sessionInfoAuthenticator,
        fakeIdentifiers,
        networkExecutor
      ),
      vehicleCommandsApi,
      networkExecutor,
      signedCommandSender,
      sessionInfoRepository
    )

  @After
  fun tearDown() {
    server.shutdown()
  }

  @Test
  fun actuateTrunk_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/actuate_trunk",
      "{\"which_trunk\":\"front\"}"
    ) {
      actuateTrunk(Trunk.FRONT)
    }
  }

  @Test
  fun actuateTrunk_commandProtocol() {
    testVehicleSecurityCommand(
      unsignedMessage {
        closureMoveRequest = closureMoveRequest {
          rearTrunk = Vcsec.ClosureMoveType_E.CLOSURE_MOVE_TYPE_MOVE
        }
      }
    ) {
      actuateTrunk(Trunk.REAR)
    }
  }

  @Test
  fun actuateTrunk_commandProtocol_commandProtocolNotSupported() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/actuate_trunk",
      "{\"which_trunk\":\"front\"}",
    ) {
      actuateTrunk(Trunk.FRONT)
    }
  }

  @Test
  fun adjustVolume_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/adjust_volume", "{\"volume\":5.0}") {
      adjustVolume(5f)
    }
  }

  @Test
  fun adjustVolume_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction { mediaUpdateVolume = mediaUpdateVolume { volumeAbsoluteFloat = 5f } }
    ) {
      adjustVolume(5f)
    }
  }

  @Test
  fun startAutoConditioning_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/auto_conditioning_start") {
      startAutoConditioning()
    }
  }

  @Test
  fun stopAutoConditioning_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/auto_conditioning_stop") {
      stopAutoConditioning()
    }
  }

  @Test
  fun cancelSoftwareUpdate_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/cancel_software_update") {
      cancelSoftwareUpdate()
    }
  }

  @Test
  fun cancelSoftwareUpdate_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        vehicleControlCancelSoftwareUpdateAction = vehicleControlCancelSoftwareUpdateAction {}
      }
    ) {
      cancelSoftwareUpdate()
    }
  }

  @Test
  fun setChargeMaxRange_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/charge_max_range") { setChargeMaxRange() }
  }

  @Test
  fun setChargeMaxRange_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        chargingStartStopAction = chargingStartStopAction { startMaxRange = void {} }
      }
    ) {
      setChargeMaxRange()
    }
  }

  @Test
  fun closeChargePortDoor_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/charge_port_door_close") {
      closeChargePortDoor()
    }
  }

  @Test
  fun closeChargePortDoor_commandProtocol() {
    testVehicleSecurityCommand(
      unsignedMessage {
        closureMoveRequest = closureMoveRequest {
          chargePort = Vcsec.ClosureMoveType_E.CLOSURE_MOVE_TYPE_CLOSE
        }
      }
    ) {
      closeChargePortDoor()
    }
  }

  @Test
  fun openChargePortDoor_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/charge_port_door_open") {
      openChargePortDoor()
    }
  }

  @Test
  fun openChargePortDoor_commandProtocol() {
    testVehicleSecurityCommand(
      unsignedMessage {
        closureMoveRequest = closureMoveRequest {
          chargePort = Vcsec.ClosureMoveType_E.CLOSURE_MOVE_TYPE_OPEN
        }
      }
    ) {
      openChargePortDoor()
    }
  }

  @Test
  fun setChargeStandard_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/charge_standard") { setChargeStandard() }
  }

  @Test
  fun setChargeStandard_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        chargingStartStopAction = chargingStartStopAction { startStandard = void {} }
      }
    ) {
      setChargeStandard()
    }
  }

  @Test
  fun startCharging_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/charge_start") { startCharging() }
  }

  @Test
  fun startCharging_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction { chargingStartStopAction = chargingStartStopAction { start = void {} } }
    ) {
      startCharging()
    }
  }

  @Test
  fun stopCharging_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/charge_stop") { stopCharging() }
  }

  @Test
  fun stopCharging_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction { chargingStartStopAction = chargingStartStopAction { stop = void {} } }
    ) {
      stopCharging()
    }
  }

  @Test
  fun adminClearPinToDrive_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/clear_pin_to_drive_admin",
      commandProtocolSupported = false
    ) {
      adminClearPinToDrive()
    }
  }

  @Test
  fun lockDoors_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/door_lock") { lockDoors() }
  }

  @Test
  fun lockDoors_commandProtocol() {
    testVehicleSecurityCommand(unsignedMessage { rKEAction = RKEAction_E.RKE_ACTION_LOCK }) {
      lockDoors()
    }
  }

  @Test
  fun unlockDoors_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/door_unlock") { unlockDoors() }
  }

  @Test
  fun unlockDoors_commandProtocol() {
    testVehicleSecurityCommand(unsignedMessage { rKEAction = RKEAction_E.RKE_ACTION_UNLOCK }) {
      unlockDoors()
    }
  }

  @Test
  fun eraseUserData_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/erase_user_data",
      commandProtocolSupported = false
    ) {
      eraseUserData()
    }
  }

  @Test
  fun flashLights_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/flash_lights") { flashLights() }
  }

  @Test
  fun flashLights_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction { vehicleControlFlashLightsAction = vehicleControlFlashLightsAction {} }
    ) {
      flashLights()
    }
  }

  @Test
  fun enableGuestMode_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/guest_mode", "{\"enable\":true}") {
      enableGuestMode()
    }
  }

  @Test
  fun enableGuestMode_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction { guestModeAction = guestMode { guestModeActive = true } }
    ) {
      enableGuestMode()
    }
  }

  @Test
  fun disableGuestMode_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/guest_mode", "{\"enable\":false}") {
      disableGuestMode()
    }
  }

  @Test
  fun disableGuestMode_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction { guestModeAction = guestMode { guestModeActive = false } }
    ) {
      disableGuestMode()
    }
  }

  @Test
  fun honkHorn_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/honk_horn") { honkHorn() }
  }

  @Test
  fun honkHorn_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction { vehicleControlHonkHornAction = vehicleControlHonkHornAction {} }
    ) {
      honkHorn()
    }
  }

  @Test
  fun mediaNextFavorite_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/media_next_fav") { mediaNextFavorite() }
  }

  @Test
  fun mediaNextFavorite_commandProtocol() {
    testInfotainmentCommand(vehicleAction { mediaNextFavorite = mediaNextFavorite {} }) {
      mediaNextFavorite()
    }
  }

  @Test
  fun mediaNextTrack_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/media_next_track") { mediaNextTrack() }
  }

  @Test
  fun mediaNextTrack_commandProtocol() {
    testInfotainmentCommand(vehicleAction { mediaNextTrack = mediaNextTrack {} }) {
      mediaNextTrack()
    }
  }

  @Test
  fun mediaPreviousFavorite_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/media_prev_fav") {
      mediaPreviousFavorite()
    }
  }

  @Test
  fun mediaPreviousFavorite_commandProtocol() {
    testInfotainmentCommand(vehicleAction { mediaPreviousFavorite = mediaPreviousFavorite {} }) {
      mediaPreviousFavorite()
    }
  }

  @Test
  fun mediaPreviousTrack_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/media_prev_track") {
      mediaPreviousTrack()
    }
  }

  @Test
  fun mediaPreviousTrack_commandProtocol() {
    testInfotainmentCommand(vehicleAction { mediaPreviousTrack = mediaPreviousTrack {} }) {
      mediaPreviousTrack()
    }
  }

  @Test
  fun mediaTogglePlayback_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/media_toggle_playback") {
      mediaTogglePlayback()
    }
  }

  @Test
  fun mediaTogglePlayback_commandProtocol() {
    testInfotainmentCommand(vehicleAction { mediaPlayAction = mediaPlayAction {} }) {
      mediaTogglePlayback()
    }
  }

  @Test
  fun mediaVolumeDown_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/media_volume_down") { mediaVolumeDown() }
  }

  @Test
  fun mediaVolumeDown_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction { mediaUpdateVolume = mediaUpdateVolume { volumeDelta = -1 } }
    ) {
      mediaVolumeDown()
    }
  }

  @Test
  fun sendUrl_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/share",
      "{\"type\":\"share_ext_content_raw\",\"value\":{\"android.intent.extra.TEXT\":\"https://youtube.com\"},\"locale\":\"en-US\",\"timestamp_ms\":\"12345\"}",
      commandProtocolSupported = false
    ) {
      sendUrl("https://youtube.com", "en-US", "12345")
    }
  }

  @Test
  fun sendNavigationGps_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/navigation_gps_request",
      "{\"lat\":30.0,\"lon\":-30.0,\"order\":0}",
      commandProtocolSupported = false
    ) {
      sendNavigationGps(30.0f, -30.0f, 0)
    }
  }

  @Test
  fun sendNavigationDestination_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/navigation_request",
      "{\"type\":\"share_ext_content_raw\",\"value\":{\"android.intent.extra.TEXT\":\"value\"},\"locale\":\"locale\",\"timestamp_ms\":\"timestampMs\"}",
      commandProtocolSupported = false
    ) {
      sendNavigationDestination("value", "locale", "timestampMs")
    }
  }

  @Test
  fun sendNavigationSupercharger_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/navigation_sc_request",
      "{\"id\":12345,\"order\":0}",
      commandProtocolSupported = false
    ) {
      sendNavigationSupercharger(12345, 0)
    }
  }

  @Test
  fun enableAutomaticSeatClimateControl_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/remote_auto_seat_climate_request",
      "{\"auto_seat_position\":0,\"auto_climate_on\":true}"
    ) {
      enableAutomaticSeatClimateControl(AutoSeat.FRONT_LEFT)
    }
  }

  @Test
  fun enableAutomaticSeatClimateControl_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        autoSeatClimateAction = autoSeatClimateAction {
          carseat +=
            AutoSeatClimateActionKt.carSeat {
              seatPosition = AutoSeat.FRONT_LEFT.position
              on = true
            }
        }
      }
    ) {
      enableAutomaticSeatClimateControl(AutoSeat.FRONT_LEFT)
    }
  }

  @Test
  fun disableAutomaticSeatClimateControl_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/remote_auto_seat_climate_request",
      "{\"auto_seat_position\":0,\"auto_climate_on\":false}"
    ) {
      disableAutomaticSeatClimateControl(AutoSeat.FRONT_LEFT)
    }
  }

  @Test
  fun disableAutomaticSeatClimateControl_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        autoSeatClimateAction = autoSeatClimateAction {
          carseat +=
            AutoSeatClimateActionKt.carSeat {
              seatPosition = AutoSeatPosition_E.AutoSeatPosition_FrontRight
              on = false
            }
        }
      }
    ) {
      disableAutomaticSeatClimateControl(AutoSeat.FRONT_RIGHT)
    }
  }

  @Test
  fun enableAutomaticSteeringWheelClimateControl_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/remote_auto_steering_wheel_heat_climate_request",
      "{\"on\":true}"
    ) {
      enableAutomaticSteeringWheelClimateControl()
    }
  }

  @Test
  fun disableAutomaticSteeringWheelClimateControl_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/remote_auto_steering_wheel_heat_climate_request",
      "{\"on\":false}"
    ) {
      disableAutomaticSteeringWheelClimateControl()
    }
  }

  @Test
  fun remoteBoombox_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/remote_boombox",
      "{\"sound\":1}",
      commandProtocolSupported = false
    ) {
      remoteBoombox(1)
    }
  }

  @Test
  fun setSeatCooler_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/remote_seat_cooler_request",
      "{\"seat_position\":0,\"seat_cooler_level\":3}"
    ) {
      setSeatCooler(CoolerSeat.FRONT_LEFT, SeatClimateLevel.HIGH)
    }
  }

  @Test
  fun setSeatCooler_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        hvacSeatCoolerActions = hvacSeatCoolerActions {
          hvacSeatCoolerAction +=
            HvacSeatCoolerActionsKt.hvacSeatCoolerAction {
              seatPosition = HvacSeatCoolerPosition_E.HvacSeatCoolerPosition_FrontLeft
              seatCoolerLevel = HvacSeatCoolerLevel_E.HvacSeatCoolerLevel_Med
            }
        }
      }
    ) {
      setSeatCooler(CoolerSeat.FRONT_LEFT, SeatClimateLevel.MEDIUM)
    }
  }

  @Test
  fun setSeatHeater_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/remote_seat_heater_request",
      "{\"seat_position\":0,\"level\":3}"
    ) {
      setSeatHeater(HeaterSeat.FRONT_LEFT, SeatClimateLevel.HIGH)
    }
  }

  @Test
  fun setSeatHeater_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        hvacSeatHeaterActions = hvacSeatHeaterActions {
          hvacSeatHeaterAction +=
            HvacSeatHeaterActionsKt.hvacSeatHeaterAction {
              cARSEATREARLEFTBACK = void {}
              sEATHEATERMED = void {}
            }
        }
      }
    ) {
      setSeatHeater(HeaterSeat.REAR_LEFT_BACK, SeatClimateLevel.MEDIUM)
    }
  }

  @Test
  fun remoteStart_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/remote_start_drive",
    ) {
      remoteStart()
    }
  }

  @Test
  fun remoteStart_commandProtocol() {
    testVehicleSecurityCommand(
      unsignedMessage { rKEAction = RKEAction_E.RKE_ACTION_REMOTE_DRIVE }
    ) {
      remoteStart()
    }
  }

  @Test
  fun setSteeringWheelHeatLevel_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/remote_steering_wheel_heat_level_request",
      "{\"level\":3}",
      commandProtocolSupported = false
    ) {
      setSteeringWheelHeatLevel(SeatClimateLevel.HIGH)
    }
  }

  @Test
  fun enableSteeringWheelHeater_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/remote_steering_wheel_heater_request",
      "{\"on\":true}"
    ) {
      enableSteeringWheelHeater()
    }
  }

  @Test
  fun enableSteeringWheelHeater_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        hvacSteeringWheelHeaterAction = hvacSteeringWheelHeaterAction { powerOn = true }
      }
    ) {
      enableSteeringWheelHeater()
    }
  }

  @Test
  fun disableSteeringWheelHeater_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/remote_steering_wheel_heater_request",
      "{\"on\":false}"
    ) {
      disableSteeringWheelHeater()
    }
  }

  @Test
  fun disableSteeringWheelHeater_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        hvacSteeringWheelHeaterAction = hvacSteeringWheelHeaterAction { powerOn = false }
      }
    ) {
      disableSteeringWheelHeater()
    }
  }

  @Test
  fun resetPinToDrive_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/reset_pin_to_drive_pin") {
      resetPinToDrive()
    }
  }

  @Test
  fun resetPinToDrive_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction { vehicleControlResetPinToDriveAction = vehicleControlResetPinToDriveAction {} }
    ) {
      resetPinToDrive()
    }
  }

  @Test
  fun resetValetPin_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/reset_valet_pin") { resetValetPin() }
  }

  @Test
  fun resetValetPin_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction { vehicleControlResetValetPinAction = vehicleControlResetValetPinAction {} }
    ) {
      resetValetPin()
    }
  }

  @Test
  fun scheduleSoftwareUpdate_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/schedule_software_update",
      "{\"offset_sec\":1234}"
    ) {
      scheduleSoftwareUpdate(1234)
    }
  }

  @Test
  fun scheduleSoftwareUpdate_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        vehicleControlScheduleSoftwareUpdateAction = vehicleControlScheduleSoftwareUpdateAction {
          offsetSec = 300
        }
      }
    ) {
      scheduleSoftwareUpdate(300)
    }
  }

  @Test
  fun enableBioweaponDefenseMode_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/set_bioweapon_mode",
      "{\"on\":true,\"manual_override\":false}"
    ) {
      enableBioweaponDefenseMode(false)
    }
  }

  @Test
  fun enableBioweaponDefenseMode_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        hvacBioweaponModeAction = hvacBioweaponModeAction {
          on = true
          manualOverride = false
        }
      }
    ) {
      enableBioweaponDefenseMode(manualOverride = false)
    }
  }

  @Test
  fun disableBioweaponDefenseMode_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/set_bioweapon_mode",
      "{\"on\":false,\"manual_override\":false}"
    ) {
      disableBioweaponDefenseMode()
    }
  }

  @Test
  fun disableBioweaponDefenseMode_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction { hvacBioweaponModeAction = hvacBioweaponModeAction { on = false } }
    ) {
      disableBioweaponDefenseMode()
    }
  }

  @Test
  fun enableCabinOverheatProtection_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/set_cabin_overheat_protection",
      "{\"on\":true,\"fan_only\":true}"
    ) {
      enableCabinOverheatProtection(true)
    }
  }

  @Test
  fun enableCabinOverheatProtection_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        setCabinOverheatProtectionAction = setCabinOverheatProtectionAction {
          on = true
          fanOnly = true
        }
      }
    ) {
      enableCabinOverheatProtection(fanOnly = true)
    }
  }

  @Test
  fun disableCabinOverheatProtection_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/set_cabin_overheat_protection",
      "{\"on\":false,\"fan_only\":true}"
    ) {
      disableCabinOverheatProtection(true)
    }
  }

  @Test
  fun disableCabinOverheatProtection_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        setCabinOverheatProtectionAction = setCabinOverheatProtectionAction {
          on = false
          fanOnly = true
        }
      }
    ) {
      disableCabinOverheatProtection(fanOnly = true)
    }
  }

  @Test
  fun setChargeLimit_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/set_charge_limit", "{\"percent\":25}") {
      setChargeLimit(25)
    }
  }

  @Test
  fun setChargeLimit_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction { chargingSetLimitAction = chargingSetLimitAction { percent = 35 } }
    ) {
      setChargeLimit(35)
    }
  }

  @Test
  fun setChargingAmps_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/set_charging_amps",
      "{\"charging_amps\":32}"
    ) {
      setChargingAmps(32)
    }
  }

  @Test
  fun setChargingAmps_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction { setChargingAmpsAction = setChargingAmpsAction { chargingAmps = 32 } }
    ) {
      setChargingAmps(32)
    }
  }

  @Test
  fun setClimateKeeperMode_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/set_climate_keeper_mode",
      "{\"climate_keeper_mode\":3}"
    ) {
      setClimateKeeperMode(ClimateKeeperMode.CAMP)
    }
  }

  @Test
  fun setClimateKeeperMode_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        hvacClimateKeeperAction = hvacClimateKeeperAction {
          climateKeeperAction = ClimateKeeperAction_E.ClimateKeeperAction_Dog
        }
      }
    ) {
      setClimateKeeperMode(ClimateKeeperMode.DOG)
    }
  }

  @Test
  fun setCabinOverheatProtectionTemperature_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/set_cop_temp",
      "{\"cop_temp\":${CopTemperature.MEDIUM.value}}"
    ) {
      setCabinOverheatProtectionTemperature(CopTemperature.MEDIUM)
    }
  }

  @Test
  fun setCabinOverheatProtectionTemperature_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        setCopTempAction = setCopTempAction {
          copActivationTemp = CopActivationTemp.CopActivationTempLow
        }
      }
    ) {
      setCabinOverheatProtectionTemperature(CopTemperature.LOW)
    }
  }

  @Test
  fun enablePinToDrive_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/set_pin_to_drive",
      "{\"on\":true,\"password\":\"1234\"}"
    ) {
      enablePinToDrive("1234")
    }
  }

  @Test
  fun enablePinToDrive_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        vehicleControlSetPinToDriveAction = vehicleControlSetPinToDriveAction {
          on = true
          password = "1234"
        }
      }
    ) {
      enablePinToDrive("1234")
    }
  }

  @Test
  fun disablePinToDrive_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/set_pin_to_drive",
      "{\"on\":false,\"password\":\"1234\"}"
    ) {
      disablePinToDrive("1234")
    }
  }

  @Test
  fun disablePinToDrive_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        vehicleControlSetPinToDriveAction = vehicleControlSetPinToDriveAction {
          on = false
          password = "4321"
        }
      }
    ) {
      disablePinToDrive("4321")
    }
  }

  @Test
  fun enableMaximumPreconditioning_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/set_preconditioning_max",
      "{\"on\":true,\"manual_override\":true}"
    ) {
      enableMaxPreconditioning(true)
    }
  }

  @Test
  fun enableMaximumPreconditioning_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        hvacSetPreconditioningMaxAction = hvacSetPreconditioningMaxAction { on = true }
      }
    ) {
      enableMaxPreconditioning(manualOverride = null)
    }
  }

  @Test
  fun disableMaxPreconditioning_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/set_preconditioning_max",
      "{\"on\":false,\"manual_override\":false}"
    ) {
      disableMaxPreconditioning()
    }
  }

  @Test
  fun disableMaximumPreconditioning_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        hvacSetPreconditioningMaxAction = hvacSetPreconditioningMaxAction { on = false }
      }
    ) {
      disableMaxPreconditioning()
    }
  }

  @Test
  fun enableScheduledCharging_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/set_scheduled_charging",
      "{\"enable\":true,\"time\":120}"
    ) {
      enableScheduledCharging(120)
    }
  }

  @Test
  fun enableScheduledCharging_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        scheduledChargingAction = scheduledChargingAction {
          enabled = true
          chargingTime = 123
        }
      }
    ) {
      enableScheduledCharging(123)
    }
  }

  @Test
  fun disableScheduledCharging_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/set_scheduled_charging",
      "{\"enable\":false,\"time\":120}"
    ) {
      disableScheduledCharging(120)
    }
  }

  @Test
  fun disableScheduledCharging_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        scheduledChargingAction = scheduledChargingAction {
          enabled = false
          chargingTime = 123
        }
      }
    ) {
      disableScheduledCharging(123)
    }
  }

  @Test
  fun enableScheduledDeparture_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/set_scheduled_departure",
      "{\"enable\":true,\"departure_time\":120,\"preconditioning_enabled\":true,\"preconditioning_weekdays_only\":true,\"off_peak_charging_enabled\":true,\"off_peak_charging_weekdays_only\":false,\"end_off_peak_time\":210}"
    ) {
      enableScheduledDeparture(120, true, true, true, false, 210)
    }
  }

  @Test
  fun enableScheduledDeparture_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        scheduledDepartureAction = scheduledDepartureAction {
          enabled = true
          departureTime = 120
          preconditioningTimes = preconditioningTimes { weekdays = void {} }
          offPeakChargingTimes = offPeakChargingTimes {}
          offPeakHoursEndTime = 210
        }
      }
    ) {
      enableScheduledDeparture(120, true, true, false, false, 210)
    }
  }

  @Test
  fun disableScheduledDeparture_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/set_scheduled_departure",
      "{\"enable\":false}"
    ) {
      disableScheduledDeparture()
    }
  }

  @Test
  fun disableScheduledDeparture_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction { scheduledDepartureAction = scheduledDepartureAction { enabled = false } }
    ) {
      disableScheduledDeparture()
    }
  }

  @Test
  fun enableSentryMode_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/set_sentry_mode", "{\"on\":true}") {
      enableSentryMode()
    }
  }

  @Test
  fun enableSentryMode_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        vehicleControlSetSentryModeAction = vehicleControlSetSentryModeAction { on = true }
      }
    ) {
      enableSentryMode()
    }
  }

  @Test
  fun disableSentryMode_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/set_sentry_mode", "{\"on\":false}") {
      disableSentryMode()
    }
  }

  @Test
  fun disableSentryMode_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        vehicleControlSetSentryModeAction = vehicleControlSetSentryModeAction { on = false }
      }
    ) {
      disableSentryMode()
    }
  }

  @Test
  fun setTemperaturesF_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/set_temps",
      "{\"driver_temp\":21.0,\"passenger_temp\":22.0}"
    ) {
      setTemperaturesF(70f, 72f)
    }
  }

  @Test
  fun setTemperaturesF_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        hvacTemperatureAdjustmentAction = hvacTemperatureAdjustmentAction {
          driverTempCelsius = 18.5f
          passengerTempCelsius = 24f
          level = HvacTemperatureAdjustmentActionKt.temperature { tEMPMAX = void {} }
        }
      }
    ) {
      setTemperaturesF(65f, 75f)
    }
  }

  @Test
  fun setTemperaturesC_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/set_temps",
      "{\"driver_temp\":19.0,\"passenger_temp\":20.0}"
    ) {
      setTemperaturesC(19f, 20f)
    }
  }

  @Test
  fun setTemperaturesC_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        hvacTemperatureAdjustmentAction = hvacTemperatureAdjustmentAction {
          driverTempCelsius = 21f
          passengerTempCelsius = 18.5f
          level = HvacTemperatureAdjustmentActionKt.temperature { tEMPMAX = void {} }
        }
      }
    ) {
      setTemperaturesC(21f, 18.5f)
    }
  }

  @Test
  fun enableValetMode_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/set_valet_mode", "{\"on\":true}") {
      enableValetMode("1234")
    }
  }

  @Test
  fun enableValetMode_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        vehicleControlSetValetModeAction = vehicleControlSetValetModeAction {
          on = true
          password = "1234"
        }
      }
    ) {
      enableValetMode("1234")
    }
  }

  @Test
  fun disableValetMode_nonCommandProtocol() {
    testApiCall("/api/1/vehicles/${Constants.VIN}/command/set_valet_mode", "{\"on\":false}") {
      disableValetMode()
    }
  }

  @Test
  fun disableValetMode_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        vehicleControlSetValetModeAction = vehicleControlSetValetModeAction { on = false }
      }
    ) {
      disableValetMode()
    }
  }

  @Test
  fun setVehicleName_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/set_vehicle_name",
      "{\"vehicle_name\":\"Vehicle Name\"}"
    ) {
      setVehicleName("Vehicle Name")
    }
  }

  @Test
  fun setVehicleName_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction { setVehicleNameAction = setVehicleNameAction { vehicleName = "New Name" } }
    ) {
      setVehicleName("New Name")
    }
  }

  @Test
  fun activateSpeedLimit_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/speed_limit_activate",
      "{\"pin\":\"1234\"}"
    ) {
      activateSpeedLimit("1234")
    }
  }

  @Test
  fun activateSpeedLimit_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        drivingSpeedLimitAction = drivingSpeedLimitAction {
          activate = true
          pin = "1234"
        }
      }
    ) {
      activateSpeedLimit("1234")
    }
  }

  @Test
  fun clearSpeedLimitPin_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/speed_limit_clear_pin",
      "{\"pin\":\"1234\"}",
      commandProtocolSupported = false
    ) {
      clearSpeedLimitPin("1234")
    }
  }

  @Test
  fun clearSpeedLimitPin_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        drivingClearSpeedLimitPinAction = drivingClearSpeedLimitPinAction { pin = "1234" }
      }
    ) {
      clearSpeedLimitPin("1234")
    }
  }

  @Test
  fun adminClearSpeedLimitPin_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/speed_limit_clear_pin_admin",
      commandProtocolSupported = false
    ) {
      adminClearSpeedLimitPin()
    }
  }

  @Test
  fun deactivateSpeedLimit_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/speed_limit_deactivate",
      "{\"pin\":\"1234\"}"
    ) {
      deactivateSpeedLimit("1234")
    }
  }

  @Test
  fun deactivateSpeedLimit_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        drivingSpeedLimitAction = drivingSpeedLimitAction {
          activate = false
          pin = "1234"
        }
      }
    ) {
      deactivateSpeedLimit("1234")
    }
  }

  @Test
  fun setSpeedLimit_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/speed_limit_set_limit",
      "{\"limit_mph\":70.0}"
    ) {
      setSpeedLimit(70.0)
    }
  }

  @Test
  fun setSpeedLimit_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction { drivingSetSpeedLimitAction = drivingSetSpeedLimitAction { limitMph = 65.0 } }
    ) {
      setSpeedLimit(65.0)
    }
  }

  @Test
  fun setSunroofState_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/sun_roof_control",
      "{\"state\":\"vent\"}"
    ) {
      setSunroofState(SunroofState.VENT)
    }
  }

  @Test
  fun setSunroofState_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        vehicleControlSunroofOpenCloseAction = vehicleControlSunroofOpenCloseAction {
          vent = void {}
        }
      }
    ) {
      setSunroofState(SunroofState.VENT)
    }
  }

  @Test
  fun takeDrivenote_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/take_drivenote",
      "{\"note\":\"drive note\"}",
      commandProtocolSupported = false
    ) {
      takeDrivenote("drive note")
    }
  }

  @Test
  fun triggerHomelink_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/trigger_homelink",
      "{\"lat\":30.0,\"lon\":-30.0}"
    ) {
      triggerHomelink(30.0f, -30.0f)
    }
  }

  @Test
  fun triggerHomelink_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        vehicleControlTriggerHomelinkAction = vehicleControlTriggerHomelinkAction {
          location = latLong {
            latitude = 30f
            longitude = -30f
          }
        }
      }
    ) {
      triggerHomelink(30.0f, -30.0f)
    }
  }

  @Test
  fun setUpcomingCalendarEntries_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/upcoming_calendar_entries",
      "{\"calendar_data\":\"calendar entries\"}",
      commandProtocolSupported = false
    ) {
      setUpcomingCalendarEntries("calendar entries")
    }
  }

  @Test
  fun controlWindows_nonCommandProtocol() {
    testApiCall(
      "/api/1/vehicles/${Constants.VIN}/command/window_control",
      "{\"lat\":30.0,\"lon\":-30.0,\"command\":\"close\"}"
    ) {
      controlWindows(30.0f, -30.0f, WindowCommand.CLOSE)
    }
  }

  @Test
  fun controlWindows_commandProtocol() {
    testInfotainmentCommand(
      vehicleAction {
        vehicleControlWindowAction = vehicleControlWindowAction {
          location = latLong {
            latitude = 30f
            longitude = -30f
          }
          vent = void {}
        }
      }
    ) {
      controlWindows(30.0f, -30.0f, WindowCommand.VENT)
    }
  }

  @Test
  fun twoCommandsOneSession_reusesSessionInfo() = runTest {
    server.enqueue(
      MockResponse().setBody(signedCommandJson(Responses.HANDSHAKE_RESPONSE)).setResponseCode(200)
    )
    server.enqueue(
      MockResponse().setResponseCode(200).setBody(signedCommandJson(INFOTAINMENT_COMMAND_RESPONSE))
    )
    server.enqueue(
      MockResponse().setResponseCode(200).setBody(signedCommandJson(INFOTAINMENT_COMMAND_RESPONSE))
    )

    vehicleCommands.flashLights()
    vehicleCommands.flashLights()

    // Handshake, then two flash lights
    assertThat(server.requestCount).isEqualTo(3)
  }

  @Test
  fun twoCommandsDifferentDomainOneSession_fetchesSessionForEachDomainOnce() = runTest {
    server.enqueue(
      MockResponse().setBody(signedCommandJson(Responses.HANDSHAKE_RESPONSE)).setResponseCode(200)
    )
    server.enqueue(
      MockResponse().setResponseCode(200).setBody(signedCommandJson(INFOTAINMENT_COMMAND_RESPONSE))
    )
    server.enqueue(
      MockResponse().setBody(signedCommandJson(Responses.HANDSHAKE_RESPONSE)).setResponseCode(200)
    )
    server.enqueue(
      MockResponse().setResponseCode(200).setBody(signedCommandJson(SECURITY_COMMAND_RESPONSE))
    )
    server.enqueue(
      MockResponse().setResponseCode(200).setBody(signedCommandJson(INFOTAINMENT_COMMAND_RESPONSE))
    )
    server.enqueue(
      MockResponse().setResponseCode(200).setBody(signedCommandJson(SECURITY_COMMAND_RESPONSE))
    )

    vehicleCommands.flashLights()
    vehicleCommands.unlockDoors()
    vehicleCommands.flashLights()
    vehicleCommands.unlockDoors()

    // Handshake, then two flash lights
    assertThat(server.requestCount).isEqualTo(6)
  }

  private fun testApiCall(
    expectedPath: String,
    expectedRequestBody: String = "",
    commandProtocolSupported: Boolean = true,
    action: suspend VehicleCommands.() -> Unit
  ) = runTest {
    if (commandProtocolSupported) server.enqueue(MockResponse().setResponseCode(422))
    server.enqueue(MockResponse().setResponseCode(200).setBody(SUCCESS_JSON_RESPONSE))

    if (commandProtocolSupported) {
      vehicleCommands.action()
    } else {
      commandProtocolUnsupportedVehicleCommands.action()
    }

    // handshake failure
    if (commandProtocolSupported) server.takeRequest()
    val request = server.takeRequest()
    assertThat(request.path).isEqualTo(expectedPath)
    assertThat(request.body.readUtf8()).isEqualTo(expectedRequestBody)
  }

  private fun testInfotainmentCommand(
    expectedAction: VehicleAction,
    action: suspend VehicleCommands.() -> Unit
  ) {
    testCommand(
      action { vehicleAction = expectedAction },
      INFOTAINMENT_COMMAND_RESPONSE,
      Domain.DOMAIN_INFOTAINMENT,
      action
    )
  }

  private fun testVehicleSecurityCommand(
    expectedMessage: UnsignedMessage,
    action: suspend VehicleCommands.() -> Unit
  ) {
    testCommand(expectedMessage, SECURITY_COMMAND_RESPONSE, Domain.DOMAIN_VEHICLE_SECURITY, action)
  }

  private fun testCommand(
    expectedBytesMessage: GeneratedMessageV3,
    response: RoutableMessage,
    domain: Domain,
    action: suspend VehicleCommands.() -> Unit
  ) = runTest {
    server.enqueue(
      MockResponse().setBody(signedCommandJson(Responses.HANDSHAKE_RESPONSE)).setResponseCode(200)
    )
    server.enqueue(MockResponse().setResponseCode(200).setBody(signedCommandJson(response)))

    vehicleCommands.action()

    val handshakeRequest = server.takeRequest()
    val handshakeMessage =
      RoutableMessage.parseFrom(
        Base64.getDecoder()
          .decode(JSONObject(handshakeRequest.body.readUtf8()).get("routable_message") as String)
      )
    assertThat(handshakeMessage.toDestination.domain).isEqualTo(domain)

    val request = server.takeRequest()
    val requestBody = JSONObject(request.body.readUtf8())
    val actualMessage =
      RoutableMessage.parseFrom(
        Base64.getDecoder().decode(requestBody.get("routable_message") as String)
      )

    assertThat(request.path).isEqualTo("/api/1/vehicles/${Constants.VIN}/signed_command")
    val expectedMessage = routableMessage {
      toDestination = destination { this.domain = domain }
      fromDestination = destination {
        routingAddress = ByteString.copyFrom(fakeIdentifiers.routingAddress)
      }
      protobufMessageAsBytes = expectedBytesMessage.toByteString()
      signatureData = signatureData {
        signerIdentity = keyIdentity {
          publicKey = ByteString.fromHex(TestKeys.ENCODED_CLIENT_PUBLIC_KEY_HEX)
        }
        hMACPersonalizedData = hMACPersonalizedSignatureData {
          epoch = ByteString.fromHex(Constants.EPOCH)
          counter = 7
          expiresAt = 2665
          tag = FAKE_SIGNATURE_TAG
        }
      }
      uuid = ByteString.copyFrom(fakeIdentifiers.uuid)
    }
    assertThat(actualMessage).isEqualTo(expectedMessage)
  }

  private companion object {
    const val SUCCESS_JSON_RESPONSE = "{\"result\": true, \"reason\": \"\"}"
    val FAKE_SIGNATURE_TAG = ByteString.copyFrom("FAKE_SIGNATURE_TAG".toByteArray())
  }
}
