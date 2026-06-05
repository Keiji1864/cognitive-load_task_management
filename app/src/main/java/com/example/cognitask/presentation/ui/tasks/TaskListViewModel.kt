package com.example.cognitask.presentation.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cognitask.data.local.datastore.SessionDataStore
import com.example.cognitask.domain.model.Task
import com.example.cognitask.domain.usecase.task.DeleteTaskUseCase
import com.example.cognitask.domain.usecase.task.GetTasksUseCase
import com.example.cognitask.domain.usecase.task.SortTasksUseCase
import com.example.cognitask.domain.usecase.task.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val sortTasksUseCase: SortTasksUseCase,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _sortOrder = MutableStateFlow(SortTasksUseCase.SortOrder.BY_CREATED)
    val sortOrder: StateFlow<SortTasksUseCase.SortOrder> = _sortOrder.asStateFlow()

    val tasks: StateFlow<List<Task>> = combine(
        sessionDataStore.userId
            .filter { it != -1L }
            .flatMapLatest { userId -> getTasksUseCase(userId) },
        _sortOrder
    ) { taskList, order ->
        sortTasksUseCase(taskList, order)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun setSortOrder(order: SortTasksUseCase.SortOrder) {
        _sortOrder.value = order
    }

    fun toggleComplete(task: Task) {
        viewModelScope.launch {
            updateTaskUseCase(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            deleteTaskUseCase(taskId)
        }
    }
}