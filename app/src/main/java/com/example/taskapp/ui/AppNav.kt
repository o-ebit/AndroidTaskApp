package com.example.taskapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.taskapp.ui.ListsScreen

@Composable
fun AppNav() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = "lists") {
        composable("lists") {
            ListsScreen(nav)
        }
        composable(
            route = "list/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: return@composable
            TasksScreen(listId = id, onBack = { nav.popBackStack() })
        }
    }
}
