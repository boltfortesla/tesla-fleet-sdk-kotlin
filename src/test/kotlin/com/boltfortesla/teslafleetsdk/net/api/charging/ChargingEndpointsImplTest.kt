package com.boltfortesla.teslafleetsdk.net.api.charging

import com.boltfortesla.teslafleetsdk.TeslaFleetApi
import com.boltfortesla.teslafleetsdk.fixtures.Constants
import com.boltfortesla.teslafleetsdk.fixtures.Responses.CHARGING_HISTORY_RESPONSE
import com.boltfortesla.teslafleetsdk.fixtures.Responses.CHARGING_SESSIONS_RESPONSE
import com.boltfortesla.teslafleetsdk.net.JitterFactorCalculatorImpl
import com.boltfortesla.teslafleetsdk.net.NetworkExecutorImpl
import com.boltfortesla.teslafleetsdk.net.api.charging.ChargingEndpoints.SortOrder
import com.boltfortesla.teslafleetsdk.net.api.charging.ChargingEndpoints.SortableField
import com.boltfortesla.teslafleetsdk.net.api.charging.response.ChargingHistoryResponse
import com.boltfortesla.teslafleetsdk.net.api.charging.response.ChargingHistoryResponse.ChargingHistoryEntry.Fee
import com.boltfortesla.teslafleetsdk.net.api.charging.response.ChargingHistoryResponse.ChargingHistoryEntry.Invoice
import com.boltfortesla.teslafleetsdk.net.api.charging.response.ChargingSessionsResponse
import com.boltfortesla.teslafleetsdk.net.api.charging.response.ChargingSessionsResponse.ChargingPeriod
import com.boltfortesla.teslafleetsdk.net.api.charging.response.ChargingSessionsResponse.ChargingPeriod.Dimension
import com.boltfortesla.teslafleetsdk.net.api.charging.response.ChargingSessionsResponse.ChargingSession
import com.boltfortesla.teslafleetsdk.net.api.charging.response.ChargingSessionsResponse.Location
import com.boltfortesla.teslafleetsdk.net.api.charging.response.ChargingSessionsResponse.Tariff
import com.boltfortesla.teslafleetsdk.net.api.charging.response.ChargingSessionsResponse.Tariff.TariffElement
import com.boltfortesla.teslafleetsdk.net.api.charging.response.ChargingSessionsResponse.Tariff.TariffElement.PriceComponent
import com.boltfortesla.teslafleetsdk.net.api.charging.response.ChargingSessionsResponse.Timestamp
import com.google.common.truth.Truth.assertThat
import com.google.gson.JsonObject
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Buffer
import org.junit.After
import org.junit.Test

class ChargingEndpointsImplTest {
  private val server = MockWebServer()
  private val chargingApi = createChargingApi(server.url("/").toString())
  private val retryConfig = TeslaFleetApi.RetryConfig()
  private val jitterFactorCalculator = JitterFactorCalculatorImpl()
  private val networkExecutor = NetworkExecutorImpl(retryConfig, jitterFactorCalculator)

  private val chargingEndpoints = ChargingEndpointsImpl(chargingApi, networkExecutor)

  @After
  fun tearDown() {
    server.shutdown()
  }

  @Test
  fun getChargingHistory_returnsChargingHistory() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(CHARGING_HISTORY_RESPONSE))

    val response =
      chargingEndpoints.getChargingHistory(
        vin = Constants.VIN,
        startTime =
          ZonedDateTime.of(
            2023,
            7,
            27,
            11,
            43,
            45,
            0,
            ZoneId.ofOffset("UTC", ZoneOffset.ofHours(-7))
          ),
        endTime =
          ZonedDateTime.of(
            2023,
            7,
            28,
            11,
            43,
            45,
            0,
            ZoneId.ofOffset("UTC", ZoneOffset.ofHours(-7))
          ),
        pageNumber = 0,
        pageSize = 5,
        sortBy = SortableField.VIN,
        sortOrder = SortOrder.ASC
      )

    val request = server.takeRequest()
    assertThat(request.path)
      .isEqualTo(
        "/api/1/dx/charging/history?vin=5YJ30123456789ABC&startTime=2023-07-27T11%3A43%3A45-07%3A00&endTime=2023-07-28T11%3A43%3A45-07%3A00&pageNo=0&pageSize=5&sortBy=vin&sortOrder=ASC"
      )
    assertThat(response.getOrNull())
      .isEqualTo(
        ChargingHistoryResponse(
          listOf(
            ChargingHistoryResponse.ChargingHistoryEntry(
              1234567,
              "TEST00000000VIN01",
              "Truckee, CA - Soaring Way",
              "2023-07-27T11:43:45-07:00",
              "2023-07-27T12:08:35-07:00",
              "2023-07-27T12:25:31-07:00",
              "US",
              listOf(
                Fee(
                  7654321,
                  "CHARGING",
                  "USD",
                  "PAYMENT",
                  0.46,
                  0,
                  0,
                  null,
                  null,
                  40,
                  0,
                  24,
                  null,
                  null,
                  18.4,
                  0,
                  0,
                  0,
                  0,
                  18.4,
                  18.4,
                  "kwh",
                  true,
                  "PAID"
                ),
                Fee(
                  87654321,
                  "PARKING",
                  "USD",
                  "NO_CHARGE",
                  0.0,
                  0,
                  0,
                  null,
                  null,
                  0,
                  0,
                  0,
                  null,
                  null,
                  0.0,
                  0,
                  0,
                  0,
                  0,
                  0.0,
                  0.0,
                  "min",
                  true,
                  "PAID"
                )
              ),
              "IMMEDIATE",
              invoices = listOf(Invoice("ABC-123NN-US.pdf", "abc-123-efg", "IMMEDIATE")),
              "TSLA"
            ),
          ),
          0
        )
      )
  }

  @Test
  fun getChargingInvoice_returnsInvoice() = runTest {
    val bytes = "abcd".toByteArray()
    server.enqueue(
      MockResponse().setResponseCode(200).setBody(Buffer().readFrom(bytes.inputStream()))
    )

    val response = chargingEndpoints.getChargingInvoice("id")

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/api/1/dx/charging/invoice/id")
    assertThat(response.getOrNull()!!.bytes()).isEqualTo(bytes)
  }

  @Test
  fun getChargingSessions_returnsSessions() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(CHARGING_SESSIONS_RESPONSE))

    val response =
      chargingEndpoints.getChargingSessions(
        vin = Constants.VIN,
        dateFrom =
          ZonedDateTime.of(
            2023,
            7,
            27,
            11,
            43,
            45,
            0,
            ZoneId.ofOffset("UTC", ZoneOffset.ofHours(-7))
          ),
        dateTo =
          ZonedDateTime.of(
            2023,
            7,
            28,
            11,
            43,
            45,
            0,
            ZoneId.ofOffset("UTC", ZoneOffset.ofHours(-7))
          ),
        limit = 5,
        offset = 0
      )

    val request = server.takeRequest()
    assertThat(request.path)
      .isEqualTo(
        "/api/1/dx/charging/sessions?vin=5YJ30123456789ABC&date_from=2023-07-27T11%3A43%3A45-07%3A00&date_to=2023-07-28T11%3A43%3A45-07%3A00&limit=5&offset=0"
      )
    assertThat(response.getOrNull())
      .isEqualTo(
        ChargingSessionsResponse(
          listOf(
            ChargingSession(
              listOf(ChargingPeriod(listOf(Dimension("ENERGY", 0)), "string")),
              "string",
              Location("string", "string"),
              "string",
              "string",
              "string",
              Tariff(
                "string",
                listOf(
                  TariffElement(
                    listOf(PriceComponent(0, 0, "ENERGY")),
                    mapOf("additionalProp1" to JsonObject()),
                  )
                )
              ),
              ChargingSessionsResponse.TotalCost(0, 0, 0),
              0,
              0,
              "string"
            )
          ),
          0,
          "string",
          Timestamp("string")
        ),
      )
  }
}
