package com.example.todoapp.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.todoapp.model.Priority

fun priorityColor(priority: Priority): Color = when (priority) {
    Priority.LOW -> Color(0xFF4CAF50)
    Priority.MEDIUM -> Color(0xFFFFA726)
    Priority.HIGH -> Color(0xFFE53935)
}

fun priorityLabel(priority: Priority): String = when (priority) {
    Priority.LOW -> "Low"
    Priority.MEDIUM -> "Medium"
    Priority.HIGH -> "High"
}

@Composable
fun PriorityBar(priority: Priority, height: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .width(10.dp)
            .height(height)
            .background(priorityColor(priority))
    )
}

@Composable
fun PriorityDot(priority: Priority) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .background(priorityColor(priority), shape = RoundedCornerShape(50))
    )
}
