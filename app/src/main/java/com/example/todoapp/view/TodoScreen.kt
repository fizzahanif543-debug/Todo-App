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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.model.Priority
import com.example.todoapp.viewmodel.TaskViewModel
import androidx.compose.ui.unit.dp


@Composable
fun TodoScreen(viewModel: TaskViewModel = viewModel()) {
    val tasks by viewModel.tasks.collectAsState()

    var showAddDialog by rememberSaveable { mutableStateOf(false) }
    var editingTaskId by rememberSaveable { mutableStateOf<Int?>(null) }
    var selectedIds by rememberSaveable { mutableStateOf(setOf<Int>()) }

    val inSelectionMode = selectedIds.isNotEmpty()
    val taskBeingEdited = editingTaskId?.let { id -> tasks.find { it.id == id } }

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
                NormalTopBar()
            }
        },
        floatingActionButton = {
            if (!inSelectionMode) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add task", tint = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    ) { padding ->
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.padding(padding).fillMaxSize()
        ) {
            if (tasks.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "No tasks yet. Tap + to add one!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskRow(
                            task = task,
                            isSelected = task.id in selectedIds,
                            selectionMode = inSelectionMode,
                            onToggleDone = { viewModel.toggleTask(task.id) },
                            onPriorityTap = { viewModel.cyclePriority(task.id) },
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

    if (showAddDialog) {
        TaskFormDialog(
            title = "Add Task",
            initialTitle = "",
            initialDescription = "",
            initialPriority = Priority.MEDIUM,
            onDismiss = { showAddDialog = false },
            onConfirm = { t, d, p ->
                viewModel.addTask(t, d, p)
                showAddDialog = false
            }
        )
    }

    taskBeingEdited?.let { task ->
        TaskFormDialog(
            title = "Edit Task",
            initialTitle = task.title,
            initialDescription = task.description,
            initialPriority = task.priority,
            onDismiss = { editingTaskId = null },
            onConfirm = { t, d, p ->
                viewModel.updateTask(task.id, t, d, p)
                editingTaskId = null
            }
        )
    }
}