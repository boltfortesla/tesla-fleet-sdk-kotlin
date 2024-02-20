package com.boltfortesla.teslafleetsdk.net

import kotlin.random.Random

/** Implementation of [JitterFactorCalculator]. Jitter will be between 1% and 5% */
class JitterFactorCalculatorImpl : JitterFactorCalculator {
  /** Returns the jitter factor */
  override fun calculate(): Double {
    return Random.nextDouble(MAX_JITTER_FACTOR - MIN_JITTER_FACTOR) + MIN_JITTER_FACTOR
  }

  private companion object {
    private const val MIN_JITTER_FACTOR = 1.01
    private const val MAX_JITTER_FACTOR = 1.05
  }
}
