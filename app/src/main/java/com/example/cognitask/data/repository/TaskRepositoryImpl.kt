package com.example.cognitask.data.repository

import com.example.cognitask.data.local.db.dao.TaskDao
import com.example.cognitask.data.mapper.toDomain
import com.example.cognitask.data.mapper.toEntity
import com.example.cognitask.domain.model.Task
import com.example.cognitask.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getTasksForUser(userId: Long): Flow<List<Task>> =
        taskDao.getTasksByUser(userId).map { list -> list.map { it.toDomain() } }

    override suspend fun getTaskById(id: Long): Task? =
        taskDao.getById(id)?.toDomain()

    override suspend fun insertTask(task: Task): Long =
        taskDao.insert(task.toEntity())

    override suspend fun updateTask(task: Task) =
        taskDao.update(task.toEntity())

    override suspend fun deleteTask(task: Task) =
        taskDao.delete(task.toEntity())

    override suspend fun deleteTaskById(id: Long) =
        taskDao.deleteById(id)
}