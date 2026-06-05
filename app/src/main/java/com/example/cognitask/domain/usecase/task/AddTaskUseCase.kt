package com.example.cognitask.domain.usecase.task

import com.example.cognitask.domain.model.Task
import com.example.cognitask.domain.repository.TaskRepository
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Result<Long> = runCatching {
        require(task.title.isNotBlank()) { "Название задачи не может быть пустым" }
        require(task.importance in 1..5)  { "Важность: от 1 до 5" }
        require(task.effort in 1..10)     { "Усилия: от 1 до 10" }
        repository.insertTask(task)
    }
}