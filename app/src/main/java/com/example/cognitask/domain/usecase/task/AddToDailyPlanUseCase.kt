package com.example.cognitask.domain.usecase.task

import com.example.cognitask.domain.repository.TaskRepository
import javax.inject.Inject

class AddToDailyPlanUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend fun toggle(taskId: Long, inPlan: Boolean) =
        repository.setInDailyPlan(taskId, inPlan)

    suspend fun addAll(taskIds: List<Long>) =
        repository.addAllToDailyPlan(taskIds)
}