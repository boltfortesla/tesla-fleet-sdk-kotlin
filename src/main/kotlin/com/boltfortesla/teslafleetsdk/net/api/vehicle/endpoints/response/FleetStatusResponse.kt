package com.boltfortesla.teslafleetsdk.net.api.vehicle.endpoints.response

import com.google.gson.annotations.SerializedName

data class FleetStatusResponse(
  @SerializedName("key_paired_vins") val keyPairedVins: List<String>,
  @SerializedName("unpaired_vins") val unpairedVins: List<String>
)
