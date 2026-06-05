package com.example.cognitask.data.mapper

import com.example.cognitask.data.local.db.entity.TaskEntity
import com.example.cognitask.domain.model.Recurrence
import com.example.cognitask.domain.model.Task

fun TaskEntity.toDomain(): Task = Task(
    id = id,
    userId = userId,
    title = title,
    description = description,
    importance = importance,
    effort = effort,
    deadline = deadline,
    recurrence = runCatching { Recurrence.valueOf(recurrence) }.getOrDefault(Recurrence.NONE),
    isCompleted = isCompleted,
    category = category,
    createdAt = createdAt
)

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    userId = userId,
    title = title,
    description = description,
    importance = importance,
    effort = effort,
    deadline = deadline,
    recurrence = recurrence.name,
    isCompleted = isCompleted,
    category = category,
    createdAt = createdAt
)