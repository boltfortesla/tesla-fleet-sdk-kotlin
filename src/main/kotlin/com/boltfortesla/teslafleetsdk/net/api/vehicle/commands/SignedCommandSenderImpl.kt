package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.SharedSecretFetcher
import com.boltfortesla.teslafleetsdk.commands.CommandSigner
import com.boltfortesla.teslafleetsdk.handshake.SessionInfo
import com.boltfortesla.teslafleetsdk.log.Log
import com.boltfortesla.teslafleetsdk.net.HandshakeRecoveryStrategyFactory
import com.boltfortesla.teslafleetsdk.net.NetworkExecutor
import com.boltfortesla.teslafleetsdk.net.SignedMessagesFaultException
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.response.VehicleCommandResponse.CommandProtocolResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.response.VehicleCommandResponse.CommandProtocolResponse.InfotainmentResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.response.VehicleCommandResponse.CommandProtocolResponse.VehicleSecurityResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.VehicleEndpoints
import com.boltfortesla.teslafleetsdk.net.signedMessageFault
import com.google.protobuf.GeneratedMessageV3
import com.tesla.generated.carserver.server.CarServer
import com.tesla.generated.universalmessage.UniversalMessage
import com.tesla.generated.universalmessage.UniversalMessage.Domain
import com.tesla.generated.vcsec.Vcsec
import com.tesla.generated.vcsec.Vcsec.UnsignedMessage
import java.lang.IllegalArgumentException
import java.util.Base64

/** Implementation of [SignedCommandSender] */
internal class SignedCommandSenderImpl(
  private val commandSigner: CommandSigner,
  private val vehicleEndpoints: VehicleEndpoints,
  private val networkExecutor: NetworkExecutor,
  private val handshakeRecoveryStrategyFactory: HandshakeRecoveryStrategyFactory,
  private val vin: String,
) : SignedCommandSender {
  override suspend fun signAndSend(
    message: GeneratedMessageV3,
    sessionInfo: SessionInfo,
    clientPublicKey: ByteArray,
    sharedSecretFetcher: SharedSecretFetcher,
  ): Result<CommandProtocolResponse> {
    sessionInfo.counter.incrementAndGet()
    val domain =
      when (message) {
        is UnsignedMessage -> Domain.DOMAIN_VEHICLE_SECURITY
        is CarServer.Action -> Domain.DOMAIN_INFOTAINMENT
        else ->
          throw IllegalArgumentException(
            "Unexpected message type: ${message::class.simpleName}. Expected an UnsignedMessage or CarServer.Action"
          )
      }
    val signedMessage = commandSigner.sign(vin, message, sessionInfo, domain, clientPublicKey)
    Log.d("Sending signed command to Fleet API")
    return networkExecutor.execute(
      handshakeRecoveryStrategyFactory.create(vin, domain, sharedSecretFetcher)
    ) {
      val rawResponse =
        vehicleEndpoints
          .signedCommand(Base64.getEncoder().encodeToString(signedMessage.toByteArray()))
          .getOrNull()
      val routableMessage =
        UniversalMessage.RoutableMessage.parseFrom(
          Base64.getDecoder().decode(rawResponse?.responseBase64 ?: "")
        )
      routableMessage.signedMessageFault()?.let { throw SignedMessagesFaultException(it) }

      when (routableMessage.fromDestination.domain) {
        Domain.DOMAIN_VEHICLE_SECURITY -> {
          val response =
            VehicleSecurityResponse(
              Vcsec.FromVCSECMessage.parseFrom(routableMessage.protobufMessageAsBytes)
            )
          if (response.hasError())
            throw ErrorResultException.VehicleSecurityFailureException(response.securityMessage)
          response
        }
        Domain.DOMAIN_INFOTAINMENT -> {
          val response =
            InfotainmentResponse(
              CarServer.Response.parseFrom(routableMessage.protobufMessageAsBytes)
            )
          if (response.hasError())
            throw ErrorResultException.InfotainmentFailureException(response.response)
          response
        }
        else -> {
          throw IllegalStateException("Unexpected domain ${routableMessage.fromDestination.domain}")
        }
      }
    }
  }

  private fun CommandProtocolResponse?.hasError(): Boolean {
    return when (this) {
      is VehicleSecurityResponse -> {
        securityMessage.commandStatus.operationStatus ==
          Vcsec.OperationStatus_E.OPERATIONSTATUS_ERROR
      }
      is InfotainmentResponse -> {
        response.actionStatus.result == CarServer.OperationStatus_E.OPERATIONSTATUS_ERROR
      }
      else -> false
    }
  }
}
