package com.example.cognitask.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cognitask.data.local.db.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE user_id = :userId ORDER BY created_at DESC")
    fun getTasksByUser(userId: Long): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM tasks WHERE user_id = :userId AND is_in_daily_plan = 1 ORDER BY created_at DESC")
    fun getDailyPlanTasks(userId: Long): Flow<List<TaskEntity>>

    @Query("UPDATE tasks SET is_in_daily_plan = :inPlan WHERE id = :taskId")
    suspend fun setInDailyPlan(taskId: Long, inPlan: Boolean)

    @Query("UPDATE tasks SET is_in_daily_plan = 1 WHERE id IN (:taskIds)")
    suspend fun addAllToDailyPlan(taskIds: List<Long>)

    @Query(
        """
    UPDATE tasks 
    SET is_completed = 0, is_in_daily_plan = 0 
    WHERE user_id = :userId AND recurrence != 'NONE'
"""
    )
    suspend fun resetRecurringTasks(userId: Long)
}