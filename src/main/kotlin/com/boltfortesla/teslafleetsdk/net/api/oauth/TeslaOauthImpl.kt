package com.boltfortesla.teslafleetsdk.net.api.oauth

import com.boltfortesla.teslafleetsdk.net.NetworkExecutor
import com.boltfortesla.teslafleetsdk.net.api.oauth.response.RefreshTokenResponse

/** Implementation of TeslaOauth */
internal class TeslaOauthImpl(
  private val oauthApi: OauthApi,
  private val networkExecutor: NetworkExecutor,
) : TeslaOauth {
  override suspend fun refreshToken(
    clientId: String,
    refreshToken: String
  ): Result<RefreshTokenResponse> {
    return networkExecutor.execute { oauthApi.refreshToken(clientId, refreshToken) }
  }
}
