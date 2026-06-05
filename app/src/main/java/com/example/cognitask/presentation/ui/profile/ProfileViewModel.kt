package com.example.cognitask.presentation.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cognitask.data.local.datastore.SessionDataStore
import com.example.cognitask.domain.repository.UserRepository
import com.example.cognitask.domain.usecase.task.GetTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getTasksUseCase: GetTasksUseCase,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    data class ProfileUiState(
        val name: String = "",
        val email: String = "",
        val energyLevel: Int = 5,
        val totalTasks: Int = 0,
        val completedTasks: Int = 0,
        val isLoading: Boolean = true
    )

    val uiState: StateFlow<ProfileUiState> = sessionDataStore.userId
        .filter { it != -1L }
        .flatMapLatest { userId ->
            combine(
                getTasksUseCase(userId),
                sessionDataStore.energyLevel
            ) { tasks, energy ->
                val user = userRepository.getUserById(userId)
                ProfileUiState(
                    name = user?.name ?: "",
                    email = user?.email ?: "",
                    energyLevel = energy,
                    totalTasks = tasks.size,
                    completedTasks = tasks.count { it.isCompleted },
                    isLoading = false
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ProfileUiState()
        )

    fun updateEnergy(level: Int) {
        viewModelScope.launch { sessionDataStore.updateEnergyLevel(level) }
    }
}