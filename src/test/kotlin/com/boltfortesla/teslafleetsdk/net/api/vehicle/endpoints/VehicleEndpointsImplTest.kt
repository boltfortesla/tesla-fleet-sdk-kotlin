package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints

import com.boltfortesla.teslafleetsdk.TeslaFleetApi
import com.boltfortesla.teslafleetsdk.fixtures.Constants
import com.boltfortesla.teslafleetsdk.fixtures.Responses
import com.boltfortesla.teslafleetsdk.fixtures.Responses.CREATE_SHARE_INVITES_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.DRIVERS_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.ELIGIBLE_SUBSCRIPTIONS_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.ELIGIBLE_UPGRADES_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.FLEET_STATUS_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.FLEET_TELEMETRY_CONFIG_MODIFY_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.FLEET_TELEMETRY_CONFIG_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.LIST_VEHICLES_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.MOBILE_ENABLED_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.NEARBY_CHARGING_SITES_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.OPTIONS_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.RECENT_ALERTS_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.REDEEM_SHARE_INVITES_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.RELEASE_NOTES_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.SERVICE_DATA_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.SHARE_INVITES_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.SUBSCRIPTIONS_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.VEHICLE_DATA_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.VEHICLE_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.WAKE_UP_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.WARRANTY_DETAILS_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.signedCommandJson
import com.boltfortesla.teslafleetsdk.net.JitterFactorCalculatorImpl
import com.boltfortesla.teslafleetsdk.net.NetworkExecutorImpl
import com.boltfortesla.teslafleetsdk.net.api.FleetApiResponse
import com.boltfortesla.teslafleetsdk.net.api.response.Product
import com.boltfortesla.teslafleetsdk.net.api.response.Product.Vehicle.GranularAccess
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.TelemetryConfig.FieldConfig
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.VehicleEndpoints.Endpoint
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.Driver
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.EligibleSubscriptionsResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.EligibleSubscriptionsResponse.Eligible
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.EligibleSubscriptionsResponse.Eligible.Addon
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.EligibleSubscriptionsResponse.Eligible.BillingOption
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.EligibleUpgradesResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.FleetStatusResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.FleetTelemetryModifyConfigResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.FleetTelemetryResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.Invitation
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.NearbyChargingSitesResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.NearbyChargingSitesResponse.ChargingStation.Location
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.OptionsResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.RecentAlertsResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.RedeemInviteResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.ReleaseNote
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.ReleaseNotesResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.ServiceDataResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.SignedCommandResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.VehicleDataResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response.WarrantyDetailsResponse
import com.google.common.truth.Truth.assertThat
import java.util.Base64
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Test

class VehicleEndpointsImplTest {
  private val server = MockWebServer()
  private val vehicleEndpointsApi = createVehicleEndpointsApi(server.url("/").toString())
  private val retryConfig = TeslaFleetApi.RetryConfig()
  private val jitterFactorCalculator = JitterFactorCalculatorImpl()
  private val networkExecutor = NetworkExecutorImpl(retryConfig, jitterFactorCalculator)

  private val vehicleEndpoints =
    VehicleEndpointsImpl(Constants.VIN, vehicleEndpointsApi, networkExecutor)

  @After
  fun tearDown() {
    server.shutdown()
  }

  @Test
  fun getDrivers_returnsDrivers() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(DRIVERS_RESPONSE))

    val response = vehicleEndpoints.getDrivers()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/vehicles/${Constants.VIN}/drivers")
    assertThat(response.getOrNull())
      .isEqualTo(
        FleetApiResponse(
          listOf(
            Driver(
              8888888,
              800001,
              "800001",
              "Testy",
              "McTesterson",
              Driver.GranularAccess(false),
              emptyList(),
              "",
            )
          )
        )
      )
  }

  @Test
  fun removeDrivers() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody("{\"response\": \"ok\"}"))

    val response = vehicleEndpoints.removeDrivers(1)

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/vehicles/${Constants.VIN}/drivers?share_user_id=1")
    assertThat(response.getOrNull()).isEqualTo(FleetApiResponse("ok"))
  }

  @Test
  fun getEligibleSubscriptions() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(ELIGIBLE_SUBSCRIPTIONS_RESPONSE))

    val response = vehicleEndpoints.getEligibleSubscriptions()

    val request = server.takeRequest()
    assertThat(request.path)
      .isEqualTo("/api/1/dx/vehicles/subscriptions/eligibility?vin=${Constants.VIN}")
    assertThat(response.getOrNull())
      .isEqualTo(
        EligibleSubscriptionsResponse(
          "string",
          listOf(
            Eligible(
              listOf(Addon("string", "string", "string", 0.0, 0.0, 0.0)),
              listOf(BillingOption("string", "string", "string", 0.0, 0.0, 0.0)),
              "string",
              "string",
              "string",
            )
          ),
          "string",
        )
      )
  }

  @Test
  fun getEligibleUpgrades() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(ELIGIBLE_UPGRADES_RESPONSE))

    val response = vehicleEndpoints.getEligibleUpgrades()

    val request = server.takeRequest()
    assertThat(request.path)
      .isEqualTo("/api/1/dx/vehicles/upgrades/eligibility?vin=${Constants.VIN}")
    assertThat(response.getOrNull())
      .isEqualTo(
        EligibleUpgradesResponse(
          "TEST00000000VIN01",
          "US",
          "VEHICLE",
          listOf(
            EligibleUpgradesResponse.EligibleOption(
              "\$FM3U",
              "PERF_FIRMWARE",
              "\$FM3B",
              listOf(EligibleUpgradesResponse.EligibleOption.PricingOption(2000, 2000, "USD", true)),
            )
          ),
        )
      )
  }

  @Test
  fun getFleetStatus() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(FLEET_STATUS_RESPONSE))

    val response = vehicleEndpoints.getFleetStatus(listOf(Constants.VIN, Constants.VIN))

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/vehicles/fleet_status")
    assertThat(response.getOrNull())
      .isEqualTo(FleetApiResponse(FleetStatusResponse(emptyList(), listOf("5YJ3000000NEXUS01"))))
  }

  @Test
  fun createFleetTelemetryConfig() = runTest {
    server.enqueue(
      MockResponse().setResponseCode(200).setBody(FLEET_TELEMETRY_CONFIG_MODIFY_RESPONSE)
    )

    val response =
      vehicleEndpoints.createFleetTelemetryConfig(
        listOf(Constants.VIN, Constants.VIN),
        "hostname",
        "authority",
        mapOf("field" to 123, "field2" to 456),
        listOf("alert1", "alert2"),
        12345,
      )

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/vehicles/fleet_telemetry_config")
    assertThat(response.getOrNull())
      .isEqualTo(FleetApiResponse(FleetTelemetryModifyConfigResponse(1)))
  }

  @Test
  fun deleteFleetTelemetryConfig() = runTest {
    server.enqueue(
      MockResponse().setResponseCode(200).setBody(FLEET_TELEMETRY_CONFIG_MODIFY_RESPONSE)
    )

    val response = vehicleEndpoints.deleteFleetTelemetryConfig()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/vehicles/${Constants.VIN}/fleet_telemetry_config")
    assertThat(response.getOrNull())
      .isEqualTo(FleetApiResponse(FleetTelemetryModifyConfigResponse(1)))
  }

  @Test
  fun getFleetTelemetryConfig() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(FLEET_TELEMETRY_CONFIG_RESPONSE))

    val response = vehicleEndpoints.getFleetTelemetryConfig()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/vehicles/${Constants.VIN}/fleet_telemetry_config")
    assertThat(response.getOrNull())
      .isEqualTo(
        FleetApiResponse(
          FleetTelemetryResponse(
            true,
            TelemetryConfig(
              "test-telemetry.com",
              "-----BEGIN CERTIFICATE-----\ncert\n-----END CERTIFICATE-----\n",
              1704067200,
              mapOf("DriveRail" to FieldConfig(1800), "BmsFullchargecomplete" to FieldConfig(1800)),
              listOf("service"),
            ),
          )
        )
      )
  }

  @Test
  fun listVehicles() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(LIST_VEHICLES_RESPONSE))

    val response = vehicleEndpoints.listVehicles(1, 10)

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/vehicles?page=1&per_page=10")
    assertThat(response.getOrNull())
      .isEqualTo(
        FleetApiResponse(
          listOf(
            Product.Vehicle(
              100021,
              null,
              99999,
              "TEST00000000VIN01",
              null,
              "OWNER",
              "Owned",
              "TEST0,COUS",
              null,
              GranularAccess(false),
              listOf("4f993c5b9e2b937b", "7a3153b1bbb48a96"),
              null,
              false,
              "100021",
              true,
              null,
              null,
              null,
              null,
            )
          )
        )
      )
  }

  @Test
  fun isMobileEnabled() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(MOBILE_ENABLED_RESPONSE))

    val response = vehicleEndpoints.isMobileEnabled()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/vehicles/${Constants.VIN}/mobile_enabled")
    assertThat(response.getOrNull()).isEqualTo(FleetApiResponse(true))
  }

  @Test
  fun getNearbyChargingSites() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(NEARBY_CHARGING_SITES_RESPONSE))

    val response = vehicleEndpoints.getNearbyChargingSites(1, 500, true)

    val request = server.takeRequest()
    assertThat(request.path)
      .isEqualTo(
        "/api/1/vehicles/${Constants.VIN}/nearby_charging_sites?count=1&radius=500&detail=true"
      )
    assertThat(response.getOrNull())
      .isEqualTo(
        FleetApiResponse(
          NearbyChargingSitesResponse(
            1693588513,
            listOf(
              NearbyChargingSitesResponse.ChargingStation(
                Location(37.409314, -122.123068),
                "Hilton Garden Inn Palo Alto",
                "destination",
                1.35024,
                "restrooms,wifi,lodging",
                id = 1,
              ),
              NearbyChargingSitesResponse.ChargingStation(
                Location(37.407771, -122.120076),
                "Dinah's Garden Hotel & Poolside Restaurant",
                "destination",
                1.534213,
                "restrooms,restaurant,wifi,cafe,lodging",
                id = 2,
              ),
            ),
            listOf(
              NearbyChargingSitesResponse.ChargingStation(
                Location(37.399071, -122.111216),
                "Los Altos, CA",
                "supercharger",
                2.202902,
                "restrooms,restaurant,wifi,cafe,shopping",
                12,
                16,
                false,
                "",
                id = 3,
              ),
              NearbyChargingSitesResponse.ChargingStation(
                Location(37.441734, -122.170202),
                "Palo Alto, CA - Stanford Shopping Center",
                "supercharger",
                2.339135,
                "restrooms,restaurant,wifi,cafe,shopping",
                11,
                20,
                false,
                "",
                id = 4,
              ),
            ),
            1693588576552,
          )
        )
      )
  }

  @Test
  fun getOptions() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(OPTIONS_RESPONSE))

    val response = vehicleEndpoints.getOptions()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/dx/vehicles/options?vin=${Constants.VIN}")
    assertThat(response.getOrNull())
      .isEqualTo(
        OptionsResponse(
          listOf(
            OptionsResponse.OptionCode(
              "\$MT315",
              displayName = "Long Range All-Wheel Drive",
              isActive = true,
            ),
            OptionsResponse.OptionCode("\$PPSW", "PPSW", "Pearl White Multi-Coat", true),
            OptionsResponse.OptionCode("\$W40B", displayName = "18\" Aero Wheels", isActive = true),
            OptionsResponse.OptionCode(
              "\$IPB0",
              displayName = "All Black Premium Interior",
              isActive = true,
            ),
            OptionsResponse.OptionCode("\$APBS", displayName = "Basic Autopilot", isActive = true),
            OptionsResponse.OptionCode(
              "\$APF2",
              displayName = "Full Self-Driving Capability",
              isActive = true,
            ),
            OptionsResponse.OptionCode(
              "\$SC04",
              displayName = "Supercharger Network Access + Pay-as-you-go",
              isActive = true,
            ),
          )
        )
      )
  }

  @Test
  fun getRecentAlerts() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(RECENT_ALERTS_RESPONSE))

    val response = vehicleEndpoints.getRecentAlerts()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/vehicles/${Constants.VIN}/recent_alerts")
    assertThat(response.getOrNull())
      .isEqualTo(
        FleetApiResponse(
          RecentAlertsResponse(
            listOf(
              RecentAlertsResponse.Alert(
                "Name_Of_The_Alert",
                "2021-03-19T22:01:15.101+00:00",
                listOf("service-fix", "customer"),
                "additional description text",
              )
            )
          )
        )
      )
  }

  @Test
  fun getReleaseNotes() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(RELEASE_NOTES_RESPONSE))

    val response = vehicleEndpoints.getReleaseNotes(true, 1)

    val request = server.takeRequest()
    assertThat(request.path)
      .isEqualTo("/api/1/vehicles/${Constants.VIN}/release_notes?staged=true&language=1")
    assertThat(response.getOrNull())
      .isEqualTo(
        FleetApiResponse(
          ReleaseNotesResponse(
            listOf(
              ReleaseNote(
                "Minor Fixes",
                "Some more info",
                "This release contains minor fixes and improvements",
                "2022.42.0",
                "release_notes_icon",
                true,
                "https://vehicle-files.teslamotors.com/release_notes/d0fa3e08a458696e6464a46c938ffc0a",
                "https://vehicle-files.teslamotors.com/release_notes/9a122cff8916fffcb61cfd65a15c276f",
              )
            ),
            deployedVersion = "123",
            stagedVersion = null,
            releaseNotesVersion = "456",
          )
        )
      )
  }

  @Test
  fun getServiceData() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(SERVICE_DATA_RESPONSE))

    val response = vehicleEndpoints.getServiceData()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/vehicles/${Constants.VIN}/service_data")
    assertThat(response.getOrNull())
      .isEqualTo(
        FleetApiResponse(
          ServiceDataResponse("in_service", "2023-05-02T17:10:53-10:00", "SV12345678", 8)
        )
      )
  }

  @Test
  fun shareInvites() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(SHARE_INVITES_RESPONSE))

    val response = vehicleEndpoints.shareInvites()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/vehicles/${Constants.VIN}/invitations")
    assertThat(response.getOrNull())
      .isEqualTo(
        FleetApiResponse(
          listOf(
            Invitation(
              429509621657,
              429511308124,
              null,
              "TEST00000000VIN01",
              "pending",
              "aqwl4JHU2q4aTeNROz8W9SpngoFvj-ReuDFIJs6-YOhA",
              "2023-06-29T00:42:00.000Z",
              null,
              null,
              null,
              "vehicle",
              "customer",
              null,
              listOf(null),
              "429509621657",
              "429511308124",
              "",
              null,
              "TEST00000000VIN01",
              "https://www.tesla.com/_rs/1/aqwl4JHU2q4aTeNROz8W9SpngoFvj-ReuDFIJs6-YOhA",
            )
          )
        )
      )
  }

  @Test
  fun createShareInvite() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(CREATE_SHARE_INVITES_RESPONSE))

    val response = vehicleEndpoints.createShareInvite()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/vehicles/${Constants.VIN}/invitations")
    assertThat(response.getOrNull())
      .isEqualTo(
        FleetApiResponse(
          Invitation(
            429509621657,
            429511308124,
            null,
            "TEST00000000VIN01",
            "pending",
            "aqwl4JHU2q4aTeNROz8W9SpngoFvj-ReuDFIJs6-YOhA",
            "2023-06-29T00:42:00.000Z",
            null,
            null,
            null,
            "vehicle",
            "customer",
            null,
            listOf(null),
            "429509621657",
            "429511308124",
            "",
            null,
            "TEST00000000VIN01",
            "https://www.tesla.com/_rs/1/aqwl4JHU2q4aTeNROz8W9SpngoFvj-ReuDFIJs6-YOhA",
          )
        )
      )
  }

  @Test
  fun redeemShareInvite() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(REDEEM_SHARE_INVITES_RESPONSE))

    val response = vehicleEndpoints.redeemShareInvite("code")

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/invitations/redeem")
    assertThat(response.getOrNull())
      .isEqualTo(FleetApiResponse(RedeemInviteResponse("88850", "5YJY000000NEXUS01")))
  }

  @Test
  fun revokeShareInvites() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody("{\"response\": true}"))

    val response = vehicleEndpoints.revokeShareInvites("invitationId")

    val request = server.takeRequest()
    assertThat(request.path)
      .isEqualTo("/api/1/vehicles/${Constants.VIN}/invitations/invitationId/revoke")
    assertThat(response.getOrNull()).isEqualTo(FleetApiResponse(true))
  }

  @Test
  fun signedCommand() = runTest {
    val expectedResponse =
      SignedCommandResponse(
        Base64.getEncoder().encodeToString(Responses.INFOTAINMENT_COMMAND_RESPONSE.toByteArray())
      )
    server.enqueue(
      MockResponse()
        .setResponseCode(200)
        .setBody(signedCommandJson(Responses.INFOTAINMENT_COMMAND_RESPONSE))
    )

    val response = vehicleEndpoints.signedCommand("v41ZyxWq7VrBrxHxbutQXQ==")

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/vehicles/${Constants.VIN}/signed_command")
    assertThat(response.getOrNull()).isEqualTo(expectedResponse)
  }

  @Test
  fun getSubscriptions() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(SUBSCRIPTIONS_RESPONSE))

    val response = vehicleEndpoints.getSubscriptions("token", "type")

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/subscriptions?device_token=token&device_type=type")
    assertThat(response.getOrNull()).isEqualTo(FleetApiResponse(listOf(100021)))
  }

  @Test
  fun setSubscriptions() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(SUBSCRIPTIONS_RESPONSE))

    val response = vehicleEndpoints.setSubscriptions("token", "type")

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/subscriptions?device_token=token&device_type=type")
    assertThat(response.getOrNull()).isEqualTo(FleetApiResponse(listOf(100021)))
  }

  @Test
  fun getVehicle() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(VEHICLE_RESPONSE))

    val response = vehicleEndpoints.getVehicle()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/vehicles/${Constants.VIN}")
    assertThat(response.getOrNull())
      .isEqualTo(
        FleetApiResponse(
          Product.Vehicle(
            100021,
            null,
            99999,
            "TEST00000000VIN01",
            null,
            "OWNER",
            "Owned",
            "TEST0,COUS",
            null,
            GranularAccess(false),
            listOf("4f993c5b9e2b937b", "7a3153b1bbb48a96"),
            null,
            false,
            "100021",
            true,
            null,
            null,
            null,
            null,
          )
        )
      )
  }

  @Test
  fun getVehicleData() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(VEHICLE_DATA_RESPONSE))

    val response =
      vehicleEndpoints.getVehicleData(listOf(Endpoint.VEHICLE_DATA_COMBO, Endpoint.VEHICLE_CONFIG))

    val request = server.takeRequest()
    assertThat(request.path)
      .isEqualTo(
        "/api/1/vehicles/${Constants.VIN}/vehicle_data?endpoints=vehicle_data_combo%3Bvehicle_config"
      )
    assertThat(response.getOrNull())
      .isEqualTo(
        FleetApiResponse(
          VehicleDataResponse(
            100021,
            800001,
            99999,
            "TEST00000000VIN01",
            null,
            "OWNER",
            VehicleDataResponse.GranularAccess(false),
            listOf("4f993c5b9e2b937b", "7a3153b1bbb48a96"),
            null,
            false,
            "100021",
            true,
            54,
            null,
            null,
            false,
            VehicleDataResponse.ChargeState(
              false,
              42,
              133.99,
              48,
              48,
              48,
              true,
              48.45,
              90,
              100,
              50,
              90,
              202.0,
              202.0,
              false,
              "<invalid>",
              false,
              "Engaged",
              0.0,
              0,
              null,
              48,
              0,
              2,
              "Disconnected",
              "<invalid>",
              143.88,
              "<invalid>",
              false,
              "<invalid>",
              133.99,
              false,
              null,
              false,
              0,
              0,
              null,
              false,
              "all_week",
              360,
              false,
              "all_week",
              "Off",
              false,
              null,
              null,
              1634914800,
              480,
              false,
              0.0,
              1692141038420,
              false,
              42,
              null,
            ),
            VehicleDataResponse.ClimateState(
              true,
              false,
              false,
              false,
              false,
              null,
              false,
              "On",
              true,
              "off",
              "High",
              0,
              21f,
              0,
              "On",
              38.4,
              true,
              false,
              false,
              false,
              false,
              -293,
              28,
              15,
              36.5,
              21f,
              false,
              -276,
              0,
              0,
              0,
              0,
              0,
              false,
              0,
              false,
              true,
              1692141038419,
              false,
              false,
            ),
            VehicleDataResponse.DriveState(
              "Home",
              73,
              37.7765494,
              -122.4195418,
              6.485299,
              23.466667,
              0,
              1692137422,
              289,
              37.7765494,
              -122.4195418,
              37.7765494,
              1,
              -122.4195418,
              "wgs",
              1,
              null,
              null,
              1692141038420,
            ),
            VehicleDataResponse.GuiSettings(
              false,
              "mi/hr",
              "mi/hr",
              "Rated",
              "F",
              "Psi",
              false,
              1692141038420,
            ),
            VehicleDataResponse.VehicleConfig(
              "NaPremium",
              0,
              true,
              true,
              "base",
              "modely",
              "US",
              true,
              true,
              false,
              "TeslaAP3",
              false,
              "MY2021",
              false,
              "MidnightSilver",
              "Black",
              "",
              "frontUnit",
              false,
              false,
              false,
              "Premium",
              "Black2",
              2,
              true,
              "19,20,22,0.8,0.04",
              "Base",
              true,
              true,
              "PM216MOSFET",
              1,
              0,
              false,
              "RoofColorGlass",
              null,
              "None",
              null,
              false,
              "None",
              1692141038420,
              "74d",
              true,
              -25200,
              true,
              true,
              "Apollo19",
            ),
            VehicleDataResponse.VehicleState(
              true,
              54,
              "ready",
              "ready",
              "dead_man",
              true,
              "2023.7.20 7910d26d5c64",
              0,
              false,
              "Unavailable",
              0,
              0,
              0,
              "15dffbff,0",
              0,
              0,
              3,
              false,
              false,
              "no_error",
              true,
              VehicleDataResponse.VehicleState.MediaInfo(
                "Pixel 6",
                2.6667,
                0.333333,
                10.333333,
                "Playing",
                "KQED",
                "PBS Newshour on KQED FM",
                0,
                0,
                "13",
                "88.5 FM KQED",
                "PBS Newshour",
              ),
              VehicleDataResponse.VehicleState.MediaState(true),
              true,
              15720.074889,
              true,
              0,
              0,
              0,
              false,
              true,
              true,
              0,
              0,
              0,
              false,
              true,
              false,
              false,
              true,
              VehicleDataResponse.VehicleState.SoftwareUpdate(0, 2700, 1, "", " "),
              VehicleDataResponse.VehicleState.SpeedLimitMode(false, 85.0, 120.0, 50.0, false),
              false,
              5,
              "vent",
              1692141038419,
              false,
              false,
              false,
              false,
              1692136878,
              1692136878,
              1692136878,
              1692136878,
              3.1,
              3.1,
              3.15,
              3.0,
              2.9,
              2.9,
              false,
              false,
              false,
              false,
              false,
              true,
              "grADOFIN",
              0,
              false,
              true,
            ),
          )
        )
      )
  }

  @Test
  fun getVehicleSubscriptions() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(SUBSCRIPTIONS_RESPONSE))

    val response = vehicleEndpoints.setVehicleSubscriptions("token", "type")

    val request = server.takeRequest()
    assertThat(request.path)
      .isEqualTo("/api/1/vehicle_subscriptions?device_token=token&device_type=type")
    assertThat(response.getOrNull()).isEqualTo(FleetApiResponse(listOf(100021)))
  }

  @Test
  fun setVehicleSubscriptions() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(SUBSCRIPTIONS_RESPONSE))

    val response = vehicleEndpoints.setVehicleSubscriptions("token", "type")

    val request = server.takeRequest()
    assertThat(request.path)
      .isEqualTo("/api/1/vehicle_subscriptions?device_token=token&device_type=type")
    assertThat(response.getOrNull()).isEqualTo(FleetApiResponse(listOf(100021)))
  }

  @Test
  fun wakeUp() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(WAKE_UP_RESPONSE))

    val response = vehicleEndpoints.wakeUp()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/vehicles/${Constants.VIN}/wake_up")
    assertThat(response.getOrNull())
      .isEqualTo(
        FleetApiResponse(
          Product.Vehicle(
            100021,
            800001,
            99999,
            "TEST00000000VIN01",
            null,
            "OWNER",
            null,
            null,
            null,
            GranularAccess(false),
            listOf("4f993c5b9e2b937b", "7a3153b1bbb48a96"),
            null,
            false,
            "100021",
            true,
            null,
            null,
            null,
            null,
          )
        )
      )
  }

  @Test
  fun getWarrantyDetails() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(WARRANTY_DETAILS_RESPONSE))

    val response = vehicleEndpoints.getWarrantyDetails()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/dx/warranty/details?vin=${Constants.VIN}")
    assertThat(response.getOrNull())
      .isEqualTo(
        WarrantyDetailsResponse(
          listOf(
            WarrantyDetailsResponse.Warranty(
              "NEW_MFG_WARRANTY",
              "Basic Vehicle Limited Warranty",
              "2025-10-21T00:00:00Z",
              50000,
              "MI",
              null,
              4,
            ),
            WarrantyDetailsResponse.Warranty(
              "BATTERY_WARRANTY",
              "Battery Limited Warranty",
              "2029-10-21T00:00:00Z",
              120000,
              "MI",
              null,
              8,
            ),
            WarrantyDetailsResponse.Warranty(
              "DRIVEUNIT_WARRANTY",
              "Drive Unit Limited Warranty",
              "2029-10-21T00:00:00Z",
              120000,
              "MI",
              null,
              8,
            ),
          ),
          emptyList(),
          emptyList(),
        )
      )
  }
}
