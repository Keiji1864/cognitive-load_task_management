package com.example.cognitask.domain.model

data class User(
    val id: Long = 0L,
    val name: String,
    val email: String,
    val passwordHash: String,
    val energyLevel: Int = 5   // 1–10
)