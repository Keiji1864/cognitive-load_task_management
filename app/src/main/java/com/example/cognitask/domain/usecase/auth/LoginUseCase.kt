package com.example.cognitask.domain.usecase.auth

import com.example.cognitask.domain.model.User
import com.example.cognitask.domain.repository.UserRepository
import com.example.cognitask.domain.util.PasswordHasher
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Заполните все поля"))
        }

        val user = userRepository.getUserByEmail(email.trim().lowercase())
            ?: return Result.failure(IllegalArgumentException("Пользователь с таким email не найден"))

        if (user.passwordHash != PasswordHasher.hash(password)) {
            return Result.failure(IllegalArgumentException("Неверный пароль"))
        }

        return Result.success(user)
    }

    companion object {
        fun validate(email: String, password: String): Result<Unit> = runCatching {
            require(email.isNotBlank()) { "Email не может быть пустым" }
            require(password.length >= 6) { "Пароль минимум 6 символов" }
        }
    }
}