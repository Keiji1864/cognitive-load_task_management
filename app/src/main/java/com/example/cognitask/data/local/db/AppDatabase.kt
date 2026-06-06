package com.example.cognitask.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cognitask.data.local.db.dao.TaskDao
import com.example.cognitask.data.local.db.dao.UserDao
import com.example.cognitask.data.local.db.entity.TaskEntity
import com.example.cognitask.data.local.db.entity.UserEntity

@Database(
    entities = [UserEntity::class, TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao

    companion object {
        const val DATABASE_NAME = "cognitask.db"
    }
}