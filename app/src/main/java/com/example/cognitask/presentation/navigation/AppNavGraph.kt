package com.example.cognitask.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode.Companion.Screen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cognitask.presentation.ui.auth.AuthViewModel
import com.example.cognitask.presentation.ui.auth.LoginScreen
import com.example.cognitask.presentation.ui.auth.RegisterScreen
import com.example.cognitask.presentation.ui.home.HomeScreen

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val sessionUserId by authViewModel.sessionUserId.collectAsStateWithLifecycle()

    if (sessionUserId == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val startDestination = if (sessionUserId!! > 0L) Screen.Home.route else Screen.Login.route

    NavHost(
        navController    = navController,
        startDestination = startDestination
    ) {

        //Логин
        composable(Screen.Login.route) {
            val vm: AuthViewModel = hiltViewModel()
            val state by vm.authState.collectAsStateWithLifecycle()

            LoginScreen(
                uiState             = state,
                onLogin             = vm::login,
                onLoginSuccess      = {
                    vm.resetAuthState()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        //Регистрация
        composable(Screen.Register.route) {
            val vm: AuthViewModel = hiltViewModel()
            val state by vm.authState.collectAsStateWithLifecycle()

            RegisterScreen(
                uiState           = state,
                onRegister        = vm::register,
                onRegisterSuccess = {
                    vm.resetAuthState()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        //Главный экран
        composable(Screen.Home.route) {
            val vm: AuthViewModel = hiltViewModel()

            HomeScreen(
                onLogout = {
                    vm.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        //Форма задачи (taskId = -1 создание, иначе редактирование)
        composable(
            route     = Screen.TaskForm.route,
            arguments = listOf(
                navArgument("taskId") {
                    type         = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) {
            // Будет реализован
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                androidx.compose.material3.Text("TaskFormScreen, День 3")
            }
        }
    }
}