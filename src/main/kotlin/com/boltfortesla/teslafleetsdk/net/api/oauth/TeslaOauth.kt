package com.boltfortesla.teslafleetsdk.net.api.oauth

import com.boltfortesla.teslafleetsdk.net.api.oauth.response.RefreshTokenResponse

/**
 * API for Oauth Endpoints
 *
 * See https://developer.tesla.com/docs/fleet-api#authentication for API documentation
 */
interface TeslaOauth {
  suspend fun refreshToken(clientId: String, refreshToken: String): Result<RefreshTokenResponse>
}
