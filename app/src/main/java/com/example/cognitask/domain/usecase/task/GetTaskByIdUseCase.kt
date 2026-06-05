package com.example.cognitask.domain.usecase.task

import com.example.cognitask.domain.model.Task
import com.example.cognitask.domain.repository.TaskRepository
import javax.inject.Inject

class GetTaskByIdUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(id: Long): Task? = repository.getTaskById(id)
}