package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.Region
import com.boltfortesla.teslafleetsdk.net.api.ApiCreator
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
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SpeedLimitActivateRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SpeedLimitClearPinRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SpeedLimitDeactivateRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SpeedLimitSetLimitRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.SunRoofControlRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.TakeDrivenoteRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.TriggerHomelinkRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.UpcomingCalendarEntriesRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.request.WindowControlRequest
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.response.VehicleCommandResponse.CommandResponse
import okhttp3.OkHttpClient
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

/** Retrofit API for Vehicle Commands. */
internal interface VehicleCommandsApi {
  @POST("/api/1/vehicles/{vehicle_tag}/command/actuate_trunk")
  suspend fun actuateTrunk(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: ActuateTrunkRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/adjust_volume")
  suspend fun adjustVolume(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: AdjustVolumeRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/auto_conditioning_start")
  suspend fun startAutoConditioning(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/auto_conditioning_stop")
  suspend fun stopAutoConditioning(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/cancel_software_update")
  suspend fun cancelSoftwareUpdate(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/charge_max_range")
  suspend fun chargeMaxRange(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/charge_port_door_close")
  suspend fun closeChargePortDoor(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/charge_port_door_open")
  suspend fun openChargePortDoor(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/charge_standard")
  suspend fun chargeStandard(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/charge_start")
  suspend fun startCharging(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/charge_stop")
  suspend fun stopCharging(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/clear_pin_to_drive_admin")
  suspend fun clearPinToDriveAdmin(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/door_lock")
  suspend fun lockDoors(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/door_unlock")
  suspend fun unlockDoors(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/erase_user_data")
  suspend fun eraseUserData(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/flash_lights")
  suspend fun flashLights(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/guest_mode")
  suspend fun setGuestMode(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: GuestModeRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/honk_horn")
  suspend fun honkHorn(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/media_next_fav")
  suspend fun nextFavorite(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/media_next_track")
  suspend fun nextTrack(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/media_prev_fav")
  suspend fun previousFavorite(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/media_prev_track")
  suspend fun previousTrack(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/media_toggle_playback")
  suspend fun toggleMediaPlayback(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/media_volume_down")
  suspend fun decreaseMediaVolume(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/media_volume_up")
  suspend fun increaseMediaVolume(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/share")
  suspend fun shareUrl(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: ShareRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/navigation_gps_request")
  suspend fun startNavigationToCoordinates(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: NavigationGpsRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/navigation_request")
  suspend fun sendNavigationLocation(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: ShareRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/navigation_sc_request")
  suspend fun startNavigationToSupercharger(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: NavigationSuperchargerRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/remote_auto_seat_climate_request")
  suspend fun setAutoSeatClimate(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: RemoteAutoSeatClimateRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/remote_auto_steering_wheel_heat_climate_request")
  suspend fun setAutoSteeringWheelHeat(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: RemoteAutoSteeringWheelHeatClimateRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/remote_boombox")
  suspend fun playBoomboxSound(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: RemoteBoomboxRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/remote_seat_cooler_request")
  suspend fun setSeatCooler(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: RemoteSeatCoolerRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/remote_seat_heater_request")
  suspend fun setSeatHeater(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: RemoteSeatHeaterRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/remote_start_drive")
  suspend fun remoteStart(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/remote_steering_wheel_heat_level_request")
  suspend fun setSteeringWheelHeatLevel(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: RemoteSteeringWheelHeatLevelRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/remote_steering_wheel_heater_request")
  suspend fun setSteeringWheelHeater(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: RemoteSteeringWheelHeaterRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/reset_pin_to_drive_pin")
  suspend fun resetPinToDrivePin(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/reset_valet_pin")
  suspend fun resetValetPin(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/schedule_software_update")
  suspend fun scheduleSoftwareUpdate(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: ScheduleSoftwareUpdateRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/set_bioweapon_mode")
  suspend fun setBioweaponMode(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: SetBioweaponModeRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/set_cabin_overheat_protection")
  suspend fun setCabinOverheatProtection(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: SetCabinOverheatProtectionRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/set_charge_limit")
  suspend fun setChargeLimit(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: SetChargeLimitRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/set_charging_amps")
  suspend fun setChargingAmps(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: SetChargingAmpsRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/set_climate_keeper_mode")
  suspend fun setClimateKeeperMode(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: SetClimateKeeperModeRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/set_cop_temp")
  suspend fun setCopTemp(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: SetCopTempRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/set_pin_to_drive")
  suspend fun setPinToDrive(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: SetPinToDriveRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/set_preconditioning_max")
  suspend fun setPreconditioningMax(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: SetPreconditioningMaxRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/set_scheduled_charging")
  suspend fun setScheduledCharging(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: SetScheduledChargingRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/set_scheduled_departure")
  suspend fun setScheduledDeparture(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: SetScheduledDepartureRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/set_sentry_mode")
  suspend fun setSentryMode(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: SetSentryModeRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/set_temps")
  suspend fun setTemps(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: SetTempsRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/set_valet_mode")
  suspend fun setValetMode(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: SetValetModeRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/set_vehicle_name")
  suspend fun setVehicleName(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: SetVehicleNameRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/speed_limit_activate")
  suspend fun speedLimitActivate(
    @Path("vehicle_tag") vehicleTag: String,
    @Body request: SpeedLimitActivateRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/speed_limit_clear_pin")
  suspend fun speedLimitClearPin(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: SpeedLimitClearPinRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/speed_limit_clear_pin_admin")
  suspend fun speedLimitClearPinAdmin(@Path(VEHICLE_TAG) vehicleTag: String): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/speed_limit_deactivate")
  suspend fun speedLimitDeactivate(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: SpeedLimitDeactivateRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/speed_limit_set_limit")
  suspend fun speedLimitSetLimit(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: SpeedLimitSetLimitRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/sun_roof_control")
  suspend fun sunRoofControl(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: SunRoofControlRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/take_drivenote")
  suspend fun takeDrivenote(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: TakeDrivenoteRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/trigger_homelink")
  suspend fun triggerHomelink(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: TriggerHomelinkRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/upcoming_calendar_entries")
  suspend fun upcomingCalendarEntries(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: UpcomingCalendarEntriesRequest
  ): CommandResponse

  @POST("/api/1/vehicles/{vehicle_tag}/command/window_control")
  suspend fun windowControl(
    @Path(VEHICLE_TAG) vehicleTag: String,
    @Body request: WindowControlRequest
  ): CommandResponse

  companion object {
    private const val VEHICLE_TAG = "vehicle_tag"
  }
}

internal fun createVehicleCommandsApi(
  baseUrl: String = Region.NA_APAC.baseUrl,
  clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
) = ApiCreator.createApi<VehicleCommandsApi>(baseUrl, clientBuilder)
