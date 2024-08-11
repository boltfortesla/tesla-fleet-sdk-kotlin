package com.boltfortesla.teslafleetsdk.net.api.user

import com.boltfortesla.teslafleetsdk.net.NetworkExecutor

/** Implementation of [UserEndpoints] */
internal class UserEndpointsImpl(
  private val userApi: UserApi,
  private val networkExecutor: NetworkExecutor,
) : UserEndpoints {
  override suspend fun getBackupKey() = networkExecutor.execute { userApi.backupKey() }

  override suspend fun getFeatureConfig() = networkExecutor.execute { userApi.featureConfig() }

  override suspend fun getMe() = networkExecutor.execute { userApi.me() }

  override suspend fun getOrders() = networkExecutor.execute { userApi.orders() }

  override suspend fun getRegion() = networkExecutor.execute { userApi.region() }
}
