package com.example.cognitask.presentation.ui.theme

import androidx.compose.ui.graphics.Color

val EnergyColors = listOf(
    Color(0xFFBD7285),
    Color(0xFFBD8A72),
    Color(0xFFBDA55A),
    Color(0xFFA0B85C),
    Color(0xFF72B86E),
    Color(0xFF52B892),
    Color(0xFF48AABC),
    Color(0xFF5892C8),
    Color(0xFF6878C8),
    Color(0xFF7060C8),
)

fun energyColor(level: Int): Color =
    EnergyColors.getOrElse(level - 1) { EnergyColors.last() }

val ImportanceLevel1Color = Color(0xFF90909A)   // outline-like
val ImportanceLevel2Color = Color(0xFF6F528A)   // tertiary
val ImportanceLevel3Color = Color(0xFF515B92)   // secondary
val ImportanceLevel4Color = Color(0xFF394379)   // primaryContainer
val ImportanceLevel5Color = Color(0xFF8C4A60)   // error

fun importanceLevelColor(importance: Int): Color = when (importance) {
    1 -> ImportanceLevel1Color
    2 -> ImportanceLevel2Color
    3 -> ImportanceLevel3Color
    4 -> ImportanceLevel4Color
    5 -> ImportanceLevel5Color
    else -> ImportanceLevel1Color
}