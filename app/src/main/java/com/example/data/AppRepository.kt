package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {
    val profile: Flow<ChildProfile?> = appDao.getProfile()
    val allTasks: Flow<List<KidTask>> = appDao.getAllTasks()

    suspend fun saveProfile(profile: ChildProfile) = appDao.saveProfile(profile)
    
    suspend fun updateTask(task: KidTask) = appDao.updateTask(task)
    
    suspend fun insertTask(task: KidTask) = appDao.insertTask(task)
    
    suspend fun deleteTask(task: KidTask) = appDao.deleteTask(task)
    
    suspend fun addStars(stars: Int) = appDao.addStars(stars)

    fun getBestScore(gameId: String): Flow<Int?> = appDao.getBestScore(gameId)
    suspend fun saveBestScore(score: GameScore) = appDao.saveBestScore(score)
}
