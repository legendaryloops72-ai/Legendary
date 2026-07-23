package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "badges")
data class BadgeItem(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val isUnlocked: Boolean = false,
    val category: String = "general", // "tasks", "stars", "games", "stories", "coloring"
    val requiredCount: Int = 1,
    val currentProgress: Int = 0
)
