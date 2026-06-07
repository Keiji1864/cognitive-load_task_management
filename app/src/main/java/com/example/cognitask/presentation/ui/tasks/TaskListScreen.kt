package com.example.cognitask.presentation.ui.tasks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Label
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cognitask.domain.usecase.task.SortTasksUseCase
import com.example.cognitask.presentation.ui.components.TaskCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    onNavigateToForm: (taskId: Long) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: TaskListViewModel = hiltViewModel()
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    var showCategoryMenu by remember { mutableStateOf(false) }

    var showSortMenu by remember { mutableStateOf(false) }
    var deleteTaskId by remember { mutableStateOf<Long?>(null) }

    deleteTaskId?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteTaskId = null },
            title = { Text("Удалить задачу?") },
            text = { Text("Это действие нельзя отменить.") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteTask(id); deleteTaskId = null }) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteTaskId = null }) { Text("Отмена") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои задачи") },
                expandedHeight = 48.dp,
                actions = {
                    if (categories.isNotEmpty()) {
                        Box {
                            IconButton(onClick = { showCategoryMenu = true }) {
                                BadgedBox(
                                    badge = {
                                        if (selectedCategory != null) Badge()
                                    }
                                ) {
                                    Icon(Icons.Filled.Label, "Категория")
                                }
                            }
                            DropdownMenu(
                                expanded = showCategoryMenu,
                                onDismissRequest = { showCategoryMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            "Все категории",
                                            color = if (selectedCategory == null)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        viewModel.setCategory(null); showCategoryMenu = false
                                    }
                                )
                                HorizontalDivider()
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                cat.replaceFirstChar { it.uppercase() },
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                color = if (selectedCategory == cat)
                                                    MaterialTheme.colorScheme.primary
                                                else
                                                    MaterialTheme.colorScheme.onSurface
                                            )
                                        },
                                        onClick = {
                                            viewModel.setCategory(cat); showCategoryMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Filled.FilterList, "Сортировка")
                        }
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false }
                        ) {
                            SortTasksUseCase.SortOrder.entries.forEach { order ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            order.label,
                                            color = if (order == sortOrder)
                                                MaterialTheme.colorScheme.primary
                                            else
                                                MaterialTheme.colorScheme.onSurface
                                        )
                                    },
                                    onClick = {
                                        viewModel.setSortOrder(order); showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToForm(-1L) }) {
                Icon(Icons.Filled.Add, "Новая задача")
            }
        }
    ) { padding ->

        val active = tasks.filter { !it.isCompleted }
        val completed = tasks.filter { it.isCompleted }

        if (tasks.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Задач пока нет", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Нажми + чтобы добавить новую",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            if (selectedCategory != null) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .fillMaxWidth()
                ) {
                    InputChip(
                        selected = true,
                        onClick = { viewModel.setCategory(null) },
                        label = {
                            Text("Категория: ${selectedCategory!!.replaceFirstChar { it.uppercase() }}")
                        },
                        trailingIcon = {
                            Icon(
                                Icons.Filled.Close, "Сбросить",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (active.isNotEmpty()) {
                    item {
                        Text(
                            "Активные (${active.size})",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(active, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onToggleComplete = viewModel::toggleComplete,
                            onEdit = { onNavigateToForm(it) },
                            onDelete = { deleteTaskId = it }
                        )
                    }
                }
                if (completed.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Выполненные (${completed.size})",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    items(completed, key = { it.id }) { task ->
                        TaskCard(
                            task = task,
                            onToggleComplete = viewModel::toggleComplete,
                            onEdit = { onNavigateToForm(it) },
                            onDelete = { deleteTaskId = it }
                        )
                    }
                }
            }
        }
    }
}