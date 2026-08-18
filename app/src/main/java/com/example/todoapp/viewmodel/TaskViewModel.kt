package com.example.todoapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.TaskRepository
import com.example.todoapp.model.Task
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TaskRepository(application)

    val tasks: StateFlow<List<Task>> = repository.tasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch { repository.seedIfEmpty() }
    }

    fun addTask(title: String) {
        if (title.isBlank()) return
        viewModelScope.launch { repository.addTask(title.trim()) }
    }

    fun toggleTask(id: Int) {
        viewModelScope.launch { repository.toggleTask(id) }
    }

    fun cyclePriority(id: Int) {
        viewModelScope.launch {
            val current = tasks.value.find { it.id == id } ?: return@launch
            repository.cyclePriority(id, current.priority)
        }
    }

    fun updateTask(id: Int, newTitle: String) {
        if (newTitle.isBlank()) return
        viewModelScope.launch { repository.updateTask(id, newTitle.trim()) }
    }

    fun deleteTask(id: Int) {
        viewModelScope.launch { repository.deleteTask(id) }
    }
}