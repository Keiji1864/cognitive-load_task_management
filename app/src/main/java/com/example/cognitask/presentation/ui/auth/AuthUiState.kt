package com.example.cognitask.presentation.ui.auth

sealed class AuthUiState {
    data object Idle : AuthUiState()

    data object Loading : AuthUiState()

    data class Success(val userId: Long) : AuthUiState()

    data class Error(val message: String) : AuthUiState()
}