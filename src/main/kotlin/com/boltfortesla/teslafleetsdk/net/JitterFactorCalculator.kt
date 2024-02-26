package com.boltfortesla.teslafleetsdk.net

/** Calculates jitter factor for retries */
interface JitterFactorCalculator {
  /** Returns a double representing how much jitter to add to backoff delays */
  fun calculate(): Double
}
