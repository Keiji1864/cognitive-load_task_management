package com.example.cognitask.data.repository

import com.example.cognitask.data.local.db.dao.UserDao
import com.example.cognitask.data.mapper.toDomain
import com.example.cognitask.data.mapper.toEntity
import com.example.cognitask.domain.model.User
import com.example.cognitask.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override suspend fun getUserByEmail(email: String): User? =
        userDao.getByEmail(email)?.toDomain()

    override suspend fun getUserById(id: Long): User? =
        userDao.getById(id)?.toDomain()

    override suspend fun insertUser(user: User): Long =
        userDao.insert(user.toEntity())

    override suspend fun updateUser(user: User) =
        userDao.update(user.toEntity())
}