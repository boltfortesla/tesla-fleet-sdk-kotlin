package com.boltfortesla.teslafleetsdk.net

/** Responsible for executing network requests. */
internal interface NetworkExecutor {
  /**
   * Executes [action].
   *
   * Returns a [Result] containing the response object, or an exception on failure.
   *
   * @param messageFaultRecoveryStrategy a [MessageFaultRecoveryStrategy] to use if [action] fails
   *   with a retryable signed message fault
   */
  suspend fun <T> execute(
    messageFaultRecoveryStrategy: MessageFaultRecoveryStrategy = MessageFaultRecoveryStrategy.NONE,
    action: suspend () -> T,
  ): Result<T>
}
