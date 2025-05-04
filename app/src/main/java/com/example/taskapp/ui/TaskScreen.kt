/* TasksScreen.kt */
package com.example.taskapp.ui

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskapp.viewmodel.TasksVm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    listId: Int,
    onBack: () -> Unit,
    vm: TasksVm = viewModel(
        factory = TasksVm.Factory(
            app = LocalContext.current.applicationContext as Application,
            listId = listId
        )
    )
) {
    val tasks by vm.tasks.collectAsState(initial = emptyList())
    var newText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tasks") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (newText.isNotBlank()) {
                        vm.add(newText)
                        newText = ""
                    }
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            OutlinedTextField(
                value = newText,
                onValueChange = { newText = it },
                placeholder = { Text("New task") },
                modifier = Modifier.fillMaxWidth()
            )

            LazyColumn {
                items(tasks, key = { it.id }) { task ->
                    val style = if (task.done) {
                        LocalTextStyle.current.copy(
                            color = Color.Gray,
                            textDecoration = TextDecoration.LineThrough
                        )
                    } else LocalTextStyle.current

                    ListItem(
                        headlineContent = { Text(task.text, style = style) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { vm.toggle(task) }
                    )
                    Divider()
                }
            }
        }
    }
}
