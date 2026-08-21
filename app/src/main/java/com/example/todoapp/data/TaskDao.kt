package com.example.todoapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY id ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert
    suspend fun insertTask(task: TaskEntity)

    @Query("UPDATE tasks SET title = :newTitle, description = :newDescription, priority = :priority, dueDateEpochDay = :dueDateEpochDay WHERE id = :id")
    suspend fun updateTask(id: Int, newTitle: String, newDescription: String, priority: String, dueDateEpochDay: Long)

    @Query("UPDATE tasks SET isDone = NOT isDone WHERE id = :id")
    suspend fun toggleDone(id: Int)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: Int)
}