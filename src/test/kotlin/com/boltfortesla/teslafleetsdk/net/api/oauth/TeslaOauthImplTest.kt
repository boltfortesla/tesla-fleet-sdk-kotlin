package com.boltfortesla.teslafleetsdk.net.api.oauth

import com.boltfortesla.teslafleetsdk.TeslaFleetApi
import com.boltfortesla.teslafleetsdk.fixtures.Responses.REFRESH_TOKEN_RESPONSE
import com.boltfortesla.teslafleetsdk.net.JitterFactorCalculatorImpl
import com.boltfortesla.teslafleetsdk.net.NetworkExecutorImpl
import com.boltfortesla.teslafleetsdk.net.api.oauth.response.RefreshTokenResponse
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Test

class TeslaOauthImplTest {
  private val server = MockWebServer()
  private val oauthApi = createOauthApi(server.url("/").toString())
  private val retryConfig = TeslaFleetApi.RetryConfig()
  private val jitterFactorCalculator = JitterFactorCalculatorImpl()
  private val networkExecutor = NetworkExecutorImpl(retryConfig, jitterFactorCalculator)

  private val oauthEndpoints = TeslaOauthImpl(oauthApi, networkExecutor)

  @After
  fun tearDown() {
    server.shutdown()
  }

  @Test
  fun refreshToken_returnsTokens() = runTest {
    server.enqueue(MockResponse().setResponseCode(200).setBody(REFRESH_TOKEN_RESPONSE))

    val response = oauthEndpoints.refreshToken("clientId", "refreshToken")

    val request = server.takeRequest()
    assertThat(request.path).isEqualTo("/oauth2/v3/token")
    assertThat(request.body.readUtf8())
      .isEqualTo("client_id=clientId&refresh_token=refreshToken&grant_type=refresh_token")
    assertThat(response.getOrNull())
      .isEqualTo(
        RefreshTokenResponse(
          "MTQ0NjJkZmQ5OTM2NDE1ZTZjNGZmZjI3",
          "Bearer",
          3600,
          "IwOGYzYTlmM2YxOTQ5MGE3YmNmMDFkNTVk",
          "idToken",
          "state"
        )
      )
  }
}
