package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {
    val profile: Flow<ChildProfile?> = appDao.getProfile()
    val allTasks: Flow<List<KidTask>> = appDao.getAllTasks()
    val allBadges: Flow<List<BadgeItem>> = appDao.getAllBadges()

    suspend fun saveProfile(profile: ChildProfile) = appDao.saveProfile(profile)
    
    suspend fun updateTask(task: KidTask) = appDao.updateTask(task)
    
    suspend fun insertTask(task: KidTask) = appDao.insertTask(task)
    
    suspend fun deleteTask(task: KidTask) = appDao.deleteTask(task)
    
    suspend fun addStars(stars: Int) = appDao.addStars(stars)

    suspend fun insertBadge(badge: BadgeItem) = appDao.insertBadge(badge)
    suspend fun updateBadge(badge: BadgeItem) = appDao.updateBadge(badge)
    suspend fun unlockBadge(badgeId: String) = appDao.unlockBadge(badgeId)

    fun getBestScore(gameId: String): Flow<Int?> = appDao.getBestScore(gameId)
    suspend fun saveBestScore(score: GameScore) = appDao.saveBestScore(score)
}
