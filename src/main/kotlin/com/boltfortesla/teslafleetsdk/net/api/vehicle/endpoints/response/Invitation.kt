package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response

import com.google.gson.annotations.SerializedName

data class Invitation(
  val id: Long,
  @SerializedName("owner_id") val ownerId: Long,
  @SerializedName("share_user_id") val shareUserId: Long?,
  @SerializedName("product_id") val productId: String,
  val state: String,
  val code: String,
  @SerializedName("expires_at") val expiresAt: String,
  @SerializedName("revoked_at") val revokedAt: String?,
  @SerializedName("borrowing_device_id") val borrowingDeviceId: String?,
  @SerializedName("key_id") val keyId: String?,
  @SerializedName("product_type") val productType: String,
  @SerializedName("share_type") val shareType: String,
  @SerializedName("share_user_sso_id") val shareUserSsoId: String?,
  @SerializedName("active_pubkeys") val activePubkeys: List<String?>,
  @SerializedName("id_s") val idS: String,
  @SerializedName("owner_id_s") val ownerIdS: String,
  @SerializedName("share_user_id_s") val shareUserIdS: String,
  @SerializedName("borrowing_key_hash") val borrowingKeyHash: String?,
  val vin: String,
  @SerializedName("share_link") val shareLink: String,
)
