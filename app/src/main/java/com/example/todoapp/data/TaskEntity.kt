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
    val isDone: Boolean = false,
    val priority: String = Priority.MEDIUM.name
)

fun TaskEntity.toTask() = Task(
    id = id,
    title = title,
    isDone = isDone,
    priority = try { Priority.valueOf(priority) } catch (e: Exception) { Priority.MEDIUM }
)
