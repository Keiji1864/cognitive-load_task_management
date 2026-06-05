package com.example.cognitask.presentation.ui.tasks

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cognitask.data.local.datastore.SessionDataStore
import com.example.cognitask.domain.model.Recurrence
import com.example.cognitask.domain.model.Task
import com.example.cognitask.domain.usecase.task.AddTaskUseCase
import com.example.cognitask.domain.usecase.task.GetTaskByIdUseCase
import com.example.cognitask.domain.usecase.task.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TaskFormUiState {
    data object Idle : TaskFormUiState()
    data object Loading : TaskFormUiState()
    data object Success : TaskFormUiState()
    data class Error(val message: String) : TaskFormUiState()
}

@HiltViewModel
class TaskFormViewModel @Inject constructor(
    private val addTaskUseCase: AddTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val getTaskByIdUseCase: GetTaskByIdUseCase,
    private val sessionDataStore: SessionDataStore,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: Long = savedStateHandle["taskId"] ?: -1L
    val isEditing: Boolean get() = taskId != -1L

    var title by mutableStateOf("")
    var description by mutableStateOf("")
    var importance by mutableStateOf(3)               // 1–5
    var effort by mutableStateOf(5)               // 1–10
    var deadline by mutableStateOf<Long?>(null)
    var recurrence by mutableStateOf(Recurrence.NONE)
    var category by mutableStateOf("")

    private val _uiState = MutableStateFlow<TaskFormUiState>(TaskFormUiState.Idle)
    val uiState: StateFlow<TaskFormUiState> = _uiState.asStateFlow()

    init {
        if (isEditing) loadTask()
    }

    private fun loadTask() {
        viewModelScope.launch {
            getTaskByIdUseCase(taskId)?.let { t ->
                title = t.title
                description = t.description
                importance = t.importance
                effort = t.effort
                deadline = t.deadline
                recurrence = t.recurrence
                category = t.category
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            _uiState.value = TaskFormUiState.Loading
            val userId = sessionDataStore.userId.first()
            val task = Task(
                id = if (isEditing) taskId else 0L,
                userId = userId,
                title = title.trim(),
                description = description.trim(),
                importance = importance,
                effort = effort,
                deadline = deadline,
                recurrence = recurrence,
                category = category.trim()
            )
            val result = if (isEditing) updateTaskUseCase(task)
            else addTaskUseCase(task).map { }
            result
                .onSuccess { _uiState.value = TaskFormUiState.Success }
                .onFailure { _uiState.value = TaskFormUiState.Error(it.message ?: "Ошибка") }
        }
    }

    fun resetState() {
        _uiState.value = TaskFormUiState.Idle
    }
}