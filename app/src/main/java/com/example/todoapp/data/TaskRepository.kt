package com.example.todoapp.data

import android.content.Context
import com.example.todoapp.model.Priority
import com.example.todoapp.model.Task
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class TaskRepository(private val context: Context) {

    private val file: File
        get() = File(context.filesDir, "tasks.json")

    fun saveTasks(tasks: List<Task>) {
        val arr = JSONArray()
        tasks.forEach { task ->
            val obj = JSONObject()
            obj.put("id", task.id)
            obj.put("title", task.title)
            obj.put("isDone", task.isDone)
            obj.put("priority", task.priority.name)
            arr.put(obj)
        }
        file.writeText(arr.toString())
    }

    fun loadTasks(): List<Task> {
        if (!file.exists()) return emptyList()
        val text = file.readText()
        if (text.isBlank()) return emptyList()

        val arr = JSONArray(text)
        val tasks = mutableListOf<Task>()
        for (i in 0 until arr.length()) {
            val obj = arr.getJSONObject(i)
            val priority = try {
                Priority.valueOf(obj.optString("priority", "MEDIUM"))
            } catch (e: IllegalArgumentException) {
                Priority.MEDIUM
            }
            tasks.add(
                Task(
                    id = obj.getInt("id"),
                    title = obj.getString("title"),
                    isDone = obj.optBoolean("isDone", false),
                    priority = priority
                )
            )
        }
        return tasks
    }

    fun seedIfEmpty(): List<Task> {
        val existing = loadTasks()
        if (existing.isNotEmpty()) return existing

        val sample = listOf(
            Task(1, "Buy groceries", priority = Priority.MEDIUM),
            Task(2, "Complete Kotlin assignment", priority = Priority.HIGH),
            Task(3, "Read 20 pages of book", priority = Priority.LOW),
            Task(4, "Workout for 30 mins", priority = Priority.MEDIUM),
            Task(5, "Call mom", priority = Priority.HIGH)
        )
        saveTasks(sample)
        return sample
    }
}