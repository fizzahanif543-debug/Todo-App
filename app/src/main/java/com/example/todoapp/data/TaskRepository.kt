package com.example.todoapp.data

import android.content.Context
import com.example.todoapp.model.Priority
import com.example.todoapp.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(context: Context) {

    private val dao = AppDatabase.getInstance(context).taskDao()

    val tasks: Flow<List<Task>> = dao.getAllTasks().map { list -> list.map { it.toTask() } }

    suspend fun addTask(title: String, description: String, priority: Priority) {
        dao.insertTask(
            TaskEntity(
                title = title,
                description = description,
                priority = priority.name,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun updateTask(id: Int, newTitle: String, newDescription: String, priority: Priority) {
        dao.updateTask(id, newTitle, newDescription, priority.name)
    }

    suspend fun toggleTask(id: Int) {
        dao.toggleDone(id)
    }

    suspend fun cyclePriority(id: Int, currentPriority: Priority) {
        val next = when (currentPriority) {
            Priority.LOW -> Priority.MEDIUM
            Priority.MEDIUM -> Priority.HIGH
            Priority.HIGH -> Priority.LOW
        }
        dao.updatePriority(id, next.name)
    }

    suspend fun deleteTask(id: Int) {
        dao.deleteById(id)
    }
}