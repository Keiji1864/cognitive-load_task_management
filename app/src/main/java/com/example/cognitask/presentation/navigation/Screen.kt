package com.example.cognitask.presentation.navigation

sealed class Screen(val route: String) {
    //Auth
    data object Login : Screen("login")
    data object Register : Screen("register")

    //Main
    data object Home : Screen("home")
    data object Profile : Screen("profile")

    //Tasks
    data object TaskList : Screen("task_list")

    data object TaskForm : Screen("task_form/{taskId}") {
        fun createRoute(taskId: Long = -1L) = "task_form/$taskId"
    }
}