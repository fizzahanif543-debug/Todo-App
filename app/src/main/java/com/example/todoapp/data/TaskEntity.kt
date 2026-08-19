package com.example.todoapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todoapp.model.Priority
import com.example.todoapp.model.Task

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String = "",
    val isDone: Boolean = false,
    val priority: String = Priority.MEDIUM.name,
    val createdAt: Long = System.currentTimeMillis()
)

fun TaskEntity.toTask() = Task(
    id = id,
    title = title,
    description = description,
    isDone = isDone,
    priority = try { Priority.valueOf(priority) } catch (e: Exception) { Priority.MEDIUM },
    createdAt = createdAt
)