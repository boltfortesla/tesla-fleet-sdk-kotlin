package com.boltfortesla.teslafleetsdk.keys

import java.math.BigInteger
import java.security.KeyFactory
import java.security.interfaces.ECPublicKey
import java.security.spec.X509EncodedKeySpec

/** Implementation of [PublicKeyEncoder]. */
internal class PublicKeyEncoderImpl : PublicKeyEncoder {
  override fun encodedPublicKey(publicKeyBytes: ByteArray): ByteArray {
    val keyFactory = KeyFactory.getInstance(ALGORITHM)
    val keySpec = X509EncodedKeySpec(publicKeyBytes)
    val publicKey = keyFactory.generatePublic(keySpec) as ECPublicKey
    return byteArrayOf(0x04) +
      publicKey.w.affineX.toSignlessByteArray() +
      publicKey.w.affineY.toSignlessByteArray()
  }

  private fun BigInteger.toSignlessByteArray(): ByteArray {
    val array = toByteArray()
    return if (array[0].toInt() == 0) {
      ByteArray(array.size - 1).apply {
        System.arraycopy(array, /* srcPos= */ 1, /* dest= */ this, /* destPos= */ 0, this.size)
      }
    } else {
      array
    }
  }

  private companion object {
    private const val ALGORITHM = "EC"
  }
}
