package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Query("SELECT * FROM child_profile WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<ChildProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: ChildProfile)

    @Update
    suspend fun updateProfile(profile: ChildProfile)

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<KidTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: KidTask)

    @Update
    suspend fun updateTask(task: KidTask)
    
    @Query("UPDATE child_profile SET totalStars = totalStars + :stars WHERE id = 1")
    suspend fun addStars(stars: Int)
}
