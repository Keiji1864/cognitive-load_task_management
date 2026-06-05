package com.example.cognitask.domain.usecase.task

import com.example.cognitask.domain.model.Task
import com.example.cognitask.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    operator fun invoke(userId: Long): Flow<List<Task>> =
        repository.getTasksForUser(userId)
}