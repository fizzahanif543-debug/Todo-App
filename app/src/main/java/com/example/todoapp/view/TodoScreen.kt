@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.todoapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.model.Priority
import com.example.todoapp.viewmodel.TaskViewModel
import androidx.compose.ui.unit.dp
import com.example.todoapp.view.components.AppBottomBar
import com.example.todoapp.model.BottomTab
import com.example.todoapp.view.components.EmptyState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun TodoScreen(viewModel: TaskViewModel = viewModel()) {
    val filteredTasks by viewModel.filteredTasks.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()

    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var editingTaskId by rememberSaveable { mutableStateOf<Int?>(null) }
    var selectedIds by rememberSaveable { mutableStateOf(setOf<Int>()) }
    var isSearchActive by rememberSaveable { mutableStateOf(false) }

    val inSelectionMode = selectedIds.isNotEmpty()
    val taskBeingEdited = editingTaskId?.let { id -> tasks.find { it.id == id } }
    val showingCalendar = selectedTab == BottomTab.CALENDAR

    Scaffold(
        topBar = {
            if (inSelectionMode) {
                SelectionTopBar(
                    selectedCount = selectedIds.size,
                    onCancel = { selectedIds = emptySet() },
                    onDelete = {
                        viewModel.deleteTasks(selectedIds)
                        selectedIds = emptySet()
                    }
                )
            } else {
                NormalTopBar(
                    isSearchActive = isSearchActive,
                    onSearchActiveChange = { isSearchActive = it },
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) }
                )
            }
        },
        floatingActionButton = {
            if (!inSelectionMode && !showingCalendar) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add task", tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        },
        bottomBar = {
            if (!inSelectionMode) {
                AppBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = { viewModel.setSelectedTab(it) }
                )
            }
        }
    ) { padding ->
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            if (showingCalendar) {
                CalendarScreen(
                    tasks = tasks,
                    onTaskClick = { editingTaskId = it.id }
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (filteredTasks.isEmpty()) {
                        EmptyState(
                            message = when {
                                searchQuery.isNotBlank() -> "No tasks match \"$searchQuery\""
                                selectedTab == BottomTab.PENDING -> "No pending tasks!"
                                selectedTab == BottomTab.COMPLETED -> "No completed tasks yet."
                                else -> "No tasks yet. Tap + to add one!"
                            },
                            modifier = Modifier.fillMaxSize().weight(1f)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().weight(1f),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredTasks, key = { it.id }) { task ->
                                TaskRow(
                                    task = task,
                                    isSelected = task.id in selectedIds,
                                    selectionMode = inSelectionMode,
                                    onToggleDone = { viewModel.toggleTask(task.id) },
                                    onDeleteTap = { viewModel.deleteTasks(setOf(task.id)) },
                                    onClick = {
                                        if (inSelectionMode) {
                                            selectedIds = if (task.id in selectedIds)
                                                selectedIds - task.id else selectedIds + task.id
                                        } else {
                                            editingTaskId = task.id
                                        }
                                    },
                                    onLongClick = { selectedIds = selectedIds + task.id },
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        TaskFormDialog(
            title = "Add Task",
            initialTitle = "",
            initialDescription = "",
            initialPriority = Priority.MEDIUM,
            initialDueDate = LocalDate.now(),
            minDueDate = LocalDate.now(),
            onDismiss = { showAddDialog = false },
            onConfirm = { t, d, p, due ->
                viewModel.addTask(t, d, p, due)
                showAddDialog = false
            }
        )
    }

    taskBeingEdited?.let { task ->
        val taskCreationDate = remember(task.createdAt) {
            Instant.ofEpochMilli(task.createdAt).atZone(ZoneId.systemDefault()).toLocalDate()
        }
        TaskFormDialog(
            title = "Edit Task",
            initialTitle = task.title,
            initialDescription = task.description,
            initialPriority = task.priority,
            initialDueDate = task.dueDate,
            minDueDate = taskCreationDate,
            onDismiss = { editingTaskId = null },
            onConfirm = { t, d, p, due ->
                viewModel.updateTask(task.id, t, d, p, due)
                editingTaskId = null
            }
        )
    }
}