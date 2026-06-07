package com.example.cognitask.domain.usecase.task

import com.example.cognitask.data.local.datastore.SessionDataStore
import com.example.cognitask.domain.model.Task
import com.example.cognitask.domain.repository.TaskRepository
import javax.inject.Inject
import kotlin.math.ceil
import kotlin.math.max

class CompletePlanTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
    private val sessionDataStore: SessionDataStore
) {
    suspend operator fun invoke(task: Task, currentEnergy: Int) {
        val nowCompleted = !task.isCompleted

        taskRepository.updateTask(task.copy(isCompleted = nowCompleted))

        if (nowCompleted) {
            val decrease = ceil(task.effort / 3.0).toInt()  // effort 1-3→-1, 4-6→-2, 7-9→-3, 10→-4
            val newEnergy = max(1, currentEnergy - decrease)
            sessionDataStore.updateEnergyLevel(newEnergy)
        } else {
            val restore = ceil(task.effort / 3.0).toInt()
            val newEnergy = minOf(10, currentEnergy + restore)
            sessionDataStore.updateEnergyLevel(newEnergy)
        }
    }
}