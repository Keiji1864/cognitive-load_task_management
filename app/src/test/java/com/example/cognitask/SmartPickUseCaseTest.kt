package com.example.cognitask

import com.example.cognitask.domain.model.Recurrence
import com.example.cognitask.domain.model.Task
import com.example.cognitask.domain.usecase.smartpick.SmartPickUseCase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SmartPickUseCaseTest {

    private val useCase = SmartPickUseCase()

    private fun task(
        id: Long,
        importance: Int = 3,
        effort: Int = 3,
        deadline: Long? = null,
        recurrence: Recurrence = Recurrence.NONE,
        isCompleted: Boolean = false
    ) = Task(
        id = id,
        userId = 1L,
        title = "Задача $id",
        importance = importance,
        effort = effort,
        deadline = deadline,
        recurrence = recurrence,
        isCompleted = isCompleted
    )

    // Тест 1: выполненные задачи не попадают в рекомендации
    @Test
    fun `completed tasks are excluded from recommendations`() {
        val tasks = listOf(
            task(id = 1, importance = 5),
            task(id = 2, importance = 4, isCompleted = true),
            task(id = 3, importance = 3)
        )
        val result = useCase(tasks, energyLevel = 7)
        assertTrue(
            "Выполненная задача не должна попасть в рекомендации",
            result.none { it.id == 2L })
    }

    // Тест 2: при низком уровне сил задачи с высоким effort не рекомендуются
    @Test
    fun `high effort task not recommended when energy is low`() {
        val hardTask = task(id = 1, importance = 5, effort = 10)
        val easyTask = task(id = 2, importance = 2, effort = 1)
        val result = useCase(listOf(hardTask, easyTask), energyLevel = 2)
        // При energy=2 лёгкая задача должна быть выше тяжёлой
        val ids = result.map { it.id }
        assertTrue(
            "Лёгкая задача должна быть рекомендована при низкой энергии",
            ids.indexOf(easyTask.id) < ids.indexOf(hardTask.id) || !ids.contains(hardTask.id)
        )
    }

    // Тест 3: при энергии 1-3 рекомендуется максимум 2 задачи
    @Test
    fun `low energy returns at most 2 recommendations`() {
        val tasks = (1L..6L).map { task(id = it, importance = 3, effort = 2) }
        val result = useCase(tasks, energyLevel = 2)
        assertTrue("При низкой энергии максимум 2 задачи", result.size <= 2)
    }

    // Тест 4: пустой список -> пустой результат
    @Test
    fun `empty task list returns empty recommendations`() {
        val result = useCase(emptyList(), energyLevel = 5)
        assertEquals(emptyList<Task>(), result)
    }
}