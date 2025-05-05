package com.example.taskapp.ui

import android.app.Application
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskapp.viewmodel.TasksVm
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    listId: Int,
    onBack: () -> Unit
) {
    /* --- ViewModel factory --- */
    val app = LocalContext.current.applicationContext as Application
    val factory = remember(listId) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val handle = SavedStateHandle(mapOf("listId" to listId))
                return TasksVm(app, handle) as T
            }
        }
    }
    val vm: TasksVm = viewModel(factory = factory)

    /* --- state --- */
    val tasks by vm.tasks.collectAsState()
    val title by vm.title.collectAsState("")
    var newText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (newText.isNotBlank()) {
                                vm.add(newText)
                                newText = ""
                            }
                        }
                    ) { Icon(Icons.Default.Add, null) }
                }
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

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                itemsIndexed(tasks, key = { _, t -> t.id }) { index, task ->

                    /* --- per‑row state --- */
                    var ask by remember { mutableStateOf(false) }
                    var edit by remember { mutableStateOf(false) }
                    var draft by remember { mutableStateOf(task.text) }
                    val scope = rememberCoroutineScope()

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                ask = true
                                false
                            } else false
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp, vertical = 2.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier
                                        .padding(end = 24.dp)
                                        .size(24.dp)
                                )
                            }
                        }
                    ) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                .combinedClickable(
                                    onClick = {},          // no-op tap
                                    onLongClick = { edit = true }
                                ),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = task.done,
                                    onCheckedChange = { vm.toggle(task) },
                                    modifier = Modifier.size(20.dp)
                                )

                                Text(
                                    task.text,
                                    Modifier
                                        .weight(1f)
                                        .padding(start = 12.dp),
                                    color = if (task.done) Color.Gray else LocalContentColor.current,
                                    textDecoration = if (task.done) TextDecoration.LineThrough else null
                                )

                                /* ▲ up */
                                IconButton(
                                    onClick = { if (index > 0) vm.move(index, index - 1) },
                                    enabled = index > 0,
                                    modifier = Modifier.size(32.dp)
                                ) { Icon(Icons.Default.KeyboardArrowUp, null, Modifier.size(18.dp)) }

                                /* ▼ down */
                                IconButton(
                                    onClick = { if (index < tasks.lastIndex) vm.move(index, index + 1) },
                                    enabled = index < tasks.lastIndex,
                                    modifier = Modifier.size(32.dp)
                                ) { Icon(Icons.Default.KeyboardArrowDown, null, Modifier.size(18.dp)) }
                            }
                        }
                    }

                    /* ---- Edit dialog ---- */
                    if (edit) {
                        AlertDialog(
                            onDismissRequest = { edit = false },
                            title = { Text("Edit item") },
                            text = {
                                OutlinedTextField(
                                    value = draft,
                                    onValueChange = { draft = it },
                                    singleLine = true
                                )
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    if (draft.isNotBlank()) vm.rename(task, draft)
                                    edit = false
                                }) { Text("Save") }
                            },
                            dismissButton = {
                                TextButton(onClick = { edit = false }) { Text("Cancel") }
                            }
                        )
                    }

                    /* ---- Y/N dialog after swipe ---- */
                    if (ask) {
                        AlertDialog(
                            onDismissRequest = {
                                ask = false
                                scope.launch { dismissState.reset() }
                            },
                            title = { Text("Delete item?") },
                            text = { Text(task.text) },
                            confirmButton = {
                                TextButton(onClick = {
                                    vm.delete(task)
                                    ask = false
                                }) { Text("Delete") }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    ask = false
                                    scope.launch { dismissState.reset() }
                                }) { Text("Cancel") }
                            }
                        )
                    }
                }
            }
        }
    }
}
