package com.example.cognitask.presentation.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cognitask.data.local.datastore.SessionDataStore
import com.example.cognitask.domain.usecase.auth.LoginUseCase
import com.example.cognitask.domain.usecase.auth.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    val sessionUserId: StateFlow<Long?> = sessionDataStore.userId
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = null
        )

    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authState: StateFlow<AuthUiState> = _authState.asStateFlow()

    // Вход

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            loginUseCase(email, password)
                .onSuccess { user ->
                    sessionDataStore.saveSession(
                        userId = user.id,
                        energyLevel = user.energyLevel
                    )
                    _authState.value = AuthUiState.Success(user.id)
                }
                .onFailure { e ->
                    _authState.value = AuthUiState.Error(e.message ?: "Ошибка входа")
                }
        }
    }

    // Регистрация

    fun register(name: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            registerUseCase(name, email, password, confirmPassword)
                .onSuccess { user ->
                    sessionDataStore.saveSession(userId = user.id)
                    _authState.value = AuthUiState.Success(user.id)
                }
                .onFailure { e ->
                    _authState.value = AuthUiState.Error(e.message ?: "Ошибка регистрации")
                }
        }
    }

    //Выход

    fun logout() {
        viewModelScope.launch {
            sessionDataStore.clearSession()
            _authState.value = AuthUiState.Idle
        }
    }

    fun resetAuthState() {
        _authState.value = AuthUiState.Idle
    }
}