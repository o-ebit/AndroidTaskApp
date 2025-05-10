package com.example.taskapp.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.taskapp.data.Category
import com.example.taskapp.viewmodel.CategoriesVm
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListsScreen(
    nav: NavHostController,
    vm: CategoriesVm = viewModel()
) {
    val lists by vm.lists.collectAsState()
    var showAdd by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { showAdd = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEEEEEE),
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Add category",
                    )
                }

                TextButton(
                    onClick = { nav.navigate("todos") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEEEEEE),
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "Today's tasks",
                    )
                }
            }

        }
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                items(lists, key = Category::id) { list ->

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
                            title = { Text("Delete category?") },
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
                            title = { Text("Rename category") },
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
            if (showAdd) {
                var draft by remember { mutableStateOf("") }
                AlertDialog(
                    onDismissRequest = { showAdd = false },
                    title = { Text("New category") },
                    text = {
                        OutlinedTextField(
                            value = draft,
                            onValueChange = { draft = it },
                            singleLine = true
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            if (draft.isNotBlank()) {
                                vm.add(draft)
                                showAdd = false
                            }
                        }) { Text("Add") }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAdd = false }) { Text("Cancel") }
                    }
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "v1.0.1",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }

        }
    }
}
