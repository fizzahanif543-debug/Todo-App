package com.example.todoapp.data

import android.content.Context
import com.example.todoapp.model.Priority
import com.example.todoapp.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskRepository(context: Context) {

    private val dao = AppDatabase.getInstance(context).taskDao()

    val tasks: Flow<List<Task>> = dao.getAllTasks().map { list -> list.map { it.toTask() } }

    suspend fun addTask(title: String) {
        dao.insertTask(TaskEntity(title = title, priority = Priority.MEDIUM.name))
    }

    suspend fun updateTask(id: Int, newTitle: String) {
        dao.updateTitle(id, newTitle)
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

    suspend fun seedIfEmpty() {
        if (dao.getTaskCount() == 0) {
            listOf(
                TaskEntity(title = "Buy groceries", priority = Priority.MEDIUM.name),
                TaskEntity(title = "Complete Kotlin assignment", priority = Priority.HIGH.name),
                TaskEntity(title = "Read 20 pages of book", priority = Priority.LOW.name),
                TaskEntity(title = "Workout for 30 mins", priority = Priority.MEDIUM.name),
                TaskEntity(title = "Call mom", priority = Priority.HIGH.name)
            ).forEach { dao.insertTask(it) }
        }
    }
}