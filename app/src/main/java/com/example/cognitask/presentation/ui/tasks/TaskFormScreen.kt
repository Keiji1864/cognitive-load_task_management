package com.example.cognitask.presentation.ui.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cognitask.domain.model.Recurrence
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormScreen(
    onNavigateBack: () -> Unit,
    viewModel: TaskFormViewModel = hiltViewModel()
) {
    val uiState   by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading  = uiState is TaskFormUiState.Loading

    var showDatePicker      by remember { mutableStateOf(false) }
    var showRecurrenceMenu  by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is TaskFormUiState.Success) {
            viewModel.resetState()
            onNavigateBack()
        }
    }

    // DatePicker
    if (showDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = viewModel.deadline
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton    = {
                TextButton(onClick = {
                    viewModel.deadline = pickerState.selectedDateMillis
                    showDatePicker     = false
                }) { Text("Выбрать") }
            },
            dismissButton    = {
                TextButton(onClick = { showDatePicker = false }) { Text("Отмена") }
            }
        ) { DatePicker(state = pickerState) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title           = { Text(if (viewModel.isEditing) "Редактировать" else "Новая задача") },
                navigationIcon  = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier            = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Название
            OutlinedTextField(
                value         = viewModel.title,
                onValueChange = { viewModel.title = it },
                label         = { Text("Название *") },
                singleLine    = true,
                enabled       = !isLoading,
                modifier      = Modifier.fillMaxWidth()
            )

            // Описание
            OutlinedTextField(
                value         = viewModel.description,
                onValueChange = { viewModel.description = it },
                label         = { Text("Описание") },
                minLines      = 2,
                maxLines      = 4,
                enabled       = !isLoading,
                modifier      = Modifier.fillMaxWidth()
            )

            // Важность 1–5
            Column {
                Text(
                    "Важность: ${viewModel.importance} / 5",
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier                = Modifier.fillMaxWidth(),
                    horizontalArrangement   = Arrangement.SpaceBetween
                ) {
                    (1..5).forEach { v ->
                        FilterChip(
                            selected = viewModel.importance == v,
                            onClick  = { viewModel.importance = v },
                            enabled  = !isLoading,
                            label    = { Text(v.toString()) },
                            colors   = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (v) {
                                    5    -> MaterialTheme.colorScheme.error
                                    4    -> MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                                    3    -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.85f)
                                    else -> MaterialTheme.colorScheme.secondaryContainer
                                }
                            )
                        )
                    }
                }
            }

            // Усилия 1–10
            Column {
                Text(
                    "Затраты сил: ${viewModel.effort} / 10",
                    style = MaterialTheme.typography.labelLarge
                )
                Slider(
                    value         = viewModel.effort.toFloat(),
                    onValueChange = { viewModel.effort = it.toInt() },
                    valueRange    = 1f..10f,
                    steps         = 8,
                    enabled       = !isLoading,
                    modifier      = Modifier.fillMaxWidth()
                )
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("1 — легко", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("10 — очень тяжело", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Дедлайн
            Column {
                Text("Дедлайн", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick  = { showDatePicker = true },
                        enabled  = !isLoading,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Filled.CalendarToday, null,
                            modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            viewModel.deadline
                                ?.let { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(it)) }
                                ?: "Выбрать дату"
                        )
                    }
                    if (viewModel.deadline != null) {
                        IconButton(onClick = { viewModel.deadline = null }) {
                            Icon(Icons.Filled.Close, "Убрать дедлайн",
                                tint = MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }

            // Повторение
            Column {
                Text("Повторение", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(4.dp))
                ExposedDropdownMenuBox(
                    expanded         = showRecurrenceMenu,
                    onExpandedChange = { showRecurrenceMenu = it }
                ) {
                    OutlinedTextField(
                        value         = viewModel.recurrence.toLabel(),
                        onValueChange = {},
                        readOnly      = true,
                        enabled       = !isLoading,
                        trailingIcon  = {
                            ExposedDropdownMenuDefaults.TrailingIcon(showRecurrenceMenu)
                        },
                        modifier      = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded         = showRecurrenceMenu,
                        onDismissRequest = { showRecurrenceMenu = false }
                    ) {
                        Recurrence.entries.forEach { rec ->
                            DropdownMenuItem(
                                text    = { Text(rec.toLabel()) },
                                onClick = {
                                    viewModel.recurrence  = rec
                                    showRecurrenceMenu    = false
                                }
                            )
                        }
                    }
                }
            }

            // Категория
            OutlinedTextField(
                value         = viewModel.category,
                onValueChange = { viewModel.category = it },
                label         = { Text("Категория (необязательно)") },
                placeholder   = { Text("Работа, учёба, спорт...") },
                singleLine    = true,
                enabled       = !isLoading,
                modifier      = Modifier.fillMaxWidth()
            )

            // Ошибка
            AnimatedVisibility(uiState is TaskFormUiState.Error) {
                Text(
                    (uiState as? TaskFormUiState.Error)?.message ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(4.dp))

            //Кнопка сохранить
            Button(
                onClick  = viewModel::save,
                enabled  = !isLoading && viewModel.title.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(22.dp),
                        color       = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        if (viewModel.isEditing) "Сохранить изменения" else "Создать задачу",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

private fun Recurrence.toLabel() = when (this) {
    Recurrence.NONE     -> "Не повторяется"
    Recurrence.DAILY    -> "Каждый день"
    Recurrence.WEEKLY   -> "Каждую неделю"
    Recurrence.BIWEEKLY -> "Каждые 2 недели"
}