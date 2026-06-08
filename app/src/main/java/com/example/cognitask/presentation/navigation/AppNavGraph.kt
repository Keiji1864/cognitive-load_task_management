package com.example.cognitask.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cognitask.presentation.ui.auth.AuthViewModel
import com.example.cognitask.presentation.ui.auth.LoginScreen
import com.example.cognitask.presentation.ui.auth.RegisterScreen
import com.example.cognitask.presentation.ui.components.BottomNavBar
import com.example.cognitask.presentation.ui.home.HomeScreen
import com.example.cognitask.presentation.ui.profile.ProfileScreen
import com.example.cognitask.presentation.ui.tasks.TaskFormScreen
import com.example.cognitask.presentation.ui.tasks.TaskListScreen

private val BOTTOM_BAR_ROUTES = setOf(
    Screen.Home.route,
    Screen.TaskList.route,
    Screen.Profile.route
)

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val sessionUserId by authViewModel.sessionUserId.collectAsStateWithLifecycle()

    if (sessionUserId == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val startDestination = if (sessionUserId!! > 0L) Screen.Home.route else Screen.Login.route

    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (currentRoute in BOTTOM_BAR_ROUTES) {
                BottomNavBar(navController)
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Screen.Login.route) {
                val vm: AuthViewModel = hiltViewModel()
                val state by vm.authState.collectAsStateWithLifecycle()
                LoginScreen(
                    uiState = state,
                    onLogin = vm::login,
                    onLoginSuccess = {
                        vm.resetAuthState()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) }
                )
            }

            composable(Screen.Register.route) {
                val vm: AuthViewModel = hiltViewModel()
                val state by vm.authState.collectAsStateWithLifecycle()
                RegisterScreen(
                    uiState = state,
                    onRegister = vm::register,
                    onRegisterSuccess = {
                        vm.resetAuthState()
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToTasks = {
                        navController.navigate(Screen.TaskList.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToForm = { taskId ->
                        navController.navigate(Screen.TaskForm.createRoute(taskId))
                    }
                )
            }

            composable(Screen.TaskList.route) {
                TaskListScreen(
                    onNavigateToForm = { taskId ->
                        navController.navigate(Screen.TaskForm.createRoute(taskId))
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onLogout = { logoutAndGoToLogin(authViewModel, navController) }
                )
            }

            composable(
                route = Screen.TaskForm.route,
                arguments = listOf(
                    navArgument("taskId") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) {
                TaskFormScreen(onNavigateBack = { navController.popBackStack() })
            }
        }
    }
}

private fun logoutAndGoToLogin(
    authViewModel: AuthViewModel,
    navController: NavHostController
) {
    authViewModel.logout()
    navController.navigate(Screen.Login.route) {
        popUpTo(0) { inclusive = true }
    }
}