package com.boltfortesla.teslafleetsdk.net.api.vehicle.commands

import com.tesla.generated.carserver.server.CarServer.Response
import com.tesla.generated.vcsec.Vcsec

/** Exception for when an Fleet API call fails. */
sealed class ErrorResultException(override val message: String) : RuntimeException(message) {
  /** Thrown when a REST action fails (nonVehicle Command Protocol). */
  data class ActionFailureException(val response: String) : ErrorResultException(response)

  /** Thrown when a Infotainment Vehicle Command fails. */
  data class InfotainmentFailureException(val response: Response) :
    ErrorResultException(response.actionStatus.resultReason.plainText)

  /** Thrown when a Vehicle Security Command fails. */
  data class VehicleSecurityFailureException(val securityMessage: Vcsec.FromVCSECMessage) :
    ErrorResultException(
      securityMessage.commandStatus.signedMessageStatus.signedMessageInformation.name
    )
}
