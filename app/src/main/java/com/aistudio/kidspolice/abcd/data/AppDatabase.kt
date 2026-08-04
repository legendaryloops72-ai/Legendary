package com.aistudio.kidspolice.abcd.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ChildProfile::class, KidTask::class, GameScore::class, VaultMediaItem::class, IntruderRecord::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appDao(): AppDao
}
