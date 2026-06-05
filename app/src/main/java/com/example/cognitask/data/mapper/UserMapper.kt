package com.example.cognitask.data.mapper

import com.example.cognitask.data.local.db.entity.UserEntity
import com.example.cognitask.domain.model.User

fun UserEntity.toDomain(): User = User(
    id = id,
    name = name,
    email = email,
    passwordHash = passwordHash,
    energyLevel = energyLevel
)

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    name = name,
    email = email,
    passwordHash = passwordHash,
    energyLevel = energyLevel
)