package com.example.todoapp.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.todoapp.model.Task
import com.example.todoapp.view.components.EmptyState
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


private val DAY_CELL_SIZE = 40.dp


@Composable
fun CalendarScreen(tasks: List<Task>, onTaskClick: (Task) -> Unit) {
    var currentMonth by rememberSaveable { mutableStateOf(YearMonth.now().toString()) }
    var selectedDateEpochDay by rememberSaveable { mutableStateOf(LocalDate.now().toEpochDay()) }

    val yearMonth = YearMonth.parse(currentMonth)
    val selectedDate = LocalDate.ofEpochDay(selectedDateEpochDay)

    val tasksByDate = remember(tasks) { tasks.groupBy { it.dueDate } }
    val tasksForSelectedDate = tasksByDate[selectedDate].orEmpty()

    val firstOfMonth = yearMonth.atDay(1)
    val leadingBlanks = (firstOfMonth.dayOfWeek.value - DayOfWeek.MONDAY.value + 7) % 7
    val daysInMonth = yearMonth.lengthOfMonth()
    val totalCells = leadingBlanks + daysInMonth
    val rows = (totalCells + 6) / 7

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentMonth = yearMonth.minusMonths(1).toString() }) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "Previous month")
                }
                Text(
                    yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { currentMonth = yearMonth.plusMonths(1).toString() }) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Next month")
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
                DayOfWeek.entries.forEach { day ->
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                var dayCounter = 1
                repeat(rows) { rowIndex ->
                    Row(
                        modifier = Modifier.fillMaxWidth().height(DAY_CELL_SIZE + 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        repeat(7) { col ->
                            val cellIndex = rowIndex * 7 + col
                            if (cellIndex < leadingBlanks || dayCounter > daysInMonth) {
                                Box(modifier = Modifier.weight(1f).fillMaxHeight())
                            } else {
                                val date = yearMonth.atDay(dayCounter)
                                val hasDueTasks = tasksByDate.containsKey(date)
                                val isSelected = date == selectedDate
                                val isToday = date == LocalDate.now()

                                Box(
                                    modifier = Modifier.weight(1f).fillMaxHeight(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(DAY_CELL_SIZE)
                                            .clip(CircleShape)
                                            .background(
                                                when {
                                                    isSelected -> MaterialTheme.colorScheme.primary
                                                    isToday -> MaterialTheme.colorScheme.primaryContainer
                                                    else -> androidx.compose.ui.graphics.Color.Transparent
                                                }
                                            )
                                            .clickable { selectedDateEpochDay = date.toEpochDay() },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                dayCounter.toString(),
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                                else MaterialTheme.colorScheme.onSurface
                                            )
                                            if (hasDueTasks) {
                                                Box(
                                                    modifier = Modifier
                                                        .padding(top = 2.dp)
                                                        .size(4.dp)
                                                        .clip(CircleShape)
                                                        .background(
                                                            if (isSelected) MaterialTheme.colorScheme.onPrimary
                                                            else MaterialTheme.colorScheme.primary
                                                        )
                                                )
                                            }
                                        }
                                    }
                                }
                                dayCounter++
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            Text(
                "Tasks due on ${selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))}",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(16.dp)
            )

            if (tasksForSelectedDate.isEmpty()) {
                EmptyState(
                    message = "No tasks due this day.",
                    modifier = Modifier.fillMaxWidth().padding(24.dp)
                )
            }
        }

        items(tasksForSelectedDate, key = { it.id }) { task ->
            ListItem(
                headlineContent = { Text(task.title) },
                supportingContent = if (task.description.isNotBlank()) {
                    { Text(task.description, maxLines = 1) }
                } else null,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 2.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { onTaskClick(task) }
            )
        }
    }
}
