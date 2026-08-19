package com.example.todoapp.view.util

fun formatTimeAgo(createdAt: Long): String {
    val now = System.currentTimeMillis()
    val diffMillis = now - createdAt

    val minutes = diffMillis / (1000 * 60)
    val hours = diffMillis / (1000 * 60 * 60)
    val days = diffMillis / (1000 * 60 * 60 * 24)

    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "$minutes min ago"
        hours < 24 -> "$hours hr ago"
        days < 7 -> "$days d ago"
        else -> "${days / 7} wk ago"
    }
}
