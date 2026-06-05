package com.example.cognitask.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cognitask.data.local.datastore.SessionDataStore
import com.example.cognitask.domain.model.Task
import com.example.cognitask.domain.usecase.smartpick.SmartPickUseCase
import com.example.cognitask.domain.usecase.task.GetTasksUseCase
import com.example.cognitask.domain.usecase.task.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val recommended: List<Task> = emptyList(),
    val otherActive: List<Task> = emptyList(),
    val energyLevel: Int = 5,
    val isLoading: Boolean = true,
    val userName: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val smartPickUseCase: SmartPickUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        sessionDataStore.userId
            .filter { it != -1L }
            .flatMapLatest { uid -> getTasksUseCase(uid) },
        sessionDataStore.energyLevel
    ) { taskList, energy ->
        val active = taskList.filter { !it.isCompleted }
        val recommended = smartPickUseCase(active, energy)
        val recommendedIds = recommended.map { it.id }.toSet()

        HomeUiState(
            recommended = recommended,
            otherActive = active.filter { it.id !in recommendedIds },
            energyLevel = energy,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )

    fun updateEnergy(level: Int) {
        viewModelScope.launch {
            sessionDataStore.updateEnergyLevel(level)
        }
    }

    fun toggleComplete(task: Task) {
        viewModelScope.launch {
            updateTaskUseCase(task.copy(isCompleted = !task.isCompleted))
        }
    }
}