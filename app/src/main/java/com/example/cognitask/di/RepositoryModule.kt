package com.example.cognitask.di

import com.example.cognitask.data.repository.TaskRepositoryImpl
import com.example.cognitask.data.repository.UserRepositoryImpl
import com.example.cognitask.domain.repository.TaskRepository
import com.example.cognitask.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}