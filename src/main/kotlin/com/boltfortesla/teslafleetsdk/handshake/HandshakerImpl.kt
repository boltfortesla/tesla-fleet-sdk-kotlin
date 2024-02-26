package com.boltfortesla.teslafleetsdk.handshake

import com.boltfortesla.teslafleetsdk.Identifiers
import com.boltfortesla.teslafleetsdk.TeslaFleetApi.SharedSecretFetcher
import com.boltfortesla.teslafleetsdk.encoding.HexCodec.toHexString
import com.boltfortesla.teslafleetsdk.keys.PublicKeyEncoder
import com.boltfortesla.teslafleetsdk.log.Log
import com.boltfortesla.teslafleetsdk.net.NetworkExecutor
import com.boltfortesla.teslafleetsdk.net.SignedMessagesFaultException
import com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.response.VehicleCommandResponse.CommandProtocolResponse.HandshakeResponse
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.VehicleEndpointsApi
import com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.request.SignedCommandRequest
import com.boltfortesla.teslafleetsdk.net.signedMessageFault
import com.google.protobuf.ByteString
import com.tesla.generated.signatures.Signatures.SessionInfo as TeslaSessionInfo
import com.tesla.generated.signatures.signatureData
import com.tesla.generated.universalmessage.UniversalMessage.Domain
import com.tesla.generated.universalmessage.UniversalMessage.RoutableMessage
import com.tesla.generated.universalmessage.copy
import com.tesla.generated.universalmessage.destination
import com.tesla.generated.universalmessage.routableMessage
import com.tesla.generated.universalmessage.sessionInfoRequest
import java.util.Base64
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Implementation of [Handshaker].
 *
 * @param clientPublicKey the Public Key for the Tesla Developer Application being used.
 */
internal class HandshakerImpl(
  private val clientPublicKey: ByteArray,
  private val publicKeyEncoder: PublicKeyEncoder,
  private val vehicleEndpointsApi: VehicleEndpointsApi,
  private val sessionInfoAuthenticator: SessionInfoAuthenticator,
  private val identifiers: Identifiers,
  private val networkExecutor: NetworkExecutor,
) : Handshaker {
  private val handshakeMutex = Mutex()

  override suspend fun performHandshake(
    vin: String,
    domain: Domain,
    sharedSecretFetcher: SharedSecretFetcher,
  ): SessionInfo {
    handshakeMutex.withLock {
      val handshakeUuid = identifiers.randomUuid()
      // Create the message to be sent as the handshake
      val message = routableMessage {
        toDestination = destination { this.domain = domain }
        fromDestination = destination {
          routingAddress = ByteString.copyFrom(identifiers.randomRoutingAddress())
        }
        sessionInfoRequest = sessionInfoRequest {
          publicKey = ByteString.copyFrom(publicKeyEncoder.encodedPublicKey(clientPublicKey))
        }
        this.uuid = ByteString.copyFrom(handshakeUuid)
      }
      Log.d("Sending handshake message ${message.toByteArray().toHexString()}")

      // Make the request against the Fleet API to initiate the handshake
      val response =
        networkExecutor.execute {
          val response =
            vehicleEndpointsApi.sendSignedCommand(
              vin,
              SignedCommandRequest(Base64.getEncoder().encodeToString(message.toByteArray()))
            )
          val routableMessage =
            RoutableMessage.parseFrom(Base64.getDecoder().decode(response.responseBase64))
          routableMessage.signedMessageFault()?.let { throw SignedMessagesFaultException(it) }
          HandshakeResponse(routableMessage)
        }
      response.onFailure {
        Log.e("Handshake failed! Response code", throwable = response.exceptionOrNull())
        throw it
      }

      // Parse the response, extract the public key, and use generate the shared secret
      val responseMessage =
        response.getOrNull()?.routableMessage ?: RoutableMessage.getDefaultInstance()
      Log.d(
        "got handshake response: ${responseMessage.copy { signatureData = signatureData {  } }.toByteArray().toHexString()}"
      )
      val sessionInfo = TeslaSessionInfo.parseFrom(responseMessage.sessionInfo)
      Log.d("fetching shared secret")
      val sharedSecret = sharedSecretFetcher.fetchSharedSecret(sessionInfo.publicKey.toByteArray())
      Log.d("fetched shared secret with length ${sharedSecret.size}")
      // Verify that the response from the vehicle is valid
      sessionInfoAuthenticator.authenticate(
        sharedSecret,
        vin,
        handshakeUuid,
        sessionInfo.toByteArray(),
        responseMessage.signatureData.sessionInfoTag.tag.toByteArray()
      )

      return SessionInfo(
        sessionInfo.epoch.toByteArray(),
        sessionInfo.clockTime,
        sessionInfo.counter,
        sharedSecret,
      )
    }
  }
}
