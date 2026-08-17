@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.todoapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.model.Priority
import com.example.todoapp.model.Task
import com.example.todoapp.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable

private fun priorityColor(priority: Priority): Color = when (priority) {
    Priority.LOW -> Color(0xFF4CAF50)
    Priority.MEDIUM -> Color(0xFFFFA726)
    Priority.HIGH -> Color(0xFFE53935)
}

private fun priorityLabel(priority: Priority): String = when (priority) {
    Priority.LOW -> "Low"
    Priority.MEDIUM -> "Medium"
    Priority.HIGH -> "High"
}

@Composable
fun TodoScreen(viewModel: TaskViewModel = viewModel()) {
    val tasks by viewModel.tasks.collectAsState()
    var newTaskText by rememberSaveable { mutableStateOf("") }

    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()

    fun submitTask() {
        if (newTaskText.isNotBlank()) {
            viewModel.addTask(newTaskText.trim())
            newTaskText = ""
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    "My Tasks",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = newTaskText,
                    onValueChange = { newTaskText = it },
                    label = { Text("Add a new task") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .bringIntoViewRequester(bringIntoViewRequester)
                        .onFocusEvent { focusState ->
                            if (focusState.isFocused) {
                                coroutineScope.launch { bringIntoViewRequester.bringIntoView() }
                            }
                        },
                    trailingIcon = {
                        IconButton(onClick = { submitTask() }) {
                            Icon(Icons.Filled.Add, contentDescription = "Add task")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Tap the colored bar on a task to change its priority",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (tasks.isEmpty()) {
                    Text(
                        "No tasks yet. Add one above!",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                }
            }

            items(tasks, key = { it.id }) { task ->
                SwipeableTaskRow(
                    task = task,
                    onToggle = { viewModel.toggleTask(task.id) },
                    onDelete = { viewModel.deleteTask(task.id) },
                    onPriorityTap = { viewModel.cyclePriority(task.id) },
                    modifier = Modifier
                        .animateItem()
                        .padding(vertical = 5.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
fun SwipeableTaskRow(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onPriorityTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFE53935))
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.White)
            }
        }
    ) {
        TaskRow(task = task, onToggle = onToggle, onPriorityTap = onPriorityTap)
    }
}

@Composable
fun TaskRow(task: Task, onToggle: () -> Unit, onPriorityTap: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(10.dp)
                    .height(52.dp)
                    .background(priorityColor(task.priority))
                    .clickable { onPriorityTap() }
            )

            Checkbox(checked = task.isDone, onCheckedChange = { onToggle() })

            Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                Text(
                    text = task.title,
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else null,
                    color = if (task.isDone)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = priorityLabel(task.priority),
                    fontSize = 11.sp,
                    color = priorityColor(task.priority)
                )
            }
        }
    }
}