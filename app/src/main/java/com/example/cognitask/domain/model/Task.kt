package com.example.cognitask.domain.model

data class Task(
    val id: Long = 0L,
    val userId: Long,
    val title: String,
    val description: String = "",
    val importance: Int,          // 1–5
    val effort: Int,              // 1–10
    val deadline: Long? = null,
    val recurrence: Recurrence = Recurrence.NONE,
    val isCompleted: Boolean = false,
    val category: String = "",
    val createdAt: Long = System.currentTimeMillis()
)