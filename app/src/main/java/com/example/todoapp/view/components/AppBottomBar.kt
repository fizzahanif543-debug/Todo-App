package com.example.todoapp.view.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.todoapp.model.BottomTab


@Composable
fun AppBottomBar(
    selectedTab: BottomTab,
    onTabSelected: (BottomTab) -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == BottomTab.ALL,
            onClick = { onTabSelected(BottomTab.ALL) },
            icon = { Icon(Icons.Filled.List, contentDescription = null) },
            label = { Text("All") }
        )
        NavigationBarItem(
            selected = selectedTab == BottomTab.PENDING,
            onClick = { onTabSelected(BottomTab.PENDING) },
            icon = { Icon(Icons.Filled.Schedule, contentDescription = null) },
            label = { Text("Pending") }
        )
        NavigationBarItem(
            selected = selectedTab == BottomTab.COMPLETED,
            onClick = { onTabSelected(BottomTab.COMPLETED) },
            icon = { Icon(Icons.Filled.CheckCircle, contentDescription = null) },
            label = { Text("Completed") }
        )
        NavigationBarItem(
            selected = selectedTab == BottomTab.CALENDAR,
            onClick = { onTabSelected(BottomTab.CALENDAR) },
            icon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null) },
            label = { Text("Calendar") }
        )
    }
}
