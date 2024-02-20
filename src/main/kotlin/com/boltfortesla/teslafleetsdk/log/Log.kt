package com.boltfortesla.teslafleetsdk.log

import com.boltfortesla.teslafleetsdk.TeslaFleetApi
import java.util.logging.Level

internal object Log {
  private var logger: TeslaFleetApi.Logger? = null

  fun setLogger(logger: TeslaFleetApi.Logger) {
    this.logger = logger
  }

  fun d(message: String) {
    logger?.log(Level.FINEST, message, null)
  }

  fun w(message: String) {
    logger?.log(Level.WARNING, message, null)
  }

  fun e(message: String, throwable: Throwable?) {
    logger?.log(Level.SEVERE, message, throwable)
  }

  fun i(message: String) {
    logger?.log(Level.FINER, message, null)
  }
}
