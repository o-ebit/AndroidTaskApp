package com.example.taskapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNav() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = "categories") {
        composable("todos") { TodoScreen(onBack = { if (nav.previousBackStackEntry != null) nav.popBackStack() }) }
        composable("categories") {
            CategoriesScreen(nav)
        }
        composable("category/{categoryId}") { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getString("categoryId")?.toIntOrNull()
            if (categoryId != null) {
                TasksScreen(
                    categoryId = categoryId,
                    onBack = { if (nav.previousBackStackEntry != null) nav.popBackStack() })
            }
        }
    }
}
