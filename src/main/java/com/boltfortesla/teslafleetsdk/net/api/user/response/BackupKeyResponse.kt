package com.boltfortesla.teslafleetsdk.net.api.user.response

import com.google.gson.annotations.SerializedName

/** Response for getBackupKey */
data class BackupKeyResponse(@SerializedName("backup_key") val backupKey: String?)
