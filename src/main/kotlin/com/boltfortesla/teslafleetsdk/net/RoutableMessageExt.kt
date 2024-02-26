package com.boltfortesla.teslafleetsdk.net

import com.tesla.generated.universalmessage.UniversalMessage.MessageFault_E
import com.tesla.generated.universalmessage.UniversalMessage.RoutableMessage

/** Returns the Signed Message Fault from a [RoutableMessage]. */
fun RoutableMessage.signedMessageFault(): MessageFault_E? {
  val fault = signedMessageStatus.signedMessageFault
  return if (fault != MessageFault_E.MESSAGEFAULT_ERROR_NONE) {
    fault
  } else {
    null
  }
}
