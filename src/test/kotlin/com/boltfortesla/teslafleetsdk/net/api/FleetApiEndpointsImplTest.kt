package com.boltfortesla.teslafleetsdk.net.api

import com.boltfortesla.teslafleetsdk.TeslaFleetApi
import com.boltfortesla.teslafleetsdk.fixtures.Constants
import com.boltfortesla.teslafleetsdk.fixtures.Responses.PRODUCTS_RESPONSE
import com.boltfortesla.teslafleetsdk.net.JitterFactorCalculatorImpl
import com.boltfortesla.teslafleetsdk.net.NetworkExecutorImpl
import com.boltfortesla.teslafleetsdk.net.api.response.Product.EnergySite
import com.boltfortesla.teslafleetsdk.net.api.response.Product.Vehicle
import com.boltfortesla.teslafleetsdk.net.api.response.Product.Vehicle.GranularAccess
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Test

class FleetApiEndpointsImplTest {
  private val server = MockWebServer()
  private val fleetApi = createFleetApi(server.url("/").toString())
  private val retryConfig = TeslaFleetApi.RetryConfig()
  private val jitterFactorCalculator = JitterFactorCalculatorImpl()
  private val networkExecutor = NetworkExecutorImpl(retryConfig, jitterFactorCalculator)

  private val fleetEndpoints = FleetApiEndpointsImpl(fleetApi, networkExecutor)

  @Test
  fun getProducts_returnsProducts() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(PRODUCTS_RESPONSE))

    val response = fleetEndpoints.getProducts()

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/products")
    assertThat(request.body.readUtf8()).isEmpty()
    assertThat(response.getOrNull())
      .containsExactly(
        Vehicle(
          100021,
          429511308124,
          99999,
          Constants.VIN,
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
          false,
          null,
          null,
          null,
          "off",
        ),
        EnergySite(
          429124,
          "battery",
          "My Home",
          "STE12345678-12345",
          "1112345-00-E--TG0123456789",
          35425,
          39362,
          90,
          1000,
        ),
      )
  }
}
