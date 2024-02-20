package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands.response

import com.tesla.generated.carserver.server.CarServer
import com.tesla.generated.universalmessage.UniversalMessage
import com.tesla.generated.vcsec.Vcsec

/** Represents a response from a Vehicle Command */
sealed interface VehicleCommandResponse {
  /** Response from a Vehicle Command that did not use the Vehicle Command protocol. */
  data class CommandResponse(val response: Response) : VehicleCommandResponse {
    data class Response(val result: Boolean, val reason: String)
  }

  /** Response from a Vehicle Command that used the Vehicle Command protocol. */
  sealed interface CommandProtocolResponse : VehicleCommandResponse {

    /** Infotainment Vehicle Command response. */
    data class InfotainmentResponse(val response: CarServer.Response) : CommandProtocolResponse

    /** Vehicle Security Vehicle Command response. */
    data class VehicleSecurityResponse(val securityMessage: Vcsec.FromVCSECMessage) :
      CommandProtocolResponse

    /** Response received when performing a Handshake with a Vehicle. */
    data class HandshakeResponse(val routableMessage: UniversalMessage.RoutableMessage) :
      CommandProtocolResponse
  }
}
