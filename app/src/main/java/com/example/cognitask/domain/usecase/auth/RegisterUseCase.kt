package com.example.cognitask.domain.usecase.auth

import com.example.cognitask.domain.model.User
import com.example.cognitask.domain.repository.UserRepository
import com.example.cognitask.domain.util.PasswordHasher
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String,
        password: String,
        confirmPassword: String
    ): Result<User> {
        if (name.isBlank()) {
            return Result.failure(IllegalArgumentException("Введите имя"))
        }
        val trimmedEmail = email.trim().lowercase()
        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) {
            return Result.failure(IllegalArgumentException("Введите корректный email"))
        }
        if (password.length < 6) {
            return Result.failure(IllegalArgumentException("Пароль должен быть не короче 6 символов"))
        }
        if (password != confirmPassword) {
            return Result.failure(IllegalArgumentException("Пароли не совпадают"))
        }
        if (userRepository.getUserByEmail(trimmedEmail) != null) {
            return Result.failure(IllegalArgumentException("Этот email уже используется"))
        }

        val newUser = User(
            name         = name.trim(),
            email        = trimmedEmail,
            passwordHash = PasswordHasher.hash(password),
            energyLevel  = 5
        )
        val id = userRepository.insertUser(newUser)
        return Result.success(newUser.copy(id = id))
    }
}