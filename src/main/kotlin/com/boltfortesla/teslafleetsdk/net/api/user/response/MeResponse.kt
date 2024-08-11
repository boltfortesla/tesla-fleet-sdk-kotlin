package com.boltfortesla.teslafleetsdk.net.api.user.response

import com.google.gson.annotations.SerializedName

/** Response for getMe */
data class MeResponse(
  val email: String,
  @SerializedName("full_name") val fullName: String,
  @SerializedName("profile_image_url") val profileImageUrl: String,
)
