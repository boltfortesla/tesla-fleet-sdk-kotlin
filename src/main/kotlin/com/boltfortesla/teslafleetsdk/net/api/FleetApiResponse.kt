package com.boltfortesla.teslafleetsdk.net.api

import com.google.gson.annotations.SerializedName

data class FleetApiResponse<T>(
  val response: T?,
  val error: String? = null,
  val errorDescription: String? = null,
  val count: Int? = null,
  val pagination: Pagination? = null,
) {
  data class Pagination(
    val previous: String?,
    val next: String?,
    val current: Int,
    @SerializedName("per_page") val perPage: Int,
    val pages: Int,
  )
}
