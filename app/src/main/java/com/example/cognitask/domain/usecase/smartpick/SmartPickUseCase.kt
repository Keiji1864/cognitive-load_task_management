package com.example.cognitask.domain.usecase.smartpick

import com.example.cognitask.domain.model.Recurrence
import com.example.cognitask.domain.model.Task
import java.util.Calendar
import javax.inject.Inject

class SmartPickUseCase @Inject constructor() {

    operator fun invoke(tasks: List<Task>, energyLevel: Int): List<Task> {
        val now = System.currentTimeMillis()
        val today = Calendar.getInstance()

        val active = tasks.filter { !it.isCompleted }
        if (active.isEmpty()) return emptyList()

        val dayBudget = energyLevel * 5 / 2
        val maxTasks = pickCount(energyLevel)

        val scored = active
            .map { it to valueScore(it, now, today) }
            .sortedByDescending { (_, s) -> s }

        val selected = mutableListOf<Task>()
        var budgetLeft = dayBudget

        for ((task, _) in scored) {
            if (selected.size >= maxTasks) break
            if (task.effort <= budgetLeft) {
                selected.add(task)
                budgetLeft -= task.effort
            }
        }

        if (selected.isEmpty()) {
            scored
                .firstOrNull { (t, _) -> t.effort <= dayBudget }
                ?.let { (t, _) -> selected.add(t) }
        }

        if (selected.size < 2 && budgetLeft > 0) {
            scored
                .filter { (t, _) -> t !in selected && t.effort <= budgetLeft }
                .maxByOrNull { (_, s) -> s }
                ?.let { (t, _) ->
                    selected.add(t)
                }
        }

        return selected
    }

    private fun pickCount(energy: Int) = when {
        energy <= 3 -> 2
        energy <= 7 -> 3
        else -> 4
    }

    private fun valueScore(task: Task, nowMs: Long, today: Calendar): Float =
        importanceScore(task.importance) * W_IMPORTANCE +
                deadlineScore(task.deadline, nowMs) * W_DEADLINE +
                recurrenceScore(task, today) * W_RECURRENCE

    private fun importanceScore(importance: Int) = (importance - 1) / 4f

    private fun deadlineScore(deadline: Long?, nowMs: Long): Float {
        deadline ?: return 0f
        val days = (deadline - nowMs) / MS_DAY
        return when {
            days < 0 -> 1.00f
            days == 0L -> 0.95f
            days == 1L -> 0.85f
            days <= 3 -> 0.70f
            days <= 7 -> 0.50f
            days <= 14 -> 0.30f
            else -> 0.10f
        }
    }

    private fun recurrenceScore(task: Task, today: Calendar): Float = when (task.recurrence) {
        Recurrence.NONE -> 0f
        Recurrence.DAILY -> 1f
        Recurrence.WEEKLY -> {
            val created = Calendar.getInstance().apply { timeInMillis = task.createdAt }
            if (created.get(Calendar.DAY_OF_WEEK) == today.get(Calendar.DAY_OF_WEEK)) 1f else 0.2f
        }

        Recurrence.BIWEEKLY -> {
            val daysSince = (today.timeInMillis - task.createdAt) / MS_DAY
            if (daysSince % 14 < 2) 1f else 0.1f
        }
    }

    companion object {
        private const val W_IMPORTANCE = 0.55f
        private const val W_DEADLINE = 0.35f
        private const val W_RECURRENCE = 0.10f
        private const val MS_DAY = 86_400_000L
    }
}