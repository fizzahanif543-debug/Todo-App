@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.todoapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.example.todoapp.model.Priority
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import com.example.todoapp.view.components.PriorityDropdown

@Composable
fun TaskFormDialog(
    title: String,
    initialTitle: String,
    initialDescription: String,
    initialPriority: Priority,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Priority) -> Unit
) {

    var taskTitle by rememberSaveable { mutableStateOf(initialTitle) }
    var taskDescription by rememberSaveable { mutableStateOf(initialDescription) }
    var selectedPriority by rememberSaveable { mutableStateOf(initialPriority) }
    var dropdownExpanded by rememberSaveable { mutableStateOf(false) }


    val maxContentHeight = LocalConfiguration.current.screenHeightDp.dp * 0.55f

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = maxContentHeight)
                    .verticalScroll(rememberScrollState())
                    .imePadding(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = taskDescription,
                    onValueChange = { taskDescription = it },
                    label = { Text("Description (optional)") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )

                PriorityDropdown(
                    selected = selectedPriority,
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = it },
                    onSelect = {
                        selectedPriority = it
                        dropdownExpanded = false
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(taskTitle, taskDescription, selectedPriority) },
                enabled = taskTitle.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
