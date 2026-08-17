package com.example.todoapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.TaskRepository
import com.example.todoapp.model.Priority
import com.example.todoapp.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TaskRepository(application)

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _tasks.value = repository.seedIfEmpty()
        }
    }

    fun addTask(title: String) {
        if (title.isBlank()) return
        val newId = (_tasks.value.maxOfOrNull { it.id } ?: 0) + 1
        val updated = _tasks.value + Task(newId, title, priority = Priority.MEDIUM)
        _tasks.value = updated
        repository.saveTasks(updated)
    }

    fun toggleTask(id: Int) {
        val updated = _tasks.value.map {
            if (it.id == id) it.copy(isDone = !it.isDone) else it
        }
        _tasks.value = updated
        repository.saveTasks(updated)
    }

    fun cyclePriority(id: Int) {
        val updated = _tasks.value.map { task ->
            if (task.id == id) {
                val next = when (task.priority) {
                    Priority.LOW -> Priority.MEDIUM
                    Priority.MEDIUM -> Priority.HIGH
                    Priority.HIGH -> Priority.LOW
                }
                task.copy(priority = next)
            } else task
        }
        _tasks.value = updated
        repository.saveTasks(updated)
    }

    fun deleteTask(id: Int) {
        val updated = _tasks.value.filter { it.id != id }
        _tasks.value = updated
        repository.saveTasks(updated)
    }
}