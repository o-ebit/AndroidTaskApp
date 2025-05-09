package com.example.taskapp.ui

import android.app.Application
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.taskapp.data.Category
import com.example.taskapp.data.Recurrence
import com.example.taskapp.data.Task
import com.example.taskapp.data.recurrenceLabel
import com.example.taskapp.viewmodel.CategoriesVm
import com.example.taskapp.viewmodel.UnifiedTaskViewModel
import kotlinx.coroutines.launch
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TasksScreen(
    categoryId: Int,
    onBack: () -> Unit
) {
    /* --- ViewModel factory --- */
    val app = LocalContext.current.applicationContext as Application
    val factory = remember(categoryId) {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val handle = SavedStateHandle(mapOf("categoryId" to categoryId))
                return UnifiedTaskViewModel(app, handle) as T
            }
        }
    }
    val vm: UnifiedTaskViewModel = viewModel(factory = factory)
    val categoriesVm: CategoriesVm = viewModel()
    val allCategories by categoriesVm.categories.collectAsState()
    if (allCategories.isEmpty()) return
    val currentCategory: Category = allCategories.firstOrNull { it.id == categoryId }
        ?: error("No categories available to default to.")
    /* --- state --- */
    val tasks by vm.tasksForCategory.collectAsState()
    val title by vm.title.collectAsState("")
    var showAdd by remember { mutableStateOf(false) }
    val headerColor by vm.headerColor.collectAsState(0xFFEEEEEE)
    var askClear by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    val reorderState = rememberReorderableLazyListState(
        onMove = { from, to -> vm.moveInCategoryPage(tasks, from.index, to.index) }


    )
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(title) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFEEEEEE),
                        navigationIconContentColor = Color.Black,
                        titleContentColor = Color.Black
                    ),
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                    },
                    actions = {
                        Button(
                            onClick = { showAdd = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFCCCCCC) // darker than header
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp)
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
                .fillMaxSize()
        ) {
            val completedExists =
                tasks.any { it.completedDate != null && it.recurrence == Recurrence.NONE }

            Button(
                onClick = { askClear = true },
                enabled = completedExists,
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(end = 16.dp, top = 8.dp)
            ) { Text("Clear completed") }

            if (askClear) {
                AlertDialog(
                    onDismissRequest = { askClear = false },
                    title = { Text("Remove completed tasks?") },
                    confirmButton = {
                        TextButton(onClick = {
                            vm.clearCompleted(categoryId)
                            askClear = false
                        }) { Text("Delete") }
                    },
                    dismissButton = {
                        TextButton(onClick = { askClear = false }) { Text("Cancel") }
                    }
                )
            }

            LazyColumn(
                state = reorderState.listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .reorderable(reorderState)
            ) {
                itemsIndexed(tasks, key = { _, task -> task.id }) { _, task ->
                    var ask by remember { mutableStateOf(false) }
                    val scope = rememberCoroutineScope()

                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = {
                            if (it == SwipeToDismissBoxValue.EndToStart) {
                                ask = true
                                false
                            } else false
                        }
                    )

                    ReorderableItem(reorderState, key = task.id) { isDragging ->
                        val elevation = if (isDragging) 4.dp else 0.dp

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
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                                    .combinedClickable(
                                        onClick = {},
                                        onLongClick = { editingTask = task }
                                    ),
                                elevation = CardDefaults.cardElevation(elevation)
                            ) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val today = LocalDate.now().toString()
                                    val doneToday =
                                        task.completedDate != null && task.completedDate >= today

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

                                    val dueLabel = recurrenceLabel(task.due, task.recurrence)

                                    if (dueLabel.isNotBlank()) {
                                        Text(
                                            dueLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )
                                    }

                                    Icon(
                                        Icons.Default.Menu,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(start = 8.dp)
                                            .detectReorder(reorderState)
                                    )
                                }
                            }
                        }
                    }

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
                                    scope.launch { dismissState.reset() }
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
            if (editingTask != null) {
                TaskEditDialog(
                    initialText = editingTask!!.text,
                    initialDue = editingTask!!.due,
                    initialRecurrence = editingTask!!.recurrence,
                    initialCategory = allCategories.firstOrNull { it.id == editingTask!!.categoryId }
                        ?: allCategories.first(),
                    allCategories = allCategories,
                    onSave = { t, d, rec, cId ->
                        vm.updateValues(editingTask!!, t, d, rec, cId)
                        editingTask = null
                    },
                    onDismiss = { editingTask = null }
                )
            }

        }
    }
    if (showAdd) {
        TaskEditDialog(
            initialText = "",
            initialDue = null,
            initialRecurrence = Recurrence.NONE,
            initialCategory = currentCategory,
            allCategories = allCategories,
            onSave = { text, due, rec, catId ->
                vm.add(text, due, rec, catId)
                showAdd = false
            },
            onDismiss = { showAdd = false }
        )
    }
}
