package com.boltfortesla.teslafleetsdk.net

import com.boltfortesla.teslafleetsdk.log.Log

/** Interface for a Strategy of how to recover from a retryable Message Fault. */
internal fun interface MessageFaultRecoveryStrategy {
  /** Perform an action to recover from the message fault. */
  suspend fun recover()

  companion object {
    /** A [MessageFaultRecoveryStrategy] that does nothing. */
    val NONE = MessageFaultRecoveryStrategy { Log.i("Not recovering from message fault") }
  }
}
