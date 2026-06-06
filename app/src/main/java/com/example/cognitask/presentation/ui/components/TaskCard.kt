package com.example.cognitask.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.cognitask.domain.model.Recurrence
import com.example.cognitask.domain.model.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TaskCard(
    task: Task,
    onToggleComplete: (Task) -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val cardColor by animateColorAsState(
        targetValue = if (task.isCompleted)
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        else
            MaterialTheme.colorScheme.surface,
        label = "cardColor"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onEdit(task.id) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(if (task.isCompleted) 0.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onToggleComplete(task) }) {
                Icon(
                    imageVector = if (task.isCompleted)
                        Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = "Выполнено",
                    tint = if (task.isCompleted)
                        MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleSmall,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough
                    else TextDecoration.None,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    InfoPill(
                        text = "★ ${task.importance}",
                        color = importanceColor(task.importance)
                    )
                    InfoPill(text = "⚡ ${task.effort}/10")
                    task.deadline?.let {
                        InfoPill(
                            text = SimpleDateFormat(
                                "dd.MM.yy",
                                Locale.getDefault()
                            ).format(Date(it))
                        )
                    }
                    if (task.recurrence != Recurrence.NONE) {
                        InfoPill(
                            text = when (task.recurrence) {
                                Recurrence.DAILY -> "День"
                                Recurrence.WEEKLY -> "Нед."
                                Recurrence.BIWEEKLY -> "2 нед."
                                else -> ""
                            }
                        )
                    }
                }
            }

            IconButton(onClick = { onEdit(task.id) }) {
                Icon(
                    Icons.Filled.Edit, "Редактировать",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = { onDelete(task.id) }) {
                Icon(
                    Icons.Filled.Delete, "Удалить",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun importanceColor(importance: Int) = when (importance) {
    5 -> MaterialTheme.colorScheme.error
    4 -> MaterialTheme.colorScheme.error.copy(alpha = 0.75f)
    3 -> MaterialTheme.colorScheme.tertiary
    else -> MaterialTheme.colorScheme.outline
}

@Composable
private fun InfoPill(
    text: String,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    if (text.isBlank()) return
    Surface(
        shape = RoundedCornerShape(50),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}