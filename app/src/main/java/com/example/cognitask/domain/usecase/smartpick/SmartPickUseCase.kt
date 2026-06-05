package com.example.cognitask.domain.usecase.smartpick

import com.example.cognitask.domain.model.Recurrence
import com.example.cognitask.domain.model.Task
import java.util.Calendar
import javax.inject.Inject

class SmartPickUseCase @Inject constructor() {

    operator fun invoke(
        tasks: List<Task>,
        energyLevel: Int        // 1–10
    ): List<Task> {
        val now   = System.currentTimeMillis()
        val today = Calendar.getInstance()

        return tasks
            .filter { !it.isCompleted }
            .map    { task -> task to score(task, energyLevel, now, today) }
            .sortedByDescending { (_, s) -> s }
            .take(pickCount(energyLevel))
            .map { (task, _) -> task }
    }

    // Сколько задач рекомендовать в зависимости от сил
    private fun pickCount(energy: Int) = when {
        energy <= 3 -> 2
        energy <= 7 -> 3
        else        -> 4
    }

    private fun score(task: Task, energy: Int, nowMs: Long, today: Calendar): Float =
        importanceScore(task.importance)      * W_IMPORTANCE  +
                deadlineScore(task.deadline, nowMs)   * W_DEADLINE    +
                energyMatchScore(task.effort, energy) * W_ENERGY      +
                recurrenceScore(task, today)          * W_RECURRENCE

    // Компоненты оценки

    // importance 1-5 → 0.0-1.0
    private fun importanceScore(importance: Int) = (importance - 1) / 4f

    // Чем ближе дедлайн, тем выше оценка; без дедлайна 0
    private fun deadlineScore(deadline: Long?, nowMs: Long): Float {
        deadline ?: return 0f
        val days = (deadline - nowMs) / MS_DAY
        return when {
            days < 0   -> 1.00f    // просрочено
            days == 0L -> 0.95f    // сегодня
            days == 1L -> 0.85f    // завтра
            days <= 3  -> 0.70f
            days <= 7  -> 0.50f
            days <= 14 -> 0.30f
            else       -> 0.10f
        }
    }

    // Если усилий нужно меньше или столько же, сколько есть сил, то задача подходит
    // Чем сильнее задача превышает уровень сил, тем больше штраф
    private fun energyMatchScore(effort: Int, energy: Int): Float =
        if (effort <= energy) {
            1f - ((energy - effort) / 20f).coerceIn(0f, 0.25f)
        } else {
            maxOf(0f, 1f - (effort - energy) / 5f)
        }

    private fun recurrenceScore(task: Task, today: Calendar): Float = when (task.recurrence) {
        Recurrence.NONE     -> 0f
        Recurrence.DAILY    -> 1f
        Recurrence.WEEKLY   -> {
            val created = Calendar.getInstance().apply { timeInMillis = task.createdAt }
            if (created.get(Calendar.DAY_OF_WEEK) == today.get(Calendar.DAY_OF_WEEK)) 1f else 0.2f
        }
        Recurrence.BIWEEKLY -> {
            val daysSince = (today.timeInMillis - task.createdAt) / MS_DAY
            if (daysSince % 14 < 2) 1f else 0.1f
        }
    }

    companion object {
        private const val W_IMPORTANCE  = 0.35f
        private const val W_DEADLINE    = 0.30f
        private const val W_ENERGY      = 0.25f
        private const val W_RECURRENCE  = 0.10f
        private const val MS_DAY        = 86_400_000L
    }
}