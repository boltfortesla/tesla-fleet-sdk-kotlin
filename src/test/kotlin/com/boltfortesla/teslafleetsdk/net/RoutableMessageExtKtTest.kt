package com.boltfortesla.teslafleetsdk.net

import com.google.common.truth.Truth.assertThat
import com.tesla.generated.universalmessage.UniversalMessage.MessageFault_E
import com.tesla.generated.universalmessage.messageStatus
import com.tesla.generated.universalmessage.routableMessage
import org.junit.Test

class RoutableMessageExtKtTest {

  @Test
  fun signedMessageFault_hasFault_returnsFault() {
    assertThat(
        routableMessage {
            signedMessageStatus = messageStatus {
              signedMessageFault = MessageFault_E.MESSAGEFAULT_ERROR_BAD_PARAMETER
            }
          }
          .signedMessageFault()
      )
      .isEqualTo(MessageFault_E.MESSAGEFAULT_ERROR_BAD_PARAMETER)
  }

  @Test
  fun signedMessageFault_faultIsNone_returnsNull() {
    assertThat(
        routableMessage {
            signedMessageStatus = messageStatus {
              signedMessageFault = MessageFault_E.MESSAGEFAULT_ERROR_NONE
            }
          }
          .signedMessageFault()
      )
      .isNull()
  }
}
