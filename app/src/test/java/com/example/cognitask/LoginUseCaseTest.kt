package com.example.cognitask

import com.example.cognitask.domain.usecase.auth.LoginUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginUseCaseTest {

    // Тест 1: пустой email -> ошибка
    @Test
    fun `blank email returns failure`() = runTest {
        val result = LoginUseCase.validate(email = "", password = "123456")
        assertTrue("Пустой email должен вернуть ошибку", result.isFailure)
    }

    // Тест 2: короткий пароль -> ошибка
    @Test
    fun `short password returns failure`() = runTest {
        val result = LoginUseCase.validate(email = "test@mail.ru", password = "123")
        assertTrue("Короткий пароль должен вернуть ошибку", result.isFailure)
    }

    // Тест 3: валидные данные -> успех
    @Test
    fun `valid credentials return success`() = runTest {
        val result = LoginUseCase.validate(email = "test@mail.ru", password = "123456")
        assertTrue("Валидные данные должны пройти валидацию", result.isSuccess)
    }
}