package com.boltfortesla.teslafleetsdk.net.api.energy

import com.boltfortesla.teslafleetsdk.TeslaFleetApi
import com.boltfortesla.teslafleetsdk.fixtures.Responses.BACKUP_HISTORY_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.CHARGE_HISTORY_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.ENERGY_COMMAND_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.ENERGY_HISTORY_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.LIVE_STATUS_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.SITE_INFO_RESPONSE
import com.boltfortesla.teslafleetsdk.net.JitterFactorCalculatorImpl
import com.boltfortesla.teslafleetsdk.net.NetworkExecutorImpl
import com.boltfortesla.teslafleetsdk.net.api.FleetApiResponse
import com.boltfortesla.teslafleetsdk.net.api.energy.EnergyEndpoints.ExportRule
import com.boltfortesla.teslafleetsdk.net.api.energy.EnergyEndpoints.OperationMode.AUTONOMOUS
import com.boltfortesla.teslafleetsdk.net.api.energy.response.BackupHistoryResponse
import com.boltfortesla.teslafleetsdk.net.api.energy.response.BackupHistoryResponse.*
import com.boltfortesla.teslafleetsdk.net.api.energy.response.ChargeHistory
import com.boltfortesla.teslafleetsdk.net.api.energy.response.ChargeHistory.ChargeDuration
import com.boltfortesla.teslafleetsdk.net.api.energy.response.ChargeHistory.ChargeStartTime
import com.boltfortesla.teslafleetsdk.net.api.energy.response.ChargeHistoryResponse
import com.boltfortesla.teslafleetsdk.net.api.energy.response.Components
import com.boltfortesla.teslafleetsdk.net.api.energy.response.EnergyCommandResponse
import com.boltfortesla.teslafleetsdk.net.api.energy.response.EnergyHistoryResponse
import com.boltfortesla.teslafleetsdk.net.api.energy.response.LiveStatusResponse
import com.boltfortesla.teslafleetsdk.net.api.energy.response.SiteInfoResponse
import com.boltfortesla.teslafleetsdk.net.api.energy.response.UserSettings
import com.google.common.truth.Truth.assertThat
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Test

class EnergyEndpointsImplTest {
  private val server = MockWebServer()
  private val energyApi = createEnergyApi(server.url("/").toString())
  private val retryConfig = TeslaFleetApi.RetryConfig()
  private val jitterFactorCalculator = JitterFactorCalculatorImpl()
  private val networkExecutor = NetworkExecutorImpl(retryConfig, jitterFactorCalculator)

  private val energyEndpoints = EnergyEndpointsImpl(SITE_ID, energyApi, networkExecutor)

  @After
  fun tearDown() {
    server.shutdown()
  }

  @Test
  fun setBackup_succeeds() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(ENERGY_COMMAND_RESPONSE))

    val response = energyEndpoints.setBackup(backupReservePercent = 50)

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/energy_sites/$SITE_ID/backup")
    assertThat(request.body.readUtf8()).isEqualTo("{\"backup_reserve_percent\":50}")
    assertThat(response.getOrNull())
      .isEqualTo(FleetApiResponse(EnergyCommandResponse(201, "Updated")))
  }

  @Test
  fun getBackupHistory_returnsBackupHistory() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(BACKUP_HISTORY_RESPONSE))

    val response =
      energyEndpoints.getBackupHistory(
        ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneId.ofOffset("UTC", ZoneOffset.ofHours(-8))),
        ZonedDateTime.of(2023, 1, 2, 8, 0, 0, 0, ZoneId.ofOffset("UTC", ZoneOffset.ofHours(-8))),
        EnergyEndpoints.Period.DAY,
        "America/Los_Angeles"
      )

    val request = server.takeRequest()
    assertThat(request.path)
      .isEqualTo(
        "/api/1/energy_sites/$SITE_ID/calendar_history?kind=backup&start_date=2023-01-01T00%3A00%3A00-08%3A00&end_date=2023-01-02T08%3A00%3A00-08%3A00&period=day&time_zone=America%2FLos_Angeles"
      )
    assertThat(response.getOrNull())
      .isEqualTo(
        FleetApiResponse(
          BackupHistoryResponse(
            listOf(
              Event("2023-01-01T01:00:00-08:00", 3600),
              Event("2023-01-02T08:00:00-08:00", 7200)
            ),
            2
          )
        )
      )
  }

  @Test
  fun getChargeHistory_returnsChargeHistory() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(CHARGE_HISTORY_RESPONSE))

    val response =
      energyEndpoints.getChargeHistory(
        ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneId.ofOffset("UTC", ZoneOffset.ofHours(-8))),
        ZonedDateTime.of(2023, 1, 2, 8, 0, 0, 0, ZoneId.ofOffset("UTC", ZoneOffset.ofHours(-8))),
        "America/Los_Angeles"
      )

    val request = server.takeRequest()
    assertThat(request.path)
      .isEqualTo(
        "/api/1/energy_sites/$SITE_ID/telemetry_history?kind=charge&start_date=2023-01-01T00%3A00%3A00-08%3A00&end_date=2023-01-02T08%3A00%3A00-08%3A00&time_zone=America%2FLos_Angeles"
      )
    assertThat(response.getOrNull())
      .isEqualTo(
        FleetApiResponse(
          ChargeHistoryResponse(
            listOf(ChargeHistory(ChargeStartTime(1672560000), ChargeDuration(12000), 25000))
          )
        )
      )
  }

  @Test
  fun getEnergyHistory_returnsEnergyHistory() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(ENERGY_HISTORY_RESPONSE))

    val response =
      energyEndpoints.getEnergyHistory(
        ZonedDateTime.of(2023, 1, 1, 0, 0, 0, 0, ZoneId.ofOffset("UTC", ZoneOffset.ofHours(-8))),
        ZonedDateTime.of(2023, 1, 2, 8, 0, 0, 0, ZoneId.ofOffset("UTC", ZoneOffset.ofHours(-8))),
        EnergyEndpoints.Period.DAY,
        "America/Los_Angeles"
      )

    val request = server.takeRequest()
    assertThat(request.path)
      .isEqualTo(
        "/api/1/energy_sites/$SITE_ID/calendar_history?kind=energy&start_date=2023-01-01T00%3A00%3A00-08%3A00&end_date=2023-01-02T08%3A00%3A00-08%3A00&period=day&time_zone=America%2FLos_Angeles"
      )
    assertThat(response.getOrNull())
      .isEqualTo(
        FleetApiResponse(
          EnergyHistoryResponse(
            "day",
            listOf(
              EnergyHistoryResponse.TimeSeriesEntry(
                "2023-06-01T01:00:00-07:00",
                70940,
                0,
                521,
                17.53125,
                3.80859375,
                43660,
                0,
                19,
                10030,
                80,
                16800,
                0,
                441,
                10480,
                10011,
                0
              )
            )
          )
        )
      )
  }

  @Test
  fun setGridImportExport_succeeds() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(ENERGY_COMMAND_RESPONSE))

    val response =
      energyEndpoints.setGridImportExport(
        disallowChargeFromGridWithSolarInstalled = true,
        ExportRule.BATTERY_OK
      )

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/energy_sites/$SITE_ID/grid_import_export")
    assertThat(request.body.readUtf8())
      .isEqualTo(
        "{\"disallow_charge_from_grid_with_solar_installed\":true,\"customer_preferred_export_rule\":\"battery_ok\"}"
      )
    assertThat(response.getOrNull())
      .isEqualTo(FleetApiResponse(EnergyCommandResponse(201, "Updated")))
  }

  @Test
  fun getLiveStatus_returnsLiveStatus() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(LIVE_STATUS_RESPONSE))

    val response = energyEndpoints.getLiveStatus()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/energy_sites/$SITE_ID/live_status")
    assertThat(response.getOrNull())
      .isEqualTo(
        FleetApiResponse(
          LiveStatusResponse(
            3102,
            18020.894736842107,
            39343,
            45.80457701965307,
            true,
            -3090,
            2581,
            "Active",
            2569,
            "on_grid",
            false,
            "2023-01-01T00:00:00-08:00"
          )
        )
      )
  }

  @Test
  fun setOffGridVehicleChargingReserve_succeeds() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(ENERGY_COMMAND_RESPONSE))

    val response =
      energyEndpoints.setOffGridVehicleChargingReserve(offGridVehicleChargingReservePercent = 50)

    val request = server.takeRequest()
    assertThat(request.path)
      .isEqualTo("/api/1/energy_sites/$SITE_ID/off_grid_vehicle_charging_reserve")
    assertThat(request.body.readUtf8())
      .isEqualTo("{\"off_grid_vehicle_charging_reserve_percent\":50}")
    assertThat(response.getOrNull())
      .isEqualTo(FleetApiResponse(EnergyCommandResponse(201, "Updated")))
  }

  @Test
  fun setOperation_succeeds() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(ENERGY_COMMAND_RESPONSE))

    val response = energyEndpoints.setOperation(defaultRealMode = AUTONOMOUS)

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/energy_sites/$SITE_ID/operation")
    assertThat(request.body.readUtf8()).isEqualTo("{\"default_real_mode\":\"autonomous\"}")
    assertThat(response.getOrNull())
      .isEqualTo(FleetApiResponse(EnergyCommandResponse(201, "Updated")))
  }

  fun getSiteInfo_returnsSiteInfo() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(SITE_INFO_RESPONSE))

    val response = energyEndpoints.getSiteInfo()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/energy_sites/$SITE_ID}/site_info")
    assertThat(response.getOrNull())
      .isEqualTo(
        FleetApiResponse(
          SiteInfoResponse(
            "0000000-00-A--TEST0000000DIN",
            "My Home",
            20,
            "autonomous",
            "2023-01-01T00:00:00-08:00",
            UserSettings(true),
            Components(
              true,
              "pv_panel",
              true,
              true,
              true,
              true,
              true,
              true,
              true,
              true,
              "ac_powerwall",
              true
            ),
            "23.12.11 452c76cb",
            3,
            15000,
            40500,
            "America/Los_Angeles",
            1000000000,
            -11.726
          )
        )
      )
  }

  @Test
  fun enableStormMode_succeeds() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(ENERGY_COMMAND_RESPONSE))

    val response = energyEndpoints.enableStormMode()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/energy_sites/$SITE_ID/storm_mode")
    assertThat(request.body.readUtf8()).isEqualTo("{\"enabled\":true}")
    assertThat(response.getOrNull())
      .isEqualTo(FleetApiResponse(EnergyCommandResponse(201, "Updated")))
  }

  @Test
  fun disableStormMode_succeeds() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(ENERGY_COMMAND_RESPONSE))

    val response = energyEndpoints.disableStormMode()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/energy_sites/$SITE_ID/storm_mode")
    assertThat(request.body.readUtf8()).isEqualTo("{\"enabled\":false}")
    assertThat(response.getOrNull())
      .isEqualTo(FleetApiResponse(EnergyCommandResponse(201, "Updated")))
  }

  private companion object {
    const val SITE_ID = 12345
  }
}
