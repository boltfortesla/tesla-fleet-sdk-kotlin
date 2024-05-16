package com.boltfortesla.teslafleetsdk

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.Region
import com.boltfortesla.teslafleetsdk.TeslaFleetApi.RetryConfig
import com.boltfortesla.teslafleetsdk.TeslaFleetApi.SharedSecretFetcher
import com.boltfortesla.teslafleetsdk.handshake.SessionInfo
import com.boltfortesla.teslafleetsdk.handshake.SessionInfoRepository
import com.boltfortesla.teslafleetsdk.handshake.SessionInfoRepository.SessionInfoKey
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
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.Base64
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
  private val sessionInfoRepository: SessionInfoRepository,
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
      clientBuilder.configure(accessToken),
    )

  override fun oAuth(
    region: Region,
    accessToken: String,
    retryConfig: RetryConfig,
    clientBuilder: OkHttpClient.Builder,
  ): TeslaOauth =
    teslaOauthFactory.create(region, retryConfig, clientBuilder.configure(accessToken))

  override fun chargingEndpoints(
    region: Region,
    accessToken: String,
    retryConfig: RetryConfig,
    clientBuilder: OkHttpClient.Builder,
  ): ChargingEndpoints =
    chargingEndpointsFactory.create(
      region,
      retryConfig,
      clientBuilder.configure(accessToken),
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
      clientBuilder.configure(accessToken),
    )

  override fun userEndpoints(
    region: Region,
    accessToken: String,
    retryConfig: RetryConfig,
    clientBuilder: OkHttpClient.Builder,
  ): UserEndpoints =
    userEndpointsFactory.create(region, retryConfig, clientBuilder.configure(accessToken))

  override fun vehicleCommands(
    vin: String,
    vehicleTag: String,
    sharedSecretFetcher: SharedSecretFetcher,
    commandProtocolSupported: Boolean,
    region: Region,
    accessToken: String,
    retryConfig: RetryConfig,
    clientBuilder: OkHttpClient.Builder,
  ): VehicleCommands =
    vehicleCommandsFactory.create(
      vin,
      vehicleTag,
      clientPublicKey,
      sharedSecretFetcher,
      commandProtocolSupported,
      region,
      retryConfig,
      clientBuilder.configure(accessToken),
    )

  override fun vehicleEndpoints(
    vehicleTag: String,
    region: Region,
    accessToken: String,
    retryConfig: RetryConfig,
    clientBuilder: OkHttpClient.Builder,
  ): VehicleEndpoints =
    vehicleEndpointsFactory.create(
      vehicleTag,
      region,
      retryConfig,
      clientBuilder.configure(accessToken),
    )

  override fun saveSessionInfo(): String {
    Log.d("Saving session info")
    val byteStream = ByteArrayOutputStream()
    ObjectOutputStream(byteStream).use { it.writeObject(sessionInfoRepository.getAll()) }
    byteStream.close()
    return Base64.getEncoder().encodeToString(byteStream.toByteArray())
  }

  override fun loadSessionInfo(sessionInfoBase64: String) {
    Log.d("Loading session info")
    val sessionInfoBytes = Base64.getDecoder().decode(sessionInfoBase64)
    val byteStream = ByteArrayInputStream(sessionInfoBytes)
    ObjectInputStream(byteStream).use {
      @Suppress("UNCHECKED_CAST")
      sessionInfoRepository.load(it.readObject() as Map<SessionInfoKey, SessionInfo>)
    }
    byteStream.close()
  }

  private fun OkHttpClient.Builder.withAuthInterceptor(accessToken: String) =
    build().newBuilder().addInterceptor(AuthenticationInterceptor(accessToken))

  private fun OkHttpClient.Builder.configure(accessToken: String) =
    this.withAuthInterceptor(accessToken).retryOnConnectionFailure(false)
}
