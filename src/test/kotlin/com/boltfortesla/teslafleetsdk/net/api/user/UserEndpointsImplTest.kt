package com.boltfortesla.teslafleetsdk.net.api.user

import com.boltfortesla.teslafleetsdk.TeslaFleetApi
import com.boltfortesla.teslafleetsdk.fixtures.Responses.BACKUP_KEY_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.FEATURE_CONFIG_RESOPNSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.ME_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.ORDERS_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.REGION_RESPONSE
import com.boltfortesla.teslafleetsdk.net.JitterFactorCalculatorImpl
import com.boltfortesla.teslafleetsdk.net.NetworkExecutorImpl
import com.boltfortesla.teslafleetsdk.net.api.FleetApiResponse
import com.boltfortesla.teslafleetsdk.net.api.user.response.BackupKeyResponse
import com.boltfortesla.teslafleetsdk.net.api.user.response.FeatureConfigResponse
import com.boltfortesla.teslafleetsdk.net.api.user.response.FeatureConfigResponse.Signaling
import com.boltfortesla.teslafleetsdk.net.api.user.response.MeResponse
import com.boltfortesla.teslafleetsdk.net.api.user.response.Order
import com.boltfortesla.teslafleetsdk.net.api.user.response.RegionResponse
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Test

class UserEndpointsImplTest {
  private val server = MockWebServer()
  private val oauthApi = createUserApi(server.url("/").toString())
  private val retryConfig = TeslaFleetApi.RetryConfig()
  private val jitterFactorCalculator = JitterFactorCalculatorImpl()
  private val networkExecutor = NetworkExecutorImpl(retryConfig, jitterFactorCalculator)

  private val userEndpoints = UserEndpointsImpl(oauthApi, networkExecutor)

  @After
  fun tearDown() {
    server.shutdown()
  }

  @Test
  fun getBackupKey_returnsBackupKey() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(BACKUP_KEY_RESPONSE))

    val response = userEndpoints.getBackupKey()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/users/backup_key")
    assertThat(request.body.readUtf8()).isEmpty()
    assertThat(response.getOrNull()).isEqualTo(FleetApiResponse(BackupKeyResponse(null)))
  }

  @Test
  fun getFeatureConfig_returnsFeatureConfig() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(FEATURE_CONFIG_RESOPNSE))

    val response = userEndpoints.getFeatureConfig()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/users/feature_config")
    assertThat(request.body.readUtf8()).isEmpty()
    assertThat(response.getOrNull())
      .isEqualTo(
        FleetApiResponse(
          FeatureConfigResponse(
            Signaling(true, false, false),
            FeatureConfigResponse.AuthRejection(enabled = true, minVersion = 4)
          )
        )
      )
  }

  @Test
  fun getMe_returnsUserDetails() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(ME_RESPONSE))

    val response = userEndpoints.getMe()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/users/me")
    assertThat(request.body.readUtf8()).isEmpty()
    assertThat(response.getOrNull())
      .isEqualTo(
        FleetApiResponse(
          MeResponse(
            "test-user@tesla.com",
            "Testy McTesterson",
            "https://vehicle-files.prd.usw2.vn.cloud.tesla.com/profile_images/f98c87cd7bebc06069b89b33f9ec634c195520f75b6e63ea89f0b7c61449c689.jpg"
          )
        )
      )
  }

  @Test
  fun getOrders_returnsOrders() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(ORDERS_RESPONSE))

    val response = userEndpoints.getOrders()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/users/orders")
    assertThat(request.body.readUtf8()).isEmpty()
    assertThat(response.getOrThrow())
      .isEqualTo(
        FleetApiResponse(
          listOf(
            Order(
              1234466,
              "RN00000001",
              "5YJ30000000000001",
              "BOOKED",
              "_Z",
              "m3",
              "US",
              "en_US",
              "APBS,DV2W,IBB1,PMNG,PRM30,SC04,MDL3,W41B,MT322,CPF0,RSF1,CW03",
              false
            )
          )
        )
      )
  }

  @Test
  fun getRegion_returnsRegion() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(REGION_RESPONSE))

    val response = userEndpoints.getRegion()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/users/region")
    assertThat(request.body.readUtf8()).isEmpty()
    assertThat(response.getOrNull())
      .isEqualTo(
        FleetApiResponse(RegionResponse("eu", "https://fleet-api.prd.eu.vn.cloud.tesla.com"))
      )
  }
}
