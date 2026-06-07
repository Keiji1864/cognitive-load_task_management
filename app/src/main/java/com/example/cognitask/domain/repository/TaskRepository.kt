package com.example.cognitask.domain.repository

import com.example.cognitask.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasksForUser(userId: Long): Flow<List<Task>>

    suspend fun getTaskById(id: Long): Task?

    suspend fun insertTask(task: Task): Long

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)

    suspend fun deleteTaskById(id: Long)

    fun getDailyPlanTasks(userId: Long): Flow<List<Task>>

    suspend fun setInDailyPlan(taskId: Long, inPlan: Boolean)

    suspend fun addAllToDailyPlan(taskIds: List<Long>)
}