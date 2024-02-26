package com.boltfortesla.teslafleetsdk.net

import com.tesla.generated.universalmessage.UniversalMessage.MessageFault_E

/** Exception thrown when there is a Signed Message fault. */
class SignedMessagesFaultException(val fault: MessageFault_E) : RuntimeException(fault.name)
