package com.example.cognitask.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.cognitask.data.local.db.dao.TaskDao
import com.example.cognitask.data.local.db.dao.UserDao
import com.example.cognitask.data.local.db.entity.TaskEntity
import com.example.cognitask.data.local.db.entity.UserEntity

@Database(
    entities = [UserEntity::class, TaskEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao

    companion object {
        const val DATABASE_NAME = "cognitask.db"

        /**
         * Пример миграции v1 → v2 (раскомментировать при необходимости).
         * При добавлении новых полей — не менять существующие entity без новой миграции!
         *
         * val MIGRATION_1_2 = object : Migration(1, 2) {
         *     override fun migrate(db: SupportSQLiteDatabase) {
         *         db.execSQL("ALTER TABLE tasks ADD COLUMN some_new_column TEXT NOT NULL DEFAULT ''")
         *     }
         * }
         */
    }
}