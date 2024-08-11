package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response

import com.google.gson.annotations.SerializedName

data class ReleaseNotesResponse(
  @SerializedName("release_notes") val releaseNotes: List<ReleaseNote>,
  @SerializedName("deployed_version") val deployedVersion: String,
  @SerializedName("staged_version") val stagedVersion: String?,
  @SerializedName("release_notes_version") val releaseNotesVersion: String
)

data class ReleaseNote(
  val title: String,
  val subtitle: String,
  val description: String,
  @SerializedName("customer_version") val customerVersion: String,
  val icon: String,
  @SerializedName("show_in_history") val showInHistory: Boolean,
  @SerializedName("image_url") val imageUrl: String,
  @SerializedName("light_image_url") val lightImageUrl: String
)
