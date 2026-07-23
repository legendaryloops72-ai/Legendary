package com.example.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ChildProfile::class, KidTask::class, GameScore::class, VaultMediaItem::class, IntruderRecord::class, BadgeItem::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}
