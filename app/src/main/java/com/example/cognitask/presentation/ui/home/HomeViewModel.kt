package com.example.cognitask.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cognitask.data.local.datastore.SessionDataStore
import com.example.cognitask.domain.model.Task
import com.example.cognitask.domain.repository.UserRepository
import com.example.cognitask.domain.usecase.smartpick.SmartPickUseCase
import com.example.cognitask.domain.usecase.task.AddToDailyPlanUseCase
import com.example.cognitask.domain.usecase.task.CompletePlanTaskUseCase
import com.example.cognitask.domain.usecase.task.GetTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val recommended: List<Task> = emptyList(),
    val dailyPlan: List<Task> = emptyList(),
    val energyLevel: Int = 5,
    val isLoading: Boolean = true,
    val userName: String = "",
    val pendingSelection: Set<Long> = emptySet(),
    val showConfirmDialog: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val smartPickUseCase: SmartPickUseCase,
    private val addToDailyPlanUseCase: AddToDailyPlanUseCase,
    private val completePlanTask: CompletePlanTaskUseCase,
    private val sessionDataStore: SessionDataStore,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _pending = MutableStateFlow<Set<Long>>(emptySet())
    private val _showConfirm = MutableStateFlow(false)

    val uiState: StateFlow<HomeUiState> = sessionDataStore.userId
        .filter { it != -1L }
        .flatMapLatest { uid ->
            combine(
                getTasksUseCase(uid),
                sessionDataStore.energyLevel,
                _pending,
                _showConfirm
            ) { taskList, energy, pending, showConfirm ->
                val active = taskList.filter { !it.isCompleted && !it.isInDailyPlan }
                val recommended = smartPickUseCase(active, energy)
                val dailyPlan = taskList.filter { it.isInDailyPlan }
                val user = userRepository.getUserById(uid)

                HomeUiState(
                    recommended = recommended,
                    dailyPlan = dailyPlan,
                    energyLevel = energy,
                    userName = user?.name ?: "",
                    pendingSelection = pending,
                    showConfirmDialog = showConfirm,
                    isLoading = false
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState()
        )


    fun togglePending(taskId: Long) {
        _pending.update { current ->
            if (taskId in current) current - taskId else current + taskId
        }
    }

    fun addAllRecommended() {
        val ids = uiState.value.recommended.map { it.id }.toSet()
        _pending.update { ids }
    }

    fun requestConfirm() {
        if (_pending.value.isNotEmpty()) _showConfirm.value = true
    }

    fun confirmPlan() {
        val ids = _pending.value.toList()
        viewModelScope.launch {
            addToDailyPlanUseCase.addAll(ids)
            _pending.value = emptySet()
            _showConfirm.value = false
        }
    }

    fun dismissConfirm() {
        _showConfirm.value = false
    }


    fun completeTask(task: Task) {
        viewModelScope.launch {
            completePlanTask(task, uiState.value.energyLevel)
        }
    }

    fun removeFromPlan(taskId: Long) {
        viewModelScope.launch {
            addToDailyPlanUseCase.toggle(taskId, false)
        }
    }


    fun updateEnergy(level: Int) {
        viewModelScope.launch { sessionDataStore.updateEnergyLevel(level) }
    }
}