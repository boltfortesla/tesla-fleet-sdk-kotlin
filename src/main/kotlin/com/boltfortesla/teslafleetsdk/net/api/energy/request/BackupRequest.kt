package com.boltfortesla.teslafleetsdk.net.api.energy.request

import com.google.gson.annotations.SerializedName

/** Request for setBackup */
internal data class BackupRequest(
  @SerializedName("backup_reserve_percent") val backupReservePercent: Int
)
