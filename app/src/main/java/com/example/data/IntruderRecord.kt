package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "intruder_records")
data class IntruderRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val imagePath: String,
    val timestamp: Long,
    val targetAppName: String,
    val failedLockType: String
)
