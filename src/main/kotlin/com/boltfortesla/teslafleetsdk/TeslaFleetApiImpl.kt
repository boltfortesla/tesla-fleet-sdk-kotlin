package com.boltfortesla.teslafleetsdk

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.Region
import com.boltfortesla.teslafleetsdk.TeslaFleetApi.RetryConfig
import com.boltfortesla.teslafleetsdk.TeslaFleetApi.SharedSecretFetcher
import com.boltfortesla.teslafleetsdk.log.Log
import com.boltfortesla.teslafleetsdk.net.AuthenticationInterceptor
import com.boltfortesla.teslafleetsdk.net.api.FleetApiEndpoints
import com.boltfortesla.teslafleetsdk.net.api.FleetApiEndpointsFactory
import com.boltfortesla.teslafleetsdk.net.api.charging.ChargingEndpoints
import com.boltfortesla.teslafleetsdk.net.api.charging.ChargingEndpointsFactory
import com.boltfortesla.teslafleetsdk.net.api.energy.EnergyEndpoints
import com.boltfortesla.teslafleetsdk.net.api.energy.EnergyEndpointsFactory
import com.boltfortesla.teslafleetsdk.net.api.oauth.TeslaOauth
import com.boltfortesla.teslafleetsdk.net.api.oauth.TeslaOauthFactory
import com.boltfortesla.teslafleetsdk.net.api.user.UserEndpoints
import com.boltfortesla.teslafleetsdk.net.api.user.UserEndpointsFactory
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.VehicleCommands
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.VehicleCommandsFactory
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.VehicleEndpoints
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.VehicleEndpointsFactory
import okhttp3.OkHttpClient

/** Implementation of [Fleet API]. */
internal class TeslaFleetApiImpl(
  private val logger: TeslaFleetApi.Logger?,
  private val clientPublicKey: ByteArray,
  private val vehicleCommandsFactory: VehicleCommandsFactory,
  private val fleetApiEndpointsFactory: FleetApiEndpointsFactory,
  private val teslaOauthFactory: TeslaOauthFactory,
  private val chargingEndpointsFactory: ChargingEndpointsFactory,
  private val energyEndpointsFactory: EnergyEndpointsFactory,
  private val userEndpointsFactory: UserEndpointsFactory,
  private val vehicleEndpointsFactory: VehicleEndpointsFactory,
) : TeslaFleetApi {

  init {
    logger?.let { Log.setLogger(logger) }
  }

  override fun fleetApiEndpoints(
    region: Region,
    accessToken: String,
    retryConfig: RetryConfig,
    clientBuilder: OkHttpClient.Builder,
  ): FleetApiEndpoints =
    fleetApiEndpointsFactory.create(
      region,
      retryConfig,
      clientBuilder.withAuthInterceptor(accessToken)
    )

  override fun oAuth(
    region: Region,
    accessToken: String,
    retryConfig: RetryConfig,
    clientBuilder: OkHttpClient.Builder,
  ): TeslaOauth =
    teslaOauthFactory.create(region, retryConfig, clientBuilder.withAuthInterceptor(accessToken))

  override fun chargingEndpoints(
    region: Region,
    accessToken: String,
    retryConfig: RetryConfig,
    clientBuilder: OkHttpClient.Builder,
  ): ChargingEndpoints =
    chargingEndpointsFactory.create(
      region,
      retryConfig,
      clientBuilder.withAuthInterceptor(accessToken)
    )

  override fun energyEndpoints(
    energySiteId: Int,
    region: Region,
    accessToken: String,
    retryConfig: RetryConfig,
    clientBuilder: OkHttpClient.Builder,
  ): EnergyEndpoints =
    energyEndpointsFactory.create(
      energySiteId,
      region,
      retryConfig,
      clientBuilder.withAuthInterceptor(accessToken)
    )

  override fun userEndpoints(
    region: Region,
    accessToken: String,
    retryConfig: RetryConfig,
    clientBuilder: OkHttpClient.Builder,
  ): UserEndpoints =
    userEndpointsFactory.create(region, retryConfig, clientBuilder.withAuthInterceptor(accessToken))

  override fun vehicleCommands(
    vin: String,
    sharedSecretFetcher: SharedSecretFetcher,
    commandProtocolSupported: Boolean,
    region: Region,
    accessToken: String,
    retryConfig: RetryConfig,
    clientBuilder: OkHttpClient.Builder,
  ): VehicleCommands =
    vehicleCommandsFactory.create(
      vin,
      clientPublicKey,
      sharedSecretFetcher,
      commandProtocolSupported,
      region,
      retryConfig,
      clientBuilder.withAuthInterceptor(accessToken)
    )

  override fun vehicleEndpoints(
    vin: String,
    region: Region,
    accessToken: String,
    retryConfig: RetryConfig,
    clientBuilder: OkHttpClient.Builder,
  ): VehicleEndpoints =
    vehicleEndpointsFactory.create(
      vin,
      region,
      retryConfig,
      clientBuilder.withAuthInterceptor(accessToken)
    )

  private fun OkHttpClient.Builder.withAuthInterceptor(
    accessToken: String,
  ) = build().newBuilder().addInterceptor(AuthenticationInterceptor(accessToken))
}
