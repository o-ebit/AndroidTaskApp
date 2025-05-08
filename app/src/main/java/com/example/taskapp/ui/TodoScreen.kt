package com.example.taskapp.ui
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import org.burnoutcrew.reorderable.*
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskapp.viewmodel.TodosVm
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoScreen(
    onBack: () -> Unit,
    vm: TodosVm = viewModel()
) {
    val items by vm.items.collectAsState()
    val showOutstanding by vm.showOutstanding.collectAsState()

    val reorderState = rememberReorderableLazyListState(
        onMove = { from, to -> vm.move(from.index, to.index) }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("To-dos") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Row(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .height(40.dp),
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
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFEEEEEE),
                    navigationIconContentColor = Color.Black,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { pad ->

        LazyColumn(
            state = reorderState.listState,
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .reorderable(reorderState)
        ) {
            val today = LocalDate.now().toString()

            items(items, key = { it.task.id }) { row ->
                val task = row.task
                val done = task.completedDate == today
                val dueLabel = when (task.due) {
                    null -> ""
                    "EVERYDAY" -> "Every day"
                    else -> LocalDate.parse(task.due)
                        .format(DateTimeFormatter.ofPattern("dd MMM"))
                }
                ReorderableItem(reorderState, key = row.task.id) { isDragging ->
                    val elevation = if (isDragging) 4.dp else 0.dp
                    Surface(tonalElevation = elevation) {
                        // your existing ListItem() and Divider() go here
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = done,
                                onCheckedChange = { vm.toggleDone(row) },
                                modifier = Modifier.size(20.dp)
                            )

                            Text(
                                text = task.text,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 12.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (done) Color.Gray else LocalContentColor.current,
                                textDecoration = if (done) TextDecoration.LineThrough else null
                            )

                            Surface(
                                color = Color(row.listColor),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(start = 4.dp)
                            ) {
                                Text(
                                    row.listTitle,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    color = Color.White
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            if (dueLabel.isNotBlank()) {
                                Text(
                                    text = dueLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (done) Color.Gray else LocalContentColor.current
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

                        HorizontalDivider(thickness = 0.5.dp)
                    }
                }
            }
        }


    }
}
