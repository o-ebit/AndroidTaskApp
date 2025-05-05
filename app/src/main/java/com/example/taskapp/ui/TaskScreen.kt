package com.example.taskapp.ui
import androidx.compose.ui.Alignment
import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskapp.data.Task
import com.example.taskapp.viewmodel.TasksVm
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.SavedStateHandle
import androidx.compose.material3.TopAppBar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState

// TasksScreen.kt ── simplify signature; remove custom factory
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    listId: Int,
    onBack: () -> Unit
) {
    val app = LocalContext.current.applicationContext as Application

    val factory = remember(listId) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val savedStateHandle = SavedStateHandle(mapOf("listId" to listId))
                return TasksVm(app, savedStateHandle) as T
            }
        }
    }

    val vm: TasksVm = viewModel(factory = factory)

    val tasks by vm.tasks.collectAsState()
    val title  by vm.title.collectAsState("")
    var newText by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { if (newText.isNotBlank()) { vm.add(newText); newText = "" } },
                content = { Icon(Icons.Default.Add, null) }
            )
        }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            OutlinedTextField(
                value = newText,
                onValueChange = { newText = it },
                placeholder = { Text("New item") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            LazyColumn {
                items(tasks, key = Task::id) { task ->

                    var ask by remember { mutableStateOf(false) }
                    val dismissState = rememberSwipeToDismissBoxState(
                        positionalThreshold = { it / 3 },               // feel free to tweak
                        confirmValueChange = {
                            ask = true          // show confirm dialog
                            false               // keep item until user confirms
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = { /* optional coloured bg */ }
                    ) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = task.done,
                                onCheckedChange = { vm.toggle(task) }
                            )
                            Text(
                                task.text,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 12.dp),
                                color = if (task.done) Color.Gray else LocalContentColor.current,
                                textDecoration = if (task.done) TextDecoration.LineThrough else null
                            )
                            IconButton(onClick = { ask = true }) {
                                Icon(Icons.Default.Delete, null)
                            }
                        }
                    }

                    if (ask) {
                        AlertDialog(
                            onDismissRequest = { ask = false },
                            title = { Text("Delete item?") },
                            text  = { Text(task.text) },
                            confirmButton = {
                                TextButton(onClick = { vm.delete(task); ask = false }) { Text("Delete") }
                            },
                            dismissButton = {
                                TextButton(onClick = { ask = false }) { Text("Cancel") }
                            }
                        )
                    }

                    HorizontalDivider()
                }
            }
        }
    }
}

