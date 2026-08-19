@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.todoapp.view.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.todoapp.model.Priority

@Composable
fun PriorityDropdown(
    selected: Priority,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelect: (Priority) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        OutlinedTextField(
            value = priorityLabel(selected),
            onValueChange = {},
            readOnly = true,
            label = { Text("Priority") },
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
            leadingIcon = { PriorityDot(selected) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            Priority.entries.forEach { priority ->
                DropdownMenuItem(
                    text = { Text(priorityLabel(priority)) },
                    leadingIcon = { PriorityDot(priority) },
                    onClick = { onSelect(priority) }
                )
            }
        }
    }
}
