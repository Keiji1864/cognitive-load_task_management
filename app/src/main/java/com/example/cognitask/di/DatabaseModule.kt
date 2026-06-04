package com.example.cognitask.di

import android.content.Context
import androidx.room.Room
import com.example.cognitask.data.local.db.AppDatabase
import com.example.cognitask.data.local.db.dao.TaskDao
import com.example.cognitask.data.local.db.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            // При смене схемы добавляй миграцию вместо fallbackToDestructiveMigration
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideTaskDao(db: AppDatabase): TaskDao = db.taskDao()
}