package com.boltfortesla.teslafleetsdk

import com.boltfortesla.teslafleetsdk.commands.CommandSignerImpl
import com.boltfortesla.teslafleetsdk.commands.HmacCommandAuthenticator
import com.boltfortesla.teslafleetsdk.crypto.HmacCalculatorImpl
import com.boltfortesla.teslafleetsdk.encoding.TlvEncoderImpl
import com.boltfortesla.teslafleetsdk.handshake.SessionInfoAuthenticatorImpl
import com.boltfortesla.teslafleetsdk.keys.PublicKeyEncoderImpl
import com.boltfortesla.teslafleetsdk.net.JitterFactorCalculatorImpl
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
import java.util.logging.Level
import okhttp3.OkHttpClient

/** Main entrypoint for the Fleet API. */
interface TeslaFleetApi {
  /**
   * API for general Fleet API endpoints not specific to a particular resource
   *
   * @param region the [Region] the API calls should be made to
   * @param accessToken Fleet API access token. Will be added to all requests
   * @param retryConfig a [RetryConfig] for network calls made with this API
   * @param clientBuilder a pre-configured [OkHttpClient.Builder] that will be used when making
   *   network requests
   */
  fun fleetApiEndpoints(
    region: Region,
    accessToken: String,
    retryConfig: RetryConfig = RetryConfig(),
    clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
  ): FleetApiEndpoints

  /**
   * API for actions related to authentication
   *
   * @param region the [Region] the API calls should be made to
   * @param accessToken Fleet API access token. Will be added to all requests
   * @param retryConfig a [RetryConfig] for network calls made with this API
   * @param clientBuilder a pre-configured [OkHttpClient.Builder] that will be used when making
   *   network requests
   */
  fun oAuth(
    region: Region,
    accessToken: String,
    retryConfig: RetryConfig = RetryConfig(),
    clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
  ): TeslaOauth

  /**
   * API for actions related to Charging
   *
   * @param region the [Region] the API calls should be made to
   * @param accessToken Fleet API access token. Will be added to all requests
   * @param retryConfig a [RetryConfig] for network calls made with this API
   * @param clientBuilder a pre-configured [OkHttpClient.Builder] that will be used when making
   *   network requests
   */
  fun chargingEndpoints(
    region: Region,
    accessToken: String,
    retryConfig: RetryConfig = RetryConfig(),
    clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
  ): ChargingEndpoints

  /**
   * API for actions related to the a Energy Sites (Powerwall)
   *
   * @param energySiteId the ID of the energy site to execute API requests for. This ID can be found
   *   by calling [FleetApiEndpoints.getProducts]
   * @param region the [Region] the API calls should be made to
   * @param accessToken Fleet API access token. Will be added to all requests
   * @param retryConfig a [RetryConfig] for network calls made with this API
   * @param clientBuilder a pre-configured [OkHttpClient.Builder] that will be used when making
   *   network requests
   */
  fun energyEndpoints(
    energySiteId: Int,
    region: Region,
    accessToken: String,
    retryConfig: RetryConfig = RetryConfig(),
    clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
  ): EnergyEndpoints

  /**
   * API for actions related to the a User
   *
   * @param region the [Region] the API calls should be made to
   * @param accessToken Fleet API access token. Will be added to all requests
   * @param retryConfig a [RetryConfig] for network calls made with this API
   * @param clientBuilder a pre-configured [OkHttpClient.Builder] that will be used when making
   *   network requests
   */
  fun userEndpoints(
    region: Region,
    accessToken: String,
    retryConfig: RetryConfig = RetryConfig(),
    clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
  ): UserEndpoints

  /**
   * API for commands that can be executed on a vehicle
   *
   * @param vin the VIN of the vehicle to send commands to
   * @param sharedSecretFetcher an implementation of [SharedSecretFetcher]
   * @param commandProtocolSupported if true, the Vehicle Command Protocol be used. If the Vehicle
   *   with VIN [vin] does NOT support the Command Protocol (a 422 is returned by the API), the
   *   Command Protocol will not be used. If false, the Vehicle Command Protocol will never be used
   *   (useful for Pre-2021 Model S/X vehicles to avoid an extra API call).
   * @param region the [Region] the API calls should be made to
   * @param accessToken Fleet API access token. Will be added to all requests
   * @param retryConfig a [RetryConfig] for network calls made with this API
   * @param clientBuilder a pre-configured [OkHttpClient.Builder] that will be used when making
   *   network requests
   */
  fun vehicleCommands(
    vin: String,
    sharedSecretFetcher: SharedSecretFetcher,
    commandProtocolSupported: Boolean,
    region: Region,
    accessToken: String,
    retryConfig: RetryConfig = RetryConfig(),
    clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
  ): VehicleCommands

  /**
   * API for actions related to a specific vehicle
   *
   * @param vin the VIN of the vehicle to make requests about
   * @param region the [Region] the API calls should be made to
   * @param accessToken Fleet API access token. Will be added to all requests
   * @param retryConfig a [RetryConfig] for network calls made with this API
   * @param clientBuilder a pre-configured [OkHttpClient.Builder] that will be used when making
   *   network requests
   */
  fun vehicleEndpoints(
    vin: String,
    region: Region,
    accessToken: String,
    retryConfig: RetryConfig = RetryConfig(),
    clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
  ): VehicleEndpoints

  /**
   * Interface to be implemented by users of [TeslaFleetApi.vehicleCommands] to generate a shared
   * secret for a Tesla Developer Application's private key and a Vehicle's public key.
   */
  fun interface SharedSecretFetcher {
    /**
     * Returns a [ByteArray] representing a derived shared 128-bit AES-GCM key using ECDH. The key
     * is generated using a Tesla Developer Application's private key and [vehiclePublicKey].
     *
     * See
     * https://github.com/teslamotors/vehicle-command/blob/main/pkg/protocol/protocol.md#key-agreement
     * for more information
     *
     * Please keep your private key secret!
     */
    suspend fun fetchSharedSecret(vehiclePublicKey: ByteArray): ByteArray
  }

  /**
   * Configuration for how network requests are retried.
   *
   * @param maxRetries the maximum number of times a network request will be retried. Defaults to
   *   [Int.MAX_VALUE]. In this case, the caller is expected to handle cancellation.
   * @param initialBackoffDelayMs the initial delay between retries. Defaults to 200ms
   * @param maxBackoffDelay the maximum delay between retries. Defaults to 2s.
   * @param backoffFactor the delay between retires is increased by this factor between attempts
   */
  data class RetryConfig(
    val maxRetries: Int = Int.MAX_VALUE,
    val initialBackoffDelayMs: Long = 200L,
    val maxBackoffDelay: Long = 2000L,
    val backoffFactor: Double = 1.5
  )

  enum class Region(val baseUrl: String, val authBaseUrl: String) {
    /** North America and Asia/Pacific */
    NA_APAC(
      "https://fleet-api.prd.na.vn.cloud.tesla.com",
      "https://auth.tesla.com",
    ),
    /** Europe, Middle East, Africa. */
    EMEA(
      "https://fleet-api.prd.eu.vn.cloud.tesla.com",
      "https://auth.tesla.com",
    ),
    /** China */
    CHINA("https://fleet-api.prd.cn.vn.cloud.tesla.cn", "https://auth.tesla.cn");

    override fun toString(): String {
      return "Region(name='$name', baseUrl='$baseUrl', authBaseUrl='$authBaseUrl')"
    }
  }

  fun interface Logger {
    fun log(level: Level, message: String, throwable: Throwable?)
  }

  companion object {
    /**
     * Returns a new instance of the [TeslaFleetApi]
     *
     * @param clientPublicKey the Public Key of a Tesla Developer Application
     * @param logger a [Logger], to intercept log messages
     */
    fun newInstance(clientPublicKey: ByteArray, logger: Logger? = null): TeslaFleetApi {
      val publicKeyEncoder = PublicKeyEncoderImpl()
      val tlvEncoder = TlvEncoderImpl()
      val hmacCalculator = HmacCalculatorImpl()
      val identifiers = IdentifiersImpl()
      val jitterFactorCalculator = JitterFactorCalculatorImpl()

      return TeslaFleetApiImpl(
        logger,
        clientPublicKey,
        VehicleCommandsFactory(
          CommandSignerImpl(
            // TODO: Make this switchable for AES for bluetooth
            HmacCommandAuthenticator(hmacCalculator),
            tlvEncoder,
            publicKeyEncoder,
            identifiers
          ),
          jitterFactorCalculator,
          publicKeyEncoder,
          SessionInfoAuthenticatorImpl(tlvEncoder, hmacCalculator),
          identifiers,
        ),
        FleetApiEndpointsFactory(jitterFactorCalculator),
        TeslaOauthFactory(jitterFactorCalculator),
        ChargingEndpointsFactory(jitterFactorCalculator),
        EnergyEndpointsFactory(jitterFactorCalculator),
        UserEndpointsFactory(jitterFactorCalculator),
        VehicleEndpointsFactory(jitterFactorCalculator)
      )
    }
  }
}
