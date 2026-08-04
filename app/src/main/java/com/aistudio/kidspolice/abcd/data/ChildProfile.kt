package com.aistudio.kidspolice.abcd.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "child_profile")
data class ChildProfile(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val totalStars: Int = 0,
    val parentMessage: String = ""
)
