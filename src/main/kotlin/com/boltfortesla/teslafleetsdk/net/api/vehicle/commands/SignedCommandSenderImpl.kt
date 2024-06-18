package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands

import com.boltfortesla.teslafleetsdk.TeslaFleetApi.SharedSecretFetcher
import com.boltfortesla.teslafleetsdk.commands.CommandSigner
import com.boltfortesla.teslafleetsdk.handshake.Handshaker
import com.boltfortesla.teslafleetsdk.handshake.SessionInfo
import com.boltfortesla.teslafleetsdk.handshake.SessionInfoRepository
import com.boltfortesla.teslafleetsdk.log.Log
import com.boltfortesla.teslafleetsdk.net.NetworkExecutor
import com.boltfortesla.teslafleetsdk.net.NetworkExecutor.Companion.HTTP_TOO_MANY_REQUESTS
import com.boltfortesla.teslafleetsdk.net.SignedMessagesFaultException
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.response.VehicleCommandResponse.CommandProtocolResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.response.VehicleCommandResponse.CommandProtocolResponse.InfotainmentResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.response.VehicleCommandResponse.CommandProtocolResponse.VehicleSecurityResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.VehicleEndpoints
import com.boltfortesla.teslafleetsdk.net.signedMessageFault
import com.google.protobuf.GeneratedMessageV3
import com.tesla.generated.carserver.server.CarServer
import com.tesla.generated.signatures.Signatures
import com.tesla.generated.universalmessage.UniversalMessage.Domain
import com.tesla.generated.universalmessage.UniversalMessage.RoutableMessage
import com.tesla.generated.vcsec.Vcsec
import com.tesla.generated.vcsec.Vcsec.UnsignedMessage
import java.lang.IllegalArgumentException
import java.util.Base64
import retrofit2.HttpException

/** Implementation of [SignedCommandSender] */
internal class SignedCommandSenderImpl(
  private val commandSigner: CommandSigner,
  private val vehicleEndpoints: VehicleEndpoints,
  private val networkExecutor: NetworkExecutor,
  private val sessionValidator: SessionValidator,
  private val sessionInfoRepository: SessionInfoRepository,
  private val handshaker: Handshaker,
  private val vin: String,
) : SignedCommandSender {
  override suspend fun signAndSend(
    message: GeneratedMessageV3,
    clientPublicKey: ByteArray,
    sharedSecretFetcher: SharedSecretFetcher,
  ): Result<CommandProtocolResponse> {
    val domain =
      when (message) {
        is UnsignedMessage -> Domain.DOMAIN_VEHICLE_SECURITY
        is CarServer.Action -> Domain.DOMAIN_INFOTAINMENT
        else ->
          throw IllegalArgumentException(
            "Unexpected message type: ${message::class.simpleName}. Expected an UnsignedMessage or CarServer.Action"
          )
      }

    return networkExecutor.execute {
      sessionInfoRepository.incrementCounter(vin, domain)
      val requestSessionInfo =
        sessionInfoRepository.get(vin, domain) ?: throw IllegalStateException("No session found")

      val signedRequestMessage =
        commandSigner.sign(vin, message, requestSessionInfo, domain, clientPublicKey)

      Log.d("Sending signed command to Fleet API")
      val result =
        vehicleEndpoints.signedCommand(
          Base64.getEncoder().encodeToString(signedRequestMessage.toByteArray())
        )
      val rawResponse = result.getOrNull()

      val responseMessage =
        RoutableMessage.parseFrom(Base64.getDecoder().decode(rawResponse?.responseBase64 ?: ""))
      responseMessage.signedMessageFault()?.let {
        recoverSession(
          responseMessage,
          signedRequestMessage,
          requestSessionInfo,
          domain,
          sharedSecretFetcher,
        )
        throw SignedMessagesFaultException(it)
      }

      // Immediately rethrow a 429 as unrecoverable, because the response message is not parsable in
      // this case
      (result.exceptionOrNull() as? HttpException)?.let {
        val response = it.response()
        if (it.code() == HTTP_TOO_MANY_REQUESTS && response != null)
          throw UnrecoverableHttpException(response)
      }

      when (responseMessage.fromDestination.domain) {
        Domain.DOMAIN_VEHICLE_SECURITY -> vehicleSecurityResponse(responseMessage)
        Domain.DOMAIN_INFOTAINMENT -> infotainmentResponse(responseMessage)
        else -> {
          throw IllegalStateException("Unexpected domain ${responseMessage.fromDestination.domain}")
        }
      }
    }
  }

  private fun vehicleSecurityResponse(routableMessage: RoutableMessage): VehicleSecurityResponse {
    val response =
      VehicleSecurityResponse(
        Vcsec.FromVCSECMessage.parseFrom(routableMessage.protobufMessageAsBytes)
      )
    if (response.hasError())
      throw ErrorResultException.VehicleSecurityFailureException(response.securityMessage)
    return response
  }

  private fun infotainmentResponse(routableMessage: RoutableMessage): InfotainmentResponse {
    val response =
      InfotainmentResponse(CarServer.Response.parseFrom(routableMessage.protobufMessageAsBytes))
    if (response.hasError())
      throw ErrorResultException.InfotainmentFailureException(response.response)
    return response
  }

  private suspend fun recoverSession(
    responseMessage: RoutableMessage,
    signedRequestMessage: RoutableMessage,
    requestSessionInfo: SessionInfo,
    domain: Domain,
    sharedSecretFetcher: SharedSecretFetcher,
  ) {
    Log.d("Recovering session after signed message afult")
    val responseSessionInfo = Signatures.SessionInfo.parseFrom(responseMessage.sessionInfo)
    val sessionIsValid =
      sessionValidator.isSessionValid(
        responseMessage,
        signedRequestMessage.uuid.toByteArray(),
        requestSessionInfo,
        vin,
      )
    if (sessionIsValid) {
      val newCounter =
        if (!requestSessionInfo.epoch.contentEquals(responseSessionInfo.epoch.toByteArray())) {
          Log.w(
            "Session epochs did not match, resetting session counter to ${responseSessionInfo.counter}"
          )
          responseSessionInfo.counter
        } else {
          requestSessionInfo.counter
        }
      Log.d("Session valid, updating from response")
      sessionInfoRepository.set(
        vin,
        domain,
        SessionInfo(
          responseSessionInfo.epoch.toByteArray(),
          responseSessionInfo.clockTime,
          newCounter,
          requestSessionInfo.sharedSecret,
        ),
      )
    } else {
      Log.d("Session is not valid, clearing session info and performing new handshake")
      sessionInfoRepository.remove(vin, domain)
      sessionInfoRepository.set(
        vin,
        domain,
        handshaker.performHandshake(vin, domain, sharedSecretFetcher),
      )
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
