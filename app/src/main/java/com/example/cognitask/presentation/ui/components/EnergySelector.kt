package com.example.cognitask.presentation.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EnergySelector(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Уровень сил сегодня",
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    energyLabel(value),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(energyEmoji(value), fontSize = 28.sp)
        }

        Spacer(Modifier.height(10.dp))

        // 10 делений
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            (1..10).forEach { level ->
                val filled    = level <= value
                val isSelected = level == value

                val targetColor = when {
                    !filled    -> Color.Transparent
                    level <= 3 -> Color(0xFFEF5350)   // красный  - мало сил
                    level <= 6 -> Color(0xFFFFA726)   // желтый - средне
                    else       -> Color(0xFF66BB6A)   // зелёный  - много сил
                }
                val bgColor by animateColorAsState(
                    targetValue = targetColor,
                    animationSpec = tween(200),
                    label = "energy_$level"
                )
                val borderColor = MaterialTheme.colorScheme.outlineVariant

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(if (isSelected) 38.dp else 30.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (filled) bgColor
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable { onValueChange(level) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Text(
                            text       = level.toString(),
                            style      = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White
                        )
                    }
                }
            }
        }

        // Подписи
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("1 — нет сил",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("10 — полон сил",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private fun energyEmoji(v: Int) = when {
    v <= 2  -> "😴"
    v <= 4  -> "😐"
    v <= 6  -> "🙂"
    v <= 8  -> "😊"
    else    -> "⚡"
}

private fun energyLabel(v: Int) = when {
    v <= 2  -> "Совсем нет сил"
    v <= 4  -> "Немного устал"
    v <= 6  -> "Нормальный день"
    v <= 8  -> "Хорошая форма"
    else    -> "Готов горы свернуть"
}