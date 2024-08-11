package com.boltfortesla.teslafleetsdk

import com.boltfortesla.teslafleetsdk.commands.CommandSignerImpl
import com.boltfortesla.teslafleetsdk.commands.HmacCommandAuthenticator
import com.boltfortesla.teslafleetsdk.crypto.HmacCalculatorImpl
import com.boltfortesla.teslafleetsdk.encoding.TlvEncoderImpl
import com.boltfortesla.teslafleetsdk.handshake.SessionInfo
import com.boltfortesla.teslafleetsdk.handshake.SessionInfoAuthenticatorImpl
import com.boltfortesla.teslafleetsdk.handshake.SessionInfoRepository
import com.boltfortesla.teslafleetsdk.handshake.SessionInfoRepositoryImpl
import com.boltfortesla.teslafleetsdk.keys.PublicKeyEncoderImpl
import com.boltfortesla.teslafleetsdk.net.JitterFactorCalculatorImpl
import com.boltfortesla.teslafleetsdk.net.api.FleetApiEndpointsFactory
import com.boltfortesla.teslafleetsdk.net.api.charging.ChargingEndpointsFactory
import com.boltfortesla.teslafleetsdk.net.api.energy.EnergyEndpointsFactory
import com.boltfortesla.teslafleetsdk.net.api.oauth.TeslaOauthFactory
import com.boltfortesla.teslafleetsdk.net.api.user.UserEndpointsFactory
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.VehicleCommandsFactory
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.VehicleEndpointsFactory
import com.google.common.truth.Truth.assertThat
import com.tesla.generated.universalmessage.UniversalMessage.Domain
import org.junit.Test

class TeslaFleetApiImplTest {
  private val publicKeyEncoder = PublicKeyEncoderImpl()
  private val tlvEncoder = TlvEncoderImpl()
  private val hmacCalculator = HmacCalculatorImpl()
  private val identifiers = IdentifiersImpl()
  private val jitterFactorCalculator = JitterFactorCalculatorImpl()
  private val sessionInfoRepository = SessionInfoRepositoryImpl()

  private val fleetApi =
    TeslaFleetApiImpl(
      null,
      "publicKey".toByteArray(),
      VehicleCommandsFactory(
        CommandSignerImpl(
          HmacCommandAuthenticator(hmacCalculator),
          tlvEncoder,
          publicKeyEncoder,
          identifiers,
        ),
        jitterFactorCalculator,
        publicKeyEncoder,
        SessionInfoAuthenticatorImpl(tlvEncoder, hmacCalculator),
        identifiers,
        sessionInfoRepository,
      ),
      FleetApiEndpointsFactory(jitterFactorCalculator),
      TeslaOauthFactory(jitterFactorCalculator),
      ChargingEndpointsFactory(jitterFactorCalculator),
      EnergyEndpointsFactory(jitterFactorCalculator),
      UserEndpointsFactory(jitterFactorCalculator),
      VehicleEndpointsFactory(jitterFactorCalculator),
      sessionInfoRepository,
    )

  @Test
  fun saveSessionInfo() {
    sessionInfoRepository.set(
      "vin1",
      Domain.DOMAIN_INFOTAINMENT,
      SessionInfo("epoch".toByteArray(), 0, 1, "sharedSecret".toByteArray()),
    )
    sessionInfoRepository.set(
      "vin1",
      Domain.DOMAIN_VEHICLE_SECURITY,
      SessionInfo("epoch2".toByteArray(), 0, 1, "sharedSecret2".toByteArray()),
    )
    sessionInfoRepository.set(
      "vin2",
      Domain.DOMAIN_INFOTAINMENT,
      SessionInfo("epoch3".toByteArray(), 0, 1, "sharedSecret3".toByteArray()),
    )

    assertThat(fleetApi.saveSessionInfo())
      .isEqualTo(ClassLoader.getSystemResource("serialized_session_info").readText())
  }

  @Test
  fun loadSessionInfo() {
    assertThat(sessionInfoRepository.getAll()).isEmpty()

    fleetApi.loadSessionInfo(ClassLoader.getSystemResource("serialized_session_info").readText())

    assertThat(sessionInfoRepository.getAll())
      .containsExactly(
        SessionInfoRepository.SessionInfoKey("vin1", Domain.DOMAIN_INFOTAINMENT),
        SessionInfo("epoch".toByteArray(), 0, 1, "sharedSecret".toByteArray()),
        SessionInfoRepository.SessionInfoKey("vin1", Domain.DOMAIN_VEHICLE_SECURITY),
        SessionInfo("epoch2".toByteArray(), 0, 1, "sharedSecret2".toByteArray()),
        SessionInfoRepository.SessionInfoKey("vin2", Domain.DOMAIN_INFOTAINMENT),
        SessionInfo("epoch3".toByteArray(), 0, 1, "sharedSecret3".toByteArray()),
      )
  }
}
