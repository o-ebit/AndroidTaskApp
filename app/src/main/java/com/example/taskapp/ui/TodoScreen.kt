package com.example.taskapp.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskapp.data.Recurrence
import com.example.taskapp.data.recurrenceLabel
import com.example.taskapp.viewmodel.CategoriesVm
import com.example.taskapp.viewmodel.UnifiedTaskViewModel
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorder
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable
import java.time.LocalDate

@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class
)
@Composable
fun TodoScreen(
    onBack: () -> Unit,
    vm: UnifiedTaskViewModel = viewModel()
) {
    var dayOffset by rememberSaveable { mutableStateOf(0) }
    val dateStr = LocalDate.now().plusDays(dayOffset.toLong()).toString()
    val isToday = dayOffset == 0
    val categoriesVm: CategoriesVm = viewModel()
    val allCategories by categoriesVm.lists.collectAsState()
    val firstCategory = allCategories.firstOrNull()
    var showAdd by remember { mutableStateOf(false) }
    /* stream now depends on dateStr */
    val showOutstanding by vm.showOutstanding.collectAsState()
    val items by vm.dueOnDate(dateStr).collectAsState(emptyList())

    // And add this function to your ViewModel class (TodosVm):
    // Set title color based on whether viewing today or another day
    val titleColor = if (isToday) Color.Black else Color(0xFF1976D2) // Blue for non-today
    val reorderState = rememberReorderableLazyListState(
        onMove = { from, to -> vm.moveInToDo(items.map { it.task }, from.index, to.index) }
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    AnimatedContent(
                        targetState = dayOffset,
                        transitionSpec = {
                            // swipe-up → new page slides up, swipe-down slides down
                            if (targetState > initialState)
                                slideInVertically { it } togetherWith slideOutVertically { -it }
                            else
                                slideInVertically { -it } togetherWith slideOutVertically { it }
                        }
                    ) { offset ->
                        Text(
                            when (offset) {
                                0 -> "Today"
                                1 -> "Tomorrow"
                                -1 -> "Yesterday"
                                else -> "Today ${if (offset > 0) "+" else ""}$offset"
                            },
                            color = titleColor
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Row(
                        modifier = Modifier.padding(end = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { vm.toggleShow(true) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (showOutstanding)
                                    MaterialTheme.colorScheme.primary else Color.LightGray
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("Outstanding")
                        }

                        Button(
                            onClick = { vm.toggleShow(false) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!showOutstanding)
                                    MaterialTheme.colorScheme.primary else Color.LightGray
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Text("All")
                        }
                        IconButton(onClick = { showAdd = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Add")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFEEEEEE),
                    navigationIconContentColor = Color.Black,
                    titleContentColor = titleColor // Use dynamic color for title
                )
            )
        },
        floatingActionButton = {
            // Day navigation buttons
            Column {
                FloatingActionButton(
                    onClick = {
                        if (dayOffset < 30) {
                            dayOffset++
                        }
                    },
                    modifier = Modifier.size(48.dp),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        contentDescription = "Next Day"
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                FloatingActionButton(
                    onClick = {
                        if (dayOffset > -30) {
                            dayOffset--
                        }
                    },
                    modifier = Modifier.size(48.dp),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "Previous Day"
                    )
                }
            }
        }
    ) { pad ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(pad)
        ) {
            // Main content
            if (items.isEmpty()) {
                Text("No tasks today", modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(
                    state = reorderState.listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .reorderable(reorderState)
                ) {
                    items(items, key = { it.task.id }) { row ->
                        val task = row.task
                        var edit by remember { mutableStateOf(false) }
                        // Mark as done if completed on this date OR any date after this (for past views)
                        val isCompleted = task.completedDate != null &&
                                (task.completedDate == dateStr ||
                                        task.completedDate > dateStr)

                        val dueLabel = recurrenceLabel(task.due, task.recurrence)

                        ReorderableItem(reorderState, key = row.task.id) { isDragging ->
                            val elevation = if (isDragging) 4.dp else 0.dp
                            Surface(tonalElevation = elevation) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                        .combinedClickable(
                                            onClick = {}, // no-op or use for quick mark/done
                                            onLongClick = { edit = true } // <-- open editor
                                        ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isCompleted,
                                        onCheckedChange = { vm.toggleDone(row, dateStr) },
                                        modifier = Modifier.size(20.dp)
                                    )

                                    Text(
                                        text = task.text,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 12.dp),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (isCompleted) Color.Gray else LocalContentColor.current,
                                        textDecoration = if (isCompleted) TextDecoration.LineThrough else null
                                    )

                                    Surface(
                                        color = Color(row.listColor),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.padding(start = 4.dp)
                                    ) {
                                        Text(
                                            row.listTitle,
                                            style = MaterialTheme.typography.labelSmall,
                                            modifier = Modifier.padding(
                                                horizontal = 6.dp,
                                                vertical = 2.dp
                                            ),
                                            color = Color.White
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    if (dueLabel.isNotBlank()) {
                                        Text(
                                            text = dueLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isCompleted) Color.Gray else LocalContentColor.current
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
                                if (edit) {
                                    TaskEditDialog(
                                        title = "Edit task",
                                        initialText = task.text,
                                        initialDue = task.due,
                                        initialRecurrence = task.recurrence,
                                        initialCategory = allCategories.firstOrNull { it.id == row.task.listId }
                                            ?: allCategories.first(),
                                        allCategories = allCategories,
                                        onSave = { t, d, rec, cId ->
                                            vm.rename(row.task, t, d, rec, cId)
                                            edit = false
                                        },
                                        onDismiss = { edit = false }
                                    )
                                }
                                HorizontalDivider(thickness = 0.5.dp)
                            }
                        }
                    }

                    // Add a spacer at the end for better visual padding
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
            if (showAdd && firstCategory != null) {
                TaskEditDialog(
                    title = "New task",
                    initialText = "",
                    initialDue = dateStr,
                    initialRecurrence = Recurrence.NONE,
                    initialCategory = firstCategory,
                    allCategories = allCategories,
                    onSave = { text, due, rec, catId ->
                        vm.add(text, due, rec, catId)
                        showAdd = false
                    },
                    onDismiss = { showAdd = false }
                )
            }
        }
    }
}