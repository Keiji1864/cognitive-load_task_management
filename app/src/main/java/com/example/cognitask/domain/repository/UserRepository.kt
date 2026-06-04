package com.example.cognitask.domain.repository

import com.example.cognitask.domain.model.User

interface UserRepository {
    suspend fun getUserByEmail(email: String): User?

    suspend fun getUserById(id: Long): User?

    suspend fun insertUser(user: User): Long

    suspend fun updateUser(user: User)
}