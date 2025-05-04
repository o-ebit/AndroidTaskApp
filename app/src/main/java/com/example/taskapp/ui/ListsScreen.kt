// ListsScreen.kt
package com.example.taskapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.taskapp.viewmodel.ListsVm
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ListsScreen(nav: NavHostController, vm: ListsVm = viewModel()) {
    val lists by vm.lists.collectAsState(initial = emptyList())
    var newTitle by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (newTitle.isNotBlank()) {
                        vm.add(newTitle)
                        newTitle = ""
                    }
                }
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            OutlinedTextField(
                value = newTitle,
                onValueChange = { newTitle = it },
                placeholder = { Text("New checklist") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            LazyColumn {
                items(lists, key = { it.id }) { list ->
                    ListItem(
                        headlineContent = { Text(list.title) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { nav.navigate("list/${list.id}") },
                        trailingContent = {
                            IconButton(onClick = { vm.delete(list) }) {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            }
                        }
                    )
                    Divider()
                }
            }
        }
    }
}
