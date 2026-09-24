package com.cat.androidcat.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cat.androidcat.ui.screens.admin.AdminAIGeneratorScreen
import com.cat.androidcat.ui.screens.admin.AdminDashboardScreen
import com.cat.androidcat.ui.screens.admin.AdminQuestionsScreen
import com.cat.androidcat.ui.screens.auth.LoginScreen
import com.cat.androidcat.ui.screens.auth.RegisterScreen
import com.cat.androidcat.ui.screens.user.*
import com.cat.androidcat.viewmodel.AdminViewModel
import com.cat.androidcat.viewmodel.AuthViewModel
import com.cat.androidcat.viewmodel.ExamViewModel
import com.cat.androidcat.viewmodel.MaterialViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    examViewModel: ExamViewModel = viewModel(),
    materialViewModel: MaterialViewModel = viewModel(),
    adminViewModel: AdminViewModel = viewModel()
) {
    val startDestination = if (authViewModel.isLoggedIn) {
        if (authViewModel.isAdmin) Screen.AdminDashboard.route else Screen.UserHome.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = { isAdmin ->
                    val destination = if (isAdmin) Screen.AdminDashboard.route else Screen.UserHome.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = { navController.popBackStack() },
                onRegisterSuccess = { isAdmin ->
                    val destination = if (isAdmin) Screen.AdminDashboard.route else Screen.UserHome.route
                    navController.navigate(destination) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        // User
        composable(Screen.UserHome.route) {
            HomeScreen(
                authViewModel = authViewModel,
                examViewModel = examViewModel,
                onStartExam = { examId ->
                    navController.navigate(Screen.Exam.createRoute(examId, "EXAM"))
                },
                onStartStudy = { examId ->
                    navController.navigate(Screen.Exam.createRoute(examId, "STUDY"))
                },
                onNavigateToMaterials = { navController.navigate(Screen.Materials.route) },
                onNavigateToAdmin = { navController.navigate(Screen.AdminDashboard.route) },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Exam.route,
            arguments = listOf(
                navArgument("examId") { type = NavType.StringType },
                navArgument("mode") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "EXAM"
            ExamScreen(
                viewModel = examViewModel,
                mode = mode,
                onFinishExam = { examId ->
                    navController.navigate(Screen.Result.createRoute(examId)) {
                        popUpTo(Screen.UserHome.route)
                    }
                },
                onBackToHome = {
                    navController.navigate(Screen.UserHome.route) {
                        popUpTo(Screen.UserHome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Result.route,
            arguments = listOf(navArgument("examId") { type = NavType.StringType })
        ) {
            ResultScreen(
                viewModel = examViewModel,
                onBackToHome = {
                    navController.navigate(Screen.UserHome.route) {
                        popUpTo(Screen.UserHome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Materials.route) {
            MaterialsScreen(
                viewModel = materialViewModel,
                onNavigateToDetail = { materialId ->
                    navController.navigate(Screen.MaterialDetail.createRoute(materialId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.MaterialDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: ""
            MaterialDetailScreen(
                materialId = id,
                viewModel = materialViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // Admin
        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                viewModel = adminViewModel,
                onNavigateToQuestions = { navController.navigate(Screen.AdminQuestions.route) },
                onNavigateToAiGenerate = { navController.navigate(Screen.AdminAIGenerate.route) },
                onNavigateToMaterials = { navController.navigate(Screen.Materials.route) },
                onNavigateToUserMode = { navController.navigate(Screen.UserHome.route) },
                onBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(Screen.UserHome.route)
                    }
                }
            )
        }

        composable(Screen.AdminQuestions.route) {
            AdminQuestionsScreen(
                viewModel = adminViewModel,
                onNavigateToAiGenerate = { navController.navigate(Screen.AdminAIGenerate.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.AdminAIGenerate.route) {
            AdminAIGeneratorScreen(
                viewModel = adminViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
