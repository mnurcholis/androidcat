package com.cat.androidcat.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")

    // User screens
    object UserHome : Screen("user_home")
    object Exam : Screen("exam/{examId}/{mode}") {
        fun createRoute(examId: String, mode: String = "EXAM") = "exam/$examId/$mode"
    }
    object Result : Screen("result/{examId}") {
        fun createRoute(examId: String) = "result/$examId"
    }
    object Materials : Screen("materials")
    object MaterialDetail : Screen("material_detail/{id}") {
        fun createRoute(id: String) = "material_detail/$id"
    }

    // Admin screens
    object AdminDashboard : Screen("admin_dashboard")
    object AdminQuestions : Screen("admin_questions")
    object AdminAIGenerate : Screen("admin_ai_generate")
    object AdminMaterials : Screen("admin_materials")
}
