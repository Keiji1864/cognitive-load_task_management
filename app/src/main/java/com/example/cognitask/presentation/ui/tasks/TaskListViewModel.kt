package com.example.cognitask.presentation.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cognitask.data.local.datastore.SessionDataStore
import com.example.cognitask.domain.model.Task
import com.example.cognitask.domain.usecase.task.DeleteTaskUseCase
import com.example.cognitask.domain.usecase.task.GetTasksUseCase
import com.example.cognitask.domain.usecase.task.SortTasksUseCase
import com.example.cognitask.domain.usecase.task.UpdateTaskUseCase
import com.example.cognitask.presentation.util.normalizeYo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val sortTasksUseCase: SortTasksUseCase,
    private val sessionDataStore: SessionDataStore
) : ViewModel() {

    private val _sortOrder = MutableStateFlow(SortTasksUseCase.SortOrder.BY_CREATED)
    private val _selectedCategory = MutableStateFlow<String?>(null)  // null = все

    val sortOrder: StateFlow<SortTasksUseCase.SortOrder> = _sortOrder.asStateFlow()
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val allTasks: Flow<List<Task>> = sessionDataStore.userId
        .filter { it != -1L }
        .flatMapLatest { userId -> getTasksUseCase(userId) }

    val categories: StateFlow<List<String>> = allTasks
        .map { tasks ->
            tasks.map { it.category.normalizeYo() }
                .filter { it.isNotBlank() }
                .distinct()
                .sorted()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val tasks: StateFlow<List<Task>> = combine(
        allTasks,
        _sortOrder,
        _selectedCategory
    ) { taskList, order, category ->
        val filtered = if (category == null) taskList
        else taskList.filter { it.category.normalizeYo() == category }
        sortTasksUseCase(filtered, order)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun setSortOrder(order: SortTasksUseCase.SortOrder) {
        _sortOrder.value = order
    }

    fun setCategory(category: String?) {
        _selectedCategory.value = category
    }

    fun toggleComplete(task: Task) {
        viewModelScope.launch { updateTaskUseCase(task.copy(isCompleted = !task.isCompleted)) }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch { deleteTaskUseCase(taskId) }
    }
}