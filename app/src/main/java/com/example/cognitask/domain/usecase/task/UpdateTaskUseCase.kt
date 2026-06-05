package com.example.cognitask.domain.usecase.task

import com.example.cognitask.domain.model.Task
import com.example.cognitask.domain.repository.TaskRepository
import javax.inject.Inject

class UpdateTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Result<Unit> = runCatching {
        require(task.title.isNotBlank()) { "Название задачи не может быть пустым" }
        repository.updateTask(task)
    }
}