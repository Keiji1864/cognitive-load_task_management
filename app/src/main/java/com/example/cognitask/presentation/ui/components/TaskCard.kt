package com.example.cognitask.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.CalendarToday
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.cognitask.domain.model.Recurrence
import com.example.cognitask.domain.model.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun TaskCard(
    task: Task,
    onToggleComplete: (Task) -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (Long) -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isInPlan: Boolean = false,
    onClick: () -> Unit = { onEdit(task.id) },
    onLongClick: () -> Unit = {}
) {
    val cardColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.primaryContainer
            task.isCompleted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else -> MaterialTheme.colorScheme.surface
        },
        label = "cardColor"
    )
    val scale by animateFloatAsState(
        targetValue = if (task.isCompleted) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "checkScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = if (!isInPlan) onLongClick else null
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = if (isSelected)
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        else null,
        elevation = if (isSelected)
            CardDefaults.cardElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp,
                focusedElevation = 0.dp,
                hoveredElevation = 0.dp,
                draggedElevation = 0.dp,
                disabledElevation = 0.dp
            )
        else
            CardDefaults.cardElevation(
                defaultElevation = if (task.isCompleted || isInPlan) 0.dp else 2.dp
            )
    ) {
        Row(
            modifier = Modifier.padding(start = 4.dp, end = 0.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.Top
        ) {
            IconButton(
                onClick = { onToggleComplete(task) },
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.CenterVertically)
                    .graphicsLayer { scaleX = scale; scaleY = scale }
            ) {
                Icon(
                    imageVector = if (task.isCompleted)
                        Icons.Filled.CheckCircle else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = "Выполнено",
                    tint = if (task.isCompleted) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleSmall,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough
                        else TextDecoration.None,
                        modifier = Modifier.weight(1f)
                    )
                    if (isInPlan) {
                        Icon(
                            Icons.Filled.CalendarMonth, "В плане дня",
                            modifier = Modifier
                                .size(14.dp)
                                .padding(start = 4.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (isSelected) {
                        Icon(
                            Icons.Filled.CheckCircle, "Выбрано",
                            modifier = Modifier
                                .size(14.dp)
                                .padding(start = 4.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                Spacer(Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    InfoPill(
                        Icons.Filled.Star,
                        "${task.importance}",
                        importanceColor(task.importance)
                    )
                    InfoPill(Icons.Filled.Bolt, "${task.effort}/10")
                    task.deadline?.let {
                        InfoPill(
                            Icons.Outlined.CalendarToday,
                            SimpleDateFormat("dd.MM.yy", Locale.getDefault()).format(Date(it))
                        )
                    }
                    if (task.recurrence != Recurrence.NONE) {
                        InfoPill(
                            Icons.Filled.Repeat,
                            when (task.recurrence) {
                                Recurrence.DAILY -> "Ежедн."
                                Recurrence.WEEKLY -> "Еженед."
                                Recurrence.BIWEEKLY -> "2 нед."
                                else -> ""
                            }
                        )
                    }
                }
            }

            Column(
                modifier = Modifier.padding(end = 2.dp, top = 2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                IconButton(
                    onClick = { onEdit(task.id) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Filled.Edit, "Редактировать",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                IconButton(
                    onClick = { onDelete(task.id) },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Filled.Delete, "Удалить",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
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
    icon: ImageVector,
    text: String,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    if (text.isBlank()) return
    Surface(shape = RoundedCornerShape(50), color = color.copy(alpha = 0.12f)) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(icon, null, modifier = Modifier.size(10.dp), tint = color)
            Text(text, style = MaterialTheme.typography.labelSmall, color = color)
        }
    }
}