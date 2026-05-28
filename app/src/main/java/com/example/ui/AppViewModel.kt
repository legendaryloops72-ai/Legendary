package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppRepository
import com.example.data.ChildProfile
import com.example.data.KidTask
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(private val repository: AppRepository) : ViewModel() {

    val profile: StateFlow<ChildProfile?> = repository.profile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val tasks: StateFlow<List<KidTask>> = repository.allTasks
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // تهيئة الملف الافتراضي تلقائياً لكي يفتح للطفل فوراً دون حواجز
        viewModelScope.launch {
            val exist = repository.profile.firstOrNull()
            if (exist == null) {
                repository.saveProfile(ChildProfile(id = 1, name = "البطل الصغير", totalStars = 15))
                addInitialTasks()
            }
        }
    }

    fun saveProfileName(name: String) {
        viewModelScope.launch {
            val current = profile.value
            if (current == null) {
                repository.saveProfile(ChildProfile(name = name, totalStars = 15))
                addInitialTasks()
            } else {
                repository.saveProfile(current.copy(name = name))
            }
        }
    }

    fun completeTask(task: KidTask) {
        viewModelScope.launch {
            if (!task.isCompleted) {
                repository.updateTask(task.copy(isCompleted = true))
                repository.addStars(task.starsReward)
            }
        }
    }

    fun awardQuizStars(stars: Int) {
        viewModelScope.launch {
            repository.addStars(stars)
        }
    }

    private fun addInitialTasks() {
        viewModelScope.launch {
            val initialTasks = listOf(
                KidTask(title = "ترتيب السرير الصباحي 🛏️", starsReward = 5),
                KidTask(title = "تنظيف الأسنان مرتين بالفرشاة 🪥", starsReward = 5),
                KidTask(title = "المشاهدة والتعلم اليومي المفيد 📚", starsReward = 5),
                KidTask(title = "غسل اليدين بالماء والصابون 🧼", starsReward = 5),
                KidTask(title = "مساعدة ماما وبابا في البيت 🏡", starsReward = 5)
            )
            initialTasks.forEach { repository.insertTask(it) }
        }
    }
}
