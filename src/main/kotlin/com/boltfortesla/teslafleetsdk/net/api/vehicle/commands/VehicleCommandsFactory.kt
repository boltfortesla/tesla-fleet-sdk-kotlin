package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands

import com.boltfortesla.teslafleetsdk.Identifiers
import com.boltfortesla.teslafleetsdk.TeslaFleetApi.Region
import com.boltfortesla.teslafleetsdk.TeslaFleetApi.RetryConfig
import com.boltfortesla.teslafleetsdk.TeslaFleetApi.SharedSecretFetcher
import com.boltfortesla.teslafleetsdk.commands.CommandSigner
import com.boltfortesla.teslafleetsdk.handshake.HandshakerImpl
import com.boltfortesla.teslafleetsdk.handshake.SessionInfoAuthenticator
import com.boltfortesla.teslafleetsdk.handshake.SessionInfoRepository
import com.boltfortesla.teslafleetsdk.keys.PublicKeyEncoder
import com.boltfortesla.teslafleetsdk.net.JitterFactorCalculator
import com.boltfortesla.teslafleetsdk.net.NetworkExecutorImpl
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.VehicleEndpointsImpl
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.createVehicleEndpointsApi
import okhttp3.OkHttpClient

/** Factory for [VehicleCommands]. */
internal class VehicleCommandsFactory(
  private val commandSigner: CommandSigner,
  private val jitterFactorCalculator: JitterFactorCalculator,
  private val publicKeyEncoder: PublicKeyEncoder,
  private val sessionInfoAuthenticator: SessionInfoAuthenticator,
  private val identifiers: Identifiers,
  private val sessionInfoRepository: SessionInfoRepository,
) {
  /**
   * Creates a [VehicleCommands] instance for the vehicle identified by [vin].
   *
   * @param vin the VIN for the vehicle to be command
   * @param clientPublicKey the Tesla Developer Application public key
   * @param region the [Region] the API calls should be made against
   * @param retryConfig a [RetryConfig]
   * @param sharedSecretFetcher a [SharedSecretFetcher] implementation for a Tesla Developer
   *   Application
   * @param clientBuilder a pre-configured [OkHttpClient.Builder] that will be used when making
   *   network requests
   */
  fun create(
    vin: String,
    clientPublicKey: ByteArray,
    sharedSecretFetcher: SharedSecretFetcher,
    commandProtocolSupported: Boolean,
    region: Region,
    retryConfig: RetryConfig,
    clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
  ): VehicleCommands {
    val networkExecutor = NetworkExecutorImpl(retryConfig, jitterFactorCalculator)
    val endpointsApi = createVehicleEndpointsApi(region.baseUrl, clientBuilder)
    val handshaker =
      HandshakerImpl(
        clientPublicKey,
        publicKeyEncoder,
        endpointsApi,
        sessionInfoAuthenticator,
        identifiers,
        networkExecutor,
      )

    val vehicleEndpoints = VehicleEndpointsImpl(vin, endpointsApi, networkExecutor)
    return VehicleCommandsImpl(
      vin,
      clientPublicKey,
      sharedSecretFetcher,
      commandProtocolSupported,
      handshaker,
      createVehicleCommandsApi(region.baseUrl, clientBuilder),
      networkExecutor,
      SignedCommandSenderImpl(
        commandSigner,
        vehicleEndpoints,
        networkExecutor,
        SessionValidatorImpl(sessionInfoAuthenticator),
        sessionInfoRepository,
        handshaker,
        vin,
      ),
      sessionInfoRepository,
      vehicleEndpoints,
    )
  }
}
