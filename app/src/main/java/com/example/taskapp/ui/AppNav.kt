package com.example.taskapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*

@Composable
fun AppNav() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = "lists") {
        composable("todos") { TodoScreen(onBack = { nav.popBackStack() }) }
        composable("lists") {
            ListsScreen(nav)
        }
        composable("list/{listId}") { backStackEntry ->
            val listId = backStackEntry.arguments?.getString("listId")?.toIntOrNull()
            if (listId != null) {
                TasksScreen(listId = listId, onBack = { nav.popBackStack() })
            }
        }
    }
}
