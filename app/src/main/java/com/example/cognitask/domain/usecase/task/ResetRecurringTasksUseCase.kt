package com.example.cognitask.domain.usecase.task

import com.example.cognitask.domain.repository.TaskRepository
import javax.inject.Inject

class ResetRecurringTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(userId: Long) =
        repository.resetRecurringTasks(userId)
}