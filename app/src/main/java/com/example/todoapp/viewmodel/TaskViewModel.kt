package com.example.todoapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todoapp.data.TaskRepository
import com.example.todoapp.model.BottomTab
import com.example.todoapp.model.Priority
import com.example.todoapp.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TaskRepository(application)

    val tasks: StateFlow<List<Task>> = repository.tasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedTab = MutableStateFlow(BottomTab.ALL)
    val selectedTab: StateFlow<BottomTab> = _selectedTab.asStateFlow()


    val filteredTasks: StateFlow<List<Task>> =
        combine(tasks, _searchQuery, _selectedTab) { allTasks, query, tab ->
            val byTab = when (tab) {
                BottomTab.ALL, BottomTab.CALENDAR -> allTasks
                BottomTab.PENDING -> allTasks.filter { !it.isDone }
                BottomTab.COMPLETED -> allTasks.filter { it.isDone }
            }
            val bySearch = if (query.isBlank()) {
                byTab
            } else {
                byTab.filter { task ->
                    val dueDateText = task.dueDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
                    task.title.contains(query, ignoreCase = true) ||
                            task.description.contains(query, ignoreCase = true) ||
                            dueDateText.contains(query, ignoreCase = true)
                }
            }
            bySearch.sortedBy { it.dueDate }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedTab(tab: BottomTab) {
        _selectedTab.value = tab
    }


    fun addTask(title: String, description: String, priority: Priority, dueDate: LocalDate? = null) {
        if (title.isBlank()) return
        viewModelScope.launch { repository.addTask(title.trim(), description.trim(), priority, dueDate) }
    }

    fun toggleTask(id: Int) {
        viewModelScope.launch { repository.toggleTask(id) }
    }

    fun updateTask(id: Int, newTitle: String, newDescription: String, priority: Priority, dueDate: LocalDate) {
        if (newTitle.isBlank()) return
        viewModelScope.launch { repository.updateTask(id, newTitle.trim(), newDescription.trim(), priority, dueDate) }
    }

    fun deleteTasks(ids: Set<Int>) {
        viewModelScope.launch {
            ids.forEach { repository.deleteTask(it) }
        }
    }
}