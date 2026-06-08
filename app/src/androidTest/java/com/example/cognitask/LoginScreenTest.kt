package com.example.cognitask

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.cognitask.presentation.ui.auth.AuthUiState
import com.example.cognitask.presentation.ui.auth.LoginScreen
import com.example.cognitask.presentation.ui.theme.CogniTaskTheme
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {

    @get:Rule
    val rule = createComposeRule()

    // Тест 1: кнопка "Войти" заблокирована если поля пустые
    @Test
    fun loginButton_disabled_when_fields_empty() {
        rule.setContent {
            CogniTaskTheme {
                LoginScreen(
                    uiState = AuthUiState.Idle,
                    onLogin = { _, _ -> },
                    onLoginSuccess = {},
                    onNavigateToRegister = {}
                )
            }
        }
        rule.onNodeWithText("Войти").assertIsNotEnabled()
    }

    // Тест 2: экран логина отображается корректно
    @Test
    fun loginScreen_displays_correctly() {
        rule.setContent {
            CogniTaskTheme {
                LoginScreen(
                    uiState = AuthUiState.Idle,
                    onLogin = { _, _ -> },
                    onLoginSuccess = {},
                    onNavigateToRegister = {}
                )
            }
        }
        rule.onNodeWithText("Войти").assertIsDisplayed()
        rule.onNodeWithText("Нет аккаунта?", substring = true).assertIsDisplayed()
    }
}