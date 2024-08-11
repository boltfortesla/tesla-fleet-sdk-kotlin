package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response

import com.google.gson.annotations.SerializedName

data class ServiceDataResponse(
  @SerializedName("service_status") val serviceStatus: String,
  @SerializedName("service_etc") val serviceEtc: String,
  @SerializedName("service_visit_number") val serviceVisitNumber: String,
  @SerializedName("status_id") val statusId: Long,
)
