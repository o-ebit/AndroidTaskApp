package com.example.taskapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.taskapp.data.Checklist
import com.example.taskapp.viewmodel.ListsVm
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen(
    nav: NavHostController,
    vm: ListsVm = viewModel()
) {
    val lists by vm.lists.collectAsState()
    var newTitle by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checklists") },
                actions = {
                    IconButton(onClick = { nav.navigate("todos") }) {
                        Icon(Icons.Default.List, contentDescription = "To-dos")
                    }
                    IconButton(onClick = {
                        if (newTitle.isNotBlank()) {
                            vm.add(newTitle)
                            newTitle = ""
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Add checklist")
                    }
                }
            )
        }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {

            OutlinedTextField(
                value = newTitle,
                onValueChange = { newTitle = it },
                placeholder = { Text("New checklist") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(lists, key = Checklist::id) { list ->

                    var confirmDelete by remember { mutableStateOf(false) }
                    var edit by remember { mutableStateOf(false) }
                    var draft by remember { mutableStateOf(list.title) }
                    val scope = rememberCoroutineScope()

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart) {
                                confirmDelete = true
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
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                    .background(Color(0xFFEEEEEE)),
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
                        ListItem(
                            headlineContent = { Text(list.title) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = { nav.navigate("list/${list.id}") },
                                    onLongClick = {
                                        draft = list.title
                                        edit = true
                                    }
                                )
                        )
                    }

                    HorizontalDivider(thickness = 0.5.dp)

                    /* -- delete dialog -- */
                    if (confirmDelete) {
                        AlertDialog(
                            onDismissRequest = {
                                confirmDelete = false
                                scope.launch { dismissState.reset() }
                            },
                            title = { Text("Delete checklist?") },
                            text = { Text(list.title) },
                            confirmButton = {
                                TextButton(onClick = {
                                    vm.delete(list)
                                    confirmDelete = false
                                }) { Text("Delete") }
                            },
                            dismissButton = {
                                TextButton(onClick = {
                                    confirmDelete = false
                                    scope.launch { dismissState.reset() }
                                }) { Text("Cancel") }
                            }
                        )
                    }

                    /* -- rename dialog -- */
                    if (edit) {
                        AlertDialog(
                            onDismissRequest = { edit = false },
                            title = { Text("Rename checklist") },
                            text = {
                                OutlinedTextField(
                                    value = draft,
                                    onValueChange = { draft = it },
                                    singleLine = true
                                )
                            },
                            confirmButton = {
                                TextButton(onClick = {
                                    if (draft.isNotBlank()) vm.rename(list, draft)
                                    edit = false
                                }) { Text("Save") }
                            },
                            dismissButton = {
                                TextButton(onClick = { edit = false }) { Text("Cancel") }
                            }
                        )
                    }
                }
            }
        }
    }
}
