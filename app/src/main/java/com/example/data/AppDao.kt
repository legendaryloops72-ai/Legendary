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

    @Query("SELECT * FROM badges")
    fun getAllBadges(): Flow<List<BadgeItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadge(badge: BadgeItem)

    @Update
    suspend fun updateBadge(badge: BadgeItem)

    @Query("UPDATE badges SET isUnlocked = 1, currentProgress = requiredCount WHERE id = :badgeId")
    suspend fun unlockBadge(badgeId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: KidTask)

    @Update
    suspend fun updateTask(task: KidTask)

    @androidx.room.Delete
    suspend fun deleteTask(task: KidTask)
    
    @Query("UPDATE child_profile SET totalStars = totalStars + :stars WHERE id = 1")
    suspend fun addStars(stars: Int)

    @Query("SELECT bestScore FROM game_scores WHERE gameId = :gameId")
    fun getBestScore(gameId: String): Flow<Int?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBestScore(score: GameScore)

    // عمليات الخزانة الآمنة (Safe Vault)
    @Query("SELECT * FROM vault_media_items ORDER BY dateAdded DESC")
    fun getAllVaultItems(): Flow<List<VaultMediaItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVaultItem(item: VaultMediaItem): Long

    @androidx.room.Delete
    suspend fun deleteVaultItem(item: VaultMediaItem)

    @Query("SELECT * FROM vault_media_items WHERE id = :id LIMIT 1")
    suspend fun getVaultItemById(id: Long): VaultMediaItem?

    // عمليات سيلفي الدخيل (Intruder Selfie)
    @Query("SELECT * FROM intruder_records ORDER BY timestamp DESC")
    fun getAllIntruderRecords(): Flow<List<IntruderRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntruderRecord(record: IntruderRecord): Long

    @androidx.room.Delete
    suspend fun deleteIntruderRecord(record: IntruderRecord)

    @Query("DELETE FROM intruder_records")
    suspend fun deleteAllIntruderRecords()
}

@androidx.room.Entity(tableName = "game_scores")
data class GameScore(
    @androidx.room.PrimaryKey val gameId: String,
    val bestScore: Int
)
