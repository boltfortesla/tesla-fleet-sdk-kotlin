package com.boltfortesla.teslafleetsdk.fixtures

import com.boltfortesla.teslafleetsdk.fixtures.Constants.HANDSHAKE_ROUTING_ADDRESS
import com.boltfortesla.teslafleetsdk.fixtures.Constants.HANDSHAKE_SESSION_INFO_TAG
import com.boltfortesla.teslafleetsdk.fixtures.Constants.REQUEST_UUID
import com.google.protobuf.ByteString
import com.tesla.generated.carserver.server.CarServer.OperationStatus_E
import com.tesla.generated.carserver.server.actionStatus
import com.tesla.generated.carserver.server.response
import com.tesla.generated.signatures.hMACSignatureData
import com.tesla.generated.signatures.sessionInfo
import com.tesla.generated.signatures.signatureData
import com.tesla.generated.universalmessage.UniversalMessage
import com.tesla.generated.universalmessage.UniversalMessage.MessageFault_E
import com.tesla.generated.universalmessage.UniversalMessage.RoutableMessage
import com.tesla.generated.universalmessage.destination
import com.tesla.generated.universalmessage.messageStatus
import com.tesla.generated.universalmessage.routableMessage
import com.tesla.generated.vcsec.Vcsec
import com.tesla.generated.vcsec.commandStatus
import com.tesla.generated.vcsec.fromVCSECMessage
import java.util.Base64

object Responses {
  val HANDSHAKE_RESPONSE = routableMessage {
    toDestination = destination { routingAddress = ByteString.fromHex(HANDSHAKE_ROUTING_ADDRESS) }
    fromDestination = destination { domain = UniversalMessage.Domain.DOMAIN_INFOTAINMENT }
    signatureData = signatureData {
      sessionInfoTag = hMACSignatureData { tag = ByteString.fromHex(HANDSHAKE_SESSION_INFO_TAG) }
    }
    sessionInfo =
      ByteString.fromHex(
        "0806124104c7a1f47138486aa4729971494878d33b1a24e39571f748a6e16c5955b3d877d3a6aaa0e955166474af5d32c410f439a2234137ad1bb085fd4e8813c958f11d971a104c463f9cc0d3d26906e982ed224adde6255a0a0000"
      )

    requestUuid = ByteString.fromHex(REQUEST_UUID)
  }

  val INFOTAINMENT_COMMAND_RESPONSE = routableMessage {
    toDestination = destination { routingAddress = ByteString.fromHex(HANDSHAKE_ROUTING_ADDRESS) }
    fromDestination = destination { domain = UniversalMessage.Domain.DOMAIN_INFOTAINMENT }
    protobufMessageAsBytes =
      response { actionStatus = actionStatus { result = OperationStatus_E.OPERATIONSTATUS_OK } }
        .toByteString()
  }

  val RETRYABLE_SIGNED_MESSAGE_FAULT_RESPONSE = routableMessage {
    signedMessageStatus = messageStatus {
      signedMessageFault = MessageFault_E.MESSAGEFAULT_ERROR_BUSY
    }
    sessionInfo =
      sessionInfo {
          counter = 6
          clockTime = 3000
          epoch = ByteString.copyFromUtf8("epoch")
        }
        .toByteString()
    signatureData = signatureData {
      sessionInfoTag = hMACSignatureData {
        tag = ByteString.fromHex("140595e7f1e79b9efa24225d36174bda8ee05001e1d2ac6093812eb57bae2caa")
      }
    }
  }

  val INFOTAINMENT_OPERATION_FAILURE_RESPONSE = routableMessage {
    toDestination = destination { routingAddress = ByteString.fromHex(HANDSHAKE_ROUTING_ADDRESS) }
    fromDestination = destination { domain = UniversalMessage.Domain.DOMAIN_INFOTAINMENT }
    protobufMessageAsBytes =
      response { actionStatus = actionStatus { result = OperationStatus_E.OPERATIONSTATUS_ERROR } }
        .toByteString()
  }

  val SECURITY_COMMAND_RESPONSE = routableMessage {
    toDestination = destination { routingAddress = ByteString.fromHex(HANDSHAKE_ROUTING_ADDRESS) }
    fromDestination = destination { domain = UniversalMessage.Domain.DOMAIN_VEHICLE_SECURITY }
    protobufMessageAsBytes =
      fromVCSECMessage {
          commandStatus = commandStatus {
            operationStatus = Vcsec.OperationStatus_E.OPERATIONSTATUS_OK
          }
        }
        .toByteString()
  }

  val SECURITY_OPERATION_FAILURE_RESPONSE = routableMessage {
    toDestination = destination { routingAddress = ByteString.fromHex(HANDSHAKE_ROUTING_ADDRESS) }
    fromDestination = destination { domain = UniversalMessage.Domain.DOMAIN_VEHICLE_SECURITY }
    protobufMessageAsBytes =
      fromVCSECMessage {
          commandStatus = commandStatus {
            operationStatus = Vcsec.OperationStatus_E.OPERATIONSTATUS_ERROR
          }
        }
        .toByteString()
  }

  val SIGNED_MESSAGE_FAULT_RESPONSE = routableMessage {
    signedMessageStatus = messageStatus {
      signedMessageFault = MessageFault_E.MESSAGEFAULT_ERROR_BAD_PARAMETER
    }

    sessionInfo =
      sessionInfo {
          counter = 6
          clockTime = 3000
          epoch = ByteString.copyFromUtf8("epoch")
        }
        .toByteString()

    signatureData = signatureData {
      sessionInfoTag = hMACSignatureData {
        tag = ByteString.fromHex("140595e7f1e79b9efa24225d36174bda8ee05001e1d2ac6093812eb57bae2caa")
      }
    }
  }

  fun signedCommandJson(routableMessage: RoutableMessage): String {
    return "{\"response\": \"${Base64.getEncoder().encodeToString(routableMessage.toByteArray())}\"}"
  }

  val CHARGING_HISTORY_RESPONSE by lazy { readResourceFile("charging_history.json") }
  val CHARGING_SESSIONS_RESPONSE by lazy { readResourceFile("charging_sessions.json") }

  val ENERGY_COMMAND_RESPONSE by lazy { readResourceFile("energy_command_response.json") }
  val BACKUP_HISTORY_RESPONSE by lazy { readResourceFile("backup_history_response.json") }
  val CHARGE_HISTORY_RESPONSE by lazy { readResourceFile("charge_history_response.json") }
  val ENERGY_HISTORY_RESPONSE by lazy { readResourceFile("energy_history_response.json") }
  val LIVE_STATUS_RESPONSE by lazy { readResourceFile("live_status_response.json") }
  val SITE_INFO_RESPONSE by lazy { readResourceFile("site_info_response.json") }
  val REFRESH_TOKEN_RESPONSE by lazy { readResourceFile("refresh_token_response.json") }

  val BACKUP_KEY_RESPONSE by lazy { readResourceFile("backup_key_response.json") }
  val FEATURE_CONFIG_RESOPNSE by lazy { readResourceFile("feature_config_response.json") }
  val ME_RESPONSE by lazy { readResourceFile("me_response.json") }
  val ORDERS_RESPONSE by lazy { readResourceFile("orders_response.json") }
  val REGION_RESPONSE by lazy { readResourceFile("region_response.json") }

  val PRODUCTS_RESPONSE by lazy { readResourceFile("products_response.json") }

  val CREATE_SHARE_INVITES_RESPONSE by lazy {
    readResourceFile("create_share_invites_response.json")
  }
  val DRIVERS_RESPONSE by lazy { readResourceFile("drivers_response.json") }
  val ELIGIBLE_SUBSCRIPTIONS_RESPONSE by lazy {
    readResourceFile("eligible_subscriptions_response.json")
  }
  val ELIGIBLE_UPGRADES_RESPONSE by lazy { readResourceFile("eligible_upgrades_response.json") }
  val FLEET_STATUS_RESPONSE by lazy { readResourceFile("fleet_status_response.json") }
  val FLEET_TELEMETRY_CONFIG_MODIFY_RESPONSE by lazy {
    readResourceFile("fleet_telemetry_config_modify_response.json")
  }
  val FLEET_TELEMETRY_CONFIG_RESPONSE by lazy {
    readResourceFile("fleet_telemetry_config_response.json")
  }
  val LIST_VEHICLES_RESPONSE by lazy { readResourceFile("list_vehicles_response.json") }
  val MOBILE_ENABLED_RESPONSE by lazy { readResourceFile("mobile_enabled_response.json") }
  val NEARBY_CHARGING_SITES_RESPONSE by lazy {
    readResourceFile("nearby_charging_sites_response.json")
  }
  val OPTIONS_RESPONSE by lazy { readResourceFile("options_response.json") }
  val RECENT_ALERTS_RESPONSE by lazy { readResourceFile("recent_alerts_response.json") }
  val REDEEM_SHARE_INVITES_RESPONSE by lazy {
    readResourceFile("redeem_share_invites_response.json")
  }
  val RELEASE_NOTES_RESPONSE by lazy { readResourceFile("release_notes_response.json") }
  val SERVICE_DATA_RESPONSE by lazy { readResourceFile("service_data_response.json") }
  val SHARE_INVITES_RESPONSE by lazy { readResourceFile("share_invites_response.json") }
  val SUBSCRIPTIONS_RESPONSE by lazy { readResourceFile("subscriptions_response.json") }
  val VEHICLE_DATA_RESPONSE by lazy { readResourceFile("vehicle_data_response.json") }
  val VEHICLE_RESPONSE by lazy { readResourceFile("vehicle_response.json") }
  val WARRANTY_DETAILS_RESPONSE by lazy { readResourceFile("warranty_details_response.json") }
  val WAKE_UP_RESPONSE by lazy { readResourceFile("wake_up_response.json") }

  private fun readResourceFile(fileName: String): String {
    return ClassLoader.getSystemResource(fileName).readText()
  }
}
