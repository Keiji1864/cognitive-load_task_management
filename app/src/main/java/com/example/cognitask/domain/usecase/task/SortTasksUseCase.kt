package com.example.cognitask.domain.usecase.task

import com.example.cognitask.domain.model.Task
import javax.inject.Inject

class SortTasksUseCase @Inject constructor() {

    enum class SortOrder(val label: String) {
        BY_IMPORTANCE("По важности"),
        BY_DEADLINE("По дедлайну"),
        BY_EFFORT("По усилиям"),
        BY_CREATED("По дате создания")
    }

    operator fun invoke(tasks: List<Task>, order: SortOrder): List<Task> = when (order) {
        SortOrder.BY_IMPORTANCE -> tasks.sortedByDescending { it.importance }
        SortOrder.BY_DEADLINE -> tasks.sortedWith(compareBy(nullsLast()) { it.deadline })
        SortOrder.BY_EFFORT -> tasks.sortedBy { it.effort }
        SortOrder.BY_CREATED -> tasks.sortedByDescending { it.createdAt }
    }
}