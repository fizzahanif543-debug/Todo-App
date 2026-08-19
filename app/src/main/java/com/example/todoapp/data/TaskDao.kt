package com.example.todoapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY id ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Insert
    suspend fun insertTask(task: TaskEntity)

    @Query("UPDATE tasks SET title = :newTitle, description = :newDescription WHERE id = :id")
    suspend fun updateTitleAndDescription(id: Int, newTitle: String, newDescription: String)

    @Query("UPDATE tasks SET title = :newTitle, description = :newDescription, priority = :priority WHERE id = :id")
    suspend fun updateTask(id: Int, newTitle: String, newDescription: String, priority: String)

    @Query("UPDATE tasks SET isDone = NOT isDone WHERE id = :id")
    suspend fun toggleDone(id: Int)

    @Query("UPDATE tasks SET priority = :priority WHERE id = :id")
    suspend fun updatePriority(id: Int, priority: String)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getTaskCount(): Int
}