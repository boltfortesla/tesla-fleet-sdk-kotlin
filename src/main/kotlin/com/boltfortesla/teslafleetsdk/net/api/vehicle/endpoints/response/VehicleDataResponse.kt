package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response

import com.google.gson.annotations.SerializedName

data class VehicleDataResponse(
  val id: Long,
  @SerializedName("user_id") val userId: Long,
  @SerializedName("vehicle_id") val vehicleId: Long,
  val vin: String,
  val color: String?,
  @SerializedName("access_type") val accessType: String,
  @SerializedName("granular_access") val granularAccess: GranularAccess,
  val tokens: List<String>,
  val state: String?,
  @SerializedName("in_service") val inService: Boolean,
  @SerializedName("id_s") val idS: String,
  @SerializedName("calendar_enabled") val calendarEnabled: Boolean,
  @SerializedName("api_version") val apiVersion: Int,
  @SerializedName("backseat_token") val backseatToken: String?,
  @SerializedName("backseat_token_updated_at") val backseatTokenUpdatedAt: String?,
  @SerializedName("ble_autopair_enrolled") val bleAutopairEnrolled: Boolean,
  @SerializedName("charge_state") val chargeState: ChargeState,
  @SerializedName("climate_state") val climateState: ClimateState,
  @SerializedName("drive_state") val driveState: DriveState,
  @SerializedName("gui_settings") val guiSettings: GuiSettings,
  @SerializedName("vehicle_config") val vehicleConfig: VehicleConfig,
  @SerializedName("vehicle_state") val vehicleState: VehicleState,
) {
  data class GranularAccess(
    @SerializedName("hide_private") val hidePrivate: Boolean,
  )

  data class ChargeState(
    @SerializedName("battery_heater_on") val batteryHeaterOn: Boolean,
    @SerializedName("battery_level") val batteryLevel: Int,
    @SerializedName("battery_range") val batteryRange: Double,
    @SerializedName("charge_amps") val chargeAmps: Int,
    @SerializedName("charge_current_request") val chargeCurrentRequest: Int,
    @SerializedName("charge_current_request_max") val chargeCurrentRequestMax: Int,
    @SerializedName("charge_enable_request") val chargeEnableRequest: Boolean,
    @SerializedName("charge_energy_added") val chargeEnergyAdded: Double,
    @SerializedName("charge_limit_soc") val chargeLimitSoc: Int,
    @SerializedName("charge_limit_soc_max") val chargeLimitSocMax: Int,
    @SerializedName("charge_limit_soc_min") val chargeLimitSocMin: Int,
    @SerializedName("charge_limit_soc_std") val chargeLimitSocStd: Int,
    @SerializedName("charge_miles_added_ideal") val chargeMilesAddedIdeal: Double,
    @SerializedName("charge_miles_added_rated") val chargeMilesAddedRated: Double,
    @SerializedName("charge_port_cold_weather_mode") val chargePortColdWeatherMode: Boolean,
    @SerializedName("charge_port_color") val chargePortColor: String,
    @SerializedName("charge_port_door_open") val chargePortDoorOpen: Boolean,
    @SerializedName("charge_port_latch") val chargePortLatch: String,
    @SerializedName("charge_rate") val chargeRate: Double,
    @SerializedName("charger_actual_current") val chargerActualCurrent: Int,
    @SerializedName("charger_phases") val chargerPhases: Int?,
    @SerializedName("charger_pilot_current") val chargerPilotCurrent: Int,
    @SerializedName("charger_power") val chargerPower: Int,
    @SerializedName("charger_voltage") val chargerVoltage: Int,
    @SerializedName("charging_state") val chargingState: String,
    @SerializedName("conn_charge_cable") val connChargeCable: String,
    @SerializedName("est_battery_range") val estBatteryRange: Double,
    @SerializedName("fast_charger_brand") val fastChargerBrand: String,
    @SerializedName("fast_charger_present") val fastChargerPresent: Boolean,
    @SerializedName("fast_charger_type") val fastChargerType: String,
    @SerializedName("ideal_battery_range") val idealBatteryRange: Double,
    @SerializedName("managed_charging_active") val managedChargingActive: Boolean,
    @SerializedName("managed_charging_start_time") val managedChargingStartTime: String?,
    @SerializedName("managed_charging_user_canceled") val managedChargingUserCanceled: Boolean,
    @SerializedName("max_range_charge_counter") val maxRangeChargeCounter: Int,
    @SerializedName("minutes_to_full_charge") val minutesToFullCharge: Int,
    @SerializedName("not_enough_power_to_heat") val notEnoughPowerToHeat: Boolean?,
    @SerializedName("off_peak_charging_enabled") val offPeakChargingEnabled: Boolean,
    @SerializedName("off_peak_charging_times") val offPeakChargingTimes: String,
    @SerializedName("off_peak_hours_end_time") val offPeakHoursEndTime: Int,
    @SerializedName("preconditioning_enabled") val preconditioningEnabled: Boolean,
    @SerializedName("preconditioning_times") val preconditioningTimes: String,
    @SerializedName("scheduled_charging_mode") val scheduledChargingMode: String,
    @SerializedName("scheduled_charging_pending") val scheduledChargingPending: Boolean,
    @SerializedName("scheduled_charging_start_time") val scheduledChargingStartTime: String?,
    @SerializedName("scheduled_charging_start_time_app") val scheduledChargingStartTimeApp: Int?,
    @SerializedName("scheduled_departure_time") val scheduledDepartureTime: Long,
    @SerializedName("scheduled_departure_time_minutes") val scheduledDepartureTimeMinutes: Int,
    @SerializedName("supercharger_session_trip_planner")
    val superchargerSessionTripPlanner: Boolean,
    @SerializedName("time_to_full_charge") val timeToFullCharge: Double,
    val timestamp: Long,
    @SerializedName("trip_charging") val tripCharging: Boolean,
    @SerializedName("usable_battery_level") val usableBatteryLevel: Int,
    @SerializedName("user_charge_enable_request") val userChargeEnableRequest: String?
  )

  data class ClimateState(
    @SerializedName("allow_cabin_overheat_protection") val allowCabinOverheatProtection: Boolean,
    @SerializedName("auto_seat_climate_left") val autoSeatClimateLeft: Boolean,
    @SerializedName("auto_seat_climate_right") val autoSeatClimateRight: Boolean,
    @SerializedName("auto_steering_wheel_heat") val autoSteeringWheelHeat: Boolean,
    @SerializedName("battery_heater") val batteryHeater: Boolean,
    @SerializedName("battery_heater_no_power") val batteryHeaterNoPower: Boolean?,
    @SerializedName("bioweapon_mode") val bioweaponMode: Boolean,
    @SerializedName("cabin_overheat_protection") val cabinOverheatProtection: String,
    @SerializedName("cabin_overheat_protection_actively_cooling")
    val cabinOverheatProtectionActivelyCooling: Boolean,
    @SerializedName("climate_keeper_mode") val climateKeeperMode: String,
    @SerializedName("cop_activation_temperature") val copActivationTemperature: String,
    @SerializedName("defrost_mode") val defrostMode: Int,
    @SerializedName("driver_temp_setting") val driverTempSetting: Int,
    @SerializedName("fan_status") val fanStatus: Int,
    @SerializedName("hvac_auto_request") val hvacAutoRequest: String,
    @SerializedName("inside_temp") val insideTemp: Double,
    @SerializedName("is_auto_conditioning_on") val isAutoConditioningOn: Boolean,
    @SerializedName("is_climate_on") val isClimateOn: Boolean,
    @SerializedName("is_front_defroster_on") val isFrontDefrosterOn: Boolean,
    @SerializedName("is_preconditioning") val isPreconditioning: Boolean,
    @SerializedName("is_rear_defroster_on") val isRearDefrosterOn: Boolean,
    @SerializedName("left_temp_direction") val leftTempDirection: Int,
    @SerializedName("max_avail_temp") val maxAvailTemp: Int,
    @SerializedName("min_avail_temp") val minAvailTemp: Int,
    @SerializedName("outside_temp") val outsideTemp: Double,
    @SerializedName("passenger_temp_setting") val passengerTempSetting: Int,
    @SerializedName("remote_heater_control_enabled") val remoteHeaterControlEnabled: Boolean,
    @SerializedName("right_temp_direction") val rightTempDirection: Int,
    @SerializedName("seat_heater_left") val seatHeaterLeft: Int,
    @SerializedName("seat_heater_rear_center") val seatHeaterRearCenter: Int,
    @SerializedName("seat_heater_rear_left") val seatHeaterRearLeft: Int,
    @SerializedName("seat_heater_rear_right") val seatHeaterRearRight: Int,
    @SerializedName("seat_heater_right") val seatHeaterRight: Int,
    @SerializedName("side_mirror_heaters") val sideMirrorHeaters: Boolean,
    @SerializedName("steering_wheel_heat_level") val steeringWheelHeatLevel: Int,
    @SerializedName("steering_wheel_heater") val steeringWheelHeater: Boolean,
    @SerializedName("supports_fan_only_cabin_overheat_protection")
    val supportsFanOnlyCabinOverheatProtection: Boolean,
    val timestamp: Long,
    @SerializedName("wiper_blade_heater") val wiperBladeHeater: Boolean,
    @SerializedName("smart_preconditioning") val smartPreconditioning: Boolean
  )

  data class DriveState(
    @SerializedName("active_route_latitude") val activeRouteLatitude: Double,
    @SerializedName("active_route_longitude") val activeRouteLongitude: Double,
    @SerializedName("active_route_traffic_minutes_delay") val activeRouteTrafficMinutesDelay: Int,
    @SerializedName("gps_as_of") val gpsAsOf: Long,
    val heading: Int,
    val latitude: Double,
    val longitude: Double,
    @SerializedName("native_latitude") val nativeLatitude: Double,
    @SerializedName("native_location_supported") val nativeLocationSupported: Int,
    @SerializedName("native_longitude") val nativeLongitude: Double,
    @SerializedName("native_type") val nativeType: String,
    val power: Int,
    @SerializedName("shift_state") val shiftState: String?,
    val speed: String?,
    val timestamp: Long,
  )

  data class GuiSettings(
    @SerializedName("gui_24_hour_time") val gui24HourTime: Boolean,
    @SerializedName("gui_charge_rate_units") val guiChargeRateUnits: String,
    @SerializedName("gui_distance_units") val guiDistanceUnits: String,
    @SerializedName("gui_range_display") val guiRangeDisplay: String,
    @SerializedName("gui_temperature_units") val guiTemperatureUnits: String,
    @SerializedName("gui_tirepressure_units") val guiTirepressureUnits: String,
    @SerializedName("show_range_units") val showRangeUnits: Boolean,
    val timestamp: Long,
  )

  data class VehicleConfig(
    @SerializedName("aux_park_lamps") val auxParkLamps: String,
    @SerializedName("badge_version") val badgeVersion: Int,
    @SerializedName("can_accept_navigation_requests") val canAcceptNavigationRequests: Boolean,
    @SerializedName("can_actuate_trunks") val canActuateTrunks: Boolean,
    @SerializedName("car_special_type") val carSpecialType: String,
    @SerializedName("car_type") val carType: String,
    @SerializedName("charge_port_type") val chargePortType: String,
    @SerializedName("cop_user_set_temp_supported") val copUserSetTempSupported: Boolean,
    @SerializedName("dashcam_clip_save_supported") val dashcamClipSaveSupported: Boolean,
    @SerializedName("default_charge_to_max") val defaultChargeToMax: Boolean,
    @SerializedName("driver_assist") val driverAssist: String,
    @SerializedName("ece_restrictions") val eceRestrictions: Boolean,
    @SerializedName("efficiency_package") val efficiencyPackage: String,
    @SerializedName("eu_vehicle") val euVehicle: Boolean,
    @SerializedName("exterior_color") val exteriorColor: String,
    @SerializedName("exterior_trim") val exteriorTrim: String,
    @SerializedName("exterior_trim_override") val exteriorTrimOverride: String,
    @SerializedName("front_drive_unit") val frontDriveUnit: String,
    @SerializedName("has_air_suspension") val hasAirSuspension: Boolean,
    @SerializedName("has_ludicrous_mode") val hasLudicrousMode: Boolean,
    @SerializedName("has_seat_cooling") val hasSeatCooling: Boolean,
    @SerializedName("headlamp_type") val headlampType: String,
    @SerializedName("interior_trim_type") val interiorTrimType: String,
    @SerializedName("key_version") val keyVersion: Int,
    @SerializedName("motorized_charge_port") val motorizedChargePort: Boolean,
    @SerializedName("paint_color_override") val paintColorOverride: String,
    @SerializedName("performance_package") val performancePackage: String,
    val plg: Boolean,
    val pws: Boolean,
    @SerializedName("rear_drive_unit") val rearDriveUnit: String,
    @SerializedName("rear_seat_heaters") val rearSeatHeaters: Int,
    @SerializedName("rear_seat_type") val rearSeatType: Int,
    val rhd: Boolean,
    @SerializedName("roof_color") val roofColor: String,
    @SerializedName("seat_type") val seatType: String?,
    @SerializedName("spoiler_type") val spoilerType: String,
    @SerializedName("sun_roof_installed") val sunRoofInstalled: Int?,
    @SerializedName("supports_qr_pairing") val supportsQrPairing: Boolean,
    @SerializedName("third_row_seats") val thirdRowSeats: String,
    val timestamp: Long,
    @SerializedName("trim_badging") val trimBadging: String,
    @SerializedName("use_range_badging") val useRangeBadging: Boolean,
    @SerializedName("utc_offset") val utcOffset: Int,
    @SerializedName("webcam_selfie_supported") val webcamSelfieSupported: Boolean,
    @SerializedName("webcam_supported") val webcamSupported: Boolean,
    @SerializedName("wheel_type") val wheelType: String,
  )

  data class VehicleState(
    @SerializedName("allow_authorized_mobile_devices_only")
    val allowAuthorizedMobileDevicesOnly: Boolean,
    @SerializedName("api_version") val apiVersion: Int,
    @SerializedName("autopark_state_v2") val autoparkStateV2: String,
    @SerializedName("autopark_state_v3") val autoparkStateV3: String,
    @SerializedName("autopark_style") val autoparkStyle: String,
    @SerializedName("calendar_supported") val calendarSupported: Boolean,
    @SerializedName("car_version") val carVersion: String,
    @SerializedName("center_display_state") val centerDisplayState: Int,
    @SerializedName("dashcam_clip_save_available") val dashcamClipSaveAvailable: Boolean,
    @SerializedName("dashcam_state") val dashcamState: String,
    val df: Int,
    val dr: Int,
    @SerializedName("fd_window") val fdWindow: Int,
    @SerializedName("feature_bitmask") val featureBitmask: String,
    @SerializedName("fp_window") val fpWindow: Int,
    val ft: Int,
    @SerializedName("homelink_device_count") val homelinkDeviceCount: Int,
    @SerializedName("homelink_nearby") val homelinkNearby: Boolean,
    @SerializedName("is_user_present") val isUserPresent: Boolean,
    @SerializedName("last_autopark_error") val lastAutoparkError: String,
    val locked: Boolean,
    @SerializedName("media_info") val mediaInfo: MediaInfo,
    @SerializedName("media_state") val mediaState: MediaState,
    @SerializedName("notifications_supported") val notificationsSupported: Boolean,
    val odometer: Double,
    @SerializedName("parsed_calendar_supported") val parsedCalendarSupported: Boolean,
    val pf: Int,
    val pr: Int,
    @SerializedName("rd_window") val rdWindow: Int,
    @SerializedName("remote_start") val remoteStart: Boolean,
    @SerializedName("remote_start_enabled") val remoteStartEnabled: Boolean,
    @SerializedName("remote_start_supported") val remoteStartSupported: Boolean,
    @SerializedName("rp_window") val rpWindow: Int,
    val rt: Int,
    @SerializedName("santa_mode") val santaMode: Int,
    @SerializedName("sentry_mode") val sentryMode: Boolean,
    @SerializedName("sentry_mode_available") val sentryModeAvailable: Boolean,
    @SerializedName("service_mode") val serviceMode: Boolean,
    @SerializedName("service_mode_plus") val serviceModePlus: Boolean,
    @SerializedName("smart_summon_available") val smartSummonAvailable: Boolean,
    @SerializedName("software_update") val softwareUpdate: SoftwareUpdate,
    @SerializedName("speed_limit_mode") val speedLimitMode: SpeedLimitMode,
    @SerializedName("summon_standby_mode_enabled") val summonStandbyModeEnabled: Boolean,
    @SerializedName("sun_roof_percent_open") val sunroofOpenPercent: Int?,
    @SerializedName("sun_roof_state") val sunRoofState: String?,
    val timestamp: Long,
    @SerializedName("tpms_hard_warning_fl") val tpmsHardWarningFl: Boolean,
    @SerializedName("tpms_hard_warning_fr") val tpmsHardWarningFr: Boolean,
    @SerializedName("tpms_hard_warning_rl") val tpmsHardWarningRl: Boolean,
    @SerializedName("tpms_hard_warning_rr") val tpmsHardWarningRr: Boolean,
    @SerializedName("tpms_last_seen_pressure_time_fl") val tpmsLastSeenPressureTimeFl: Long,
    @SerializedName("tpms_last_seen_pressure_time_fr") val tpmsLastSeenPressureTimeFr: Long,
    @SerializedName("tpms_last_seen_pressure_time_rl") val tpmsLastSeenPressureTimeRl: Long,
    @SerializedName("tpms_last_seen_pressure_time_rr") val tpmsLastSeenPressureTimeRr: Long,
    @SerializedName("tpms_pressure_fl") val tpmsPressureFl: Double,
    @SerializedName("tpms_pressure_fr") val tpmsPressureFr: Double,
    @SerializedName("tpms_pressure_rl") val tpmsPressureRl: Double,
    @SerializedName("tpms_pressure_rr") val tpmsPressureRr: Double,
    @SerializedName("tpms_rcp_front_value") val tpmsRcpFrontValue: Double,
    @SerializedName("tpms_rcp_rear_value") val tpmsRcpRearValue: Double,
    @SerializedName("tpms_soft_warning_fl") val tpmsSoftWarningFl: Boolean,
    @SerializedName("tpms_soft_warning_fr") val tpmsSoftWarningFr: Boolean,
    @SerializedName("tpms_soft_warning_rl") val tpmsSoftWarningRl: Boolean,
    @SerializedName("tpms_soft_warning_rr") val tpmsSoftWarningRr: Boolean,
    @SerializedName("valet_mode") val valetMode: Boolean,
    @SerializedName("valet_pin_needed") val valetPinNeeded: Boolean,
    @SerializedName("vehicle_name") val vehicleName: String,
    @SerializedName("vehicle_self_test_progress") val vehicleSelfTestProgress: Int,
    @SerializedName("vehicle_self_test_requested") val vehicleSelfTestRequested: Boolean,
    @SerializedName("webcam_available") val webcamAvailable: Boolean,
  ) {
    data class MediaInfo(
      @SerializedName("a2dp_source_name") val a2dpSourceName: String,
      @SerializedName("audio_volume") val audioVolume: Double,
      @SerializedName("audio_volume_increment") val audioVolumeIncrement: Double,
      @SerializedName("audio_volume_max") val audioVolumeMax: Double,
      @SerializedName("media_playback_status") val mediaPlaybackStatus: String,
      @SerializedName("now_playing_album") val nowPlayingAlbum: String,
      @SerializedName("now_playing_artist") val nowPlayingArtist: String,
      @SerializedName("now_playing_duration") val nowPlayingDuration: Int,
      @SerializedName("now_playing_elapsed") val nowPlayingElapsed: Int,
      @SerializedName("now_playing_source") val nowPlayingSource: String,
      @SerializedName("now_playing_station") val nowPlayingStation: String,
      @SerializedName("now_playing_title") val nowPlayingTitle: String,
    )

    data class MediaState(
      @SerializedName("remote_control_enabled") val remoteControlEnabled: Boolean
    )

    data class SoftwareUpdate(
      @SerializedName("download_perc") val downloadPerc: Int,
      @SerializedName("expected_duration_sec") val expectedDurationSec: Int,
      @SerializedName("install_perc") val installPerc: Int,
      val status: String,
      val version: String,
    )

    data class SpeedLimitMode(
      val active: Boolean,
      @SerializedName("current_limit_mph") val currentLimitMph: Double,
      @SerializedName("max_limit_mph") val maxLimitMph: Double,
      @SerializedName("min_limit_mph") val minLimitMph: Double,
      @SerializedName("pin_code_set") val pinCodeSet: Boolean,
    )
  }
}