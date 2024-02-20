package com.boltfortesla.teslafleetsdk.net

/** Responsible for executing network requests. */
internal interface NetworkExecutor {
  /**
   * Executes [action].
   *
   * Returns a [Result] containing the response object, or an exception on failure.
   */
  suspend fun <T> execute(
    action: suspend () -> T,
  ): Result<T>
}
