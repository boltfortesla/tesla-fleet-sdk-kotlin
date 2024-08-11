package com.boltfortesla.teslafleetsdk.crypto

import com.boltfortesla.teslafleetsdk.encoding.HexCodec.toHexString
import com.boltfortesla.teslafleetsdk.log.Log
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/** Implementation of [HmacCalculator] */
internal class HmacCalculatorImpl : HmacCalculator {
  override fun calculateSha256Hmac(key: ByteArray, data: ByteArray): ByteArray {
    Log.d("calculating HMAC with key length ${key.size} for data ${data.toHexString()}")
    return Mac.getInstance(ALGORITHM).apply { init(SecretKeySpec(key, ALGORITHM)) }.doFinal(data)
  }

  private companion object {
    private const val ALGORITHM = "HmacSHA256"
  }
}
