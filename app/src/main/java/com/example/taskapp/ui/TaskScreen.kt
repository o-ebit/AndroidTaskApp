package com.example.taskapp.ui

import androidx.compose.material3.*
import androidx.compose.foundation.*
import android.app.Application
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskapp.viewmodel.TasksVm
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
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
    var showAdd by remember { mutableStateOf(false) }
    val headerColor by vm.headerColor.collectAsState(0xFFEEEEEE.toLong())
    var askClear by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(title) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { showAdd = true },
                            modifier = Modifier
                                .background(Color(0xFFF0F0F0), shape = MaterialTheme.shapes.small)
                                .padding(4.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Black)
                        }
                    }
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color(headerColor))
                )
            }
        }
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .fillMaxSize()) {
            val completedExists = tasks.any { it.completedDate != null && it.due != "EVERYDAY" }

            Button(
                onClick = { askClear = true },
                enabled = completedExists,
                modifier = Modifier.align(Alignment.End).padding(end = 16.dp, top = 8.dp)
            ) { Text("Clear completed") }

            if (askClear) {
                AlertDialog(
                    onDismissRequest = { askClear = false },
                    title = { Text("Remove completed tasks?") },
                    confirmButton = {
                        TextButton(onClick = {
                            vm.clearCompleted()
                            askClear = false
                        }) { Text("Delete") }
                    },
                    dismissButton = {
                        TextButton(onClick = { askClear = false }) { Text("Cancel") }
                    }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                itemsIndexed(tasks, key = { _, t -> t.id }) { index, task ->

                    /* --- per‑row state --- */
                    var ask by remember { mutableStateOf(false) }
                    var edit by remember { mutableStateOf(false) }
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
                                    onLongClick = {
                                        edit = true
                                    }
                                ),
                            elevation = CardDefaults.cardElevation(if (dismissState.targetValue != SwipeToDismissBoxValue.Settled) 4.dp else 0.dp)
                        ) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                val today = LocalDate.now().toString()
                                val doneToday = task.completedDate == today && task.due != "EVERYDAY"

                                Text(
                                    text = task.text,
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 12.dp),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = if (doneToday) Color.Gray else LocalContentColor.current,
                                        textDecoration = if (doneToday) TextDecoration.LineThrough else null
                                    )
                                )

                                val dueLabel = when (task.due) {
                                    null -> ""
                                    "EVERYDAY" -> "Every day"
                                    else -> LocalDate.parse(task.due)
                                        .format(DateTimeFormatter.ofPattern("dd MMM"))
                                }
                                if (dueLabel.isNotBlank()) {
                                Text(
                                    dueLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(end = 8.dp)
                                )}

                                /* ▲ up */
                                IconButton(
                                    onClick = { if (index > 0) vm.move(index, index - 1) },
                                    enabled = index > 0,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.KeyboardArrowUp,
                                        null,
                                        Modifier.size(18.dp)
                                    )
                                }

                                /* ▼ down */
                                IconButton(
                                    onClick = {
                                        if (index < tasks.lastIndex) vm.move(
                                            index,
                                            index + 1
                                        )
                                    },
                                    enabled = index < tasks.lastIndex,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        Icons.Default.KeyboardArrowDown,
                                        null,
                                        Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    /* ---- Edit dialog ---- */
                    if (edit) {
                        TaskEditDialog(
                            title = "Edit task",
                            initialText = task.text,
                            initialDue = task.due,
                            onSave = { t, d -> vm.rename(task, t, d); edit = false },
                            onDismiss = { edit = false }
                        )
                    }

                    /* ---- Y/N dialog after swipe ---- */
                    if (ask) {
                        AlertDialog(
                            onDismissRequest = {
                                ask = false
                                scope.launch { dismissState.reset() }
                            },
                            title = { Text("Delete task?") },
                            text = { Text(task.text) },
                            confirmButton = {
                                TextButton(onClick = {
                                    vm.delete(task)
                                    ask = false
                                    scope.launch { dismissState.reset() } // ← add this
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
    if (showAdd) {
        TaskEditDialog(
            title = "New task",
            initialText = "",
            initialDue = null,
            onSave = { t, d -> vm.add(t, d); showAdd = false },
            onDismiss = { showAdd = false }
        )
    }
}

@Composable
fun CompactChip(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp,
        modifier = modifier
            .padding(end = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskEditDialog(
    title: String,
    initialText: String,
    initialDue: String?,
    onSave: (String, String?) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(initialText) }
    var due by remember { mutableStateOf(initialDue) }
    var pickDate by remember { mutableStateOf(false) }

    if (pickDate) {
        val pickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { pickDate = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = pickerState.selectedDateMillis
                        if (millis != null) {
                            due = LocalDate.ofEpochDay(millis / 86_400_000)
                                .toString() // formatted as yyyy-MM-dd
                        }
                        pickDate = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { pickDate = false }) { Text("Cancel") }
            },
            content = {
                DatePicker(state = pickerState)
            }
        )
    }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Task") },
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                Row {
                    CompactChip(text = "None",    onClick = { due = null })
                    CompactChip(text = "Every day", onClick = { due = "EVERYDAY" })
                    CompactChip(text = "Today",   onClick = { due = LocalDate.now().toString() })
                    CompactChip(text = "Date…",   onClick = { pickDate = true })
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    when (due) {
                        null -> "No due date"
                        "EVERYDAY" -> "Every day"
                        else -> LocalDate.parse(due)
                            .format(DateTimeFormatter.ofPattern("dd MMM"))
                    },
                    style = MaterialTheme.typography.labelSmall
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (text.isNotBlank()) onSave(text, due) }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}