// TaskEditDialog.kt
package com.example.taskapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.taskapp.data.Category
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditDialog(
    title: String,
    initialText: String,
    initialDue: String? = null,
    initialCategory: Category,
    allCategories: List<Category>,
    onSave: (String, String?, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(initialText) }
    var due by remember { mutableStateOf(initialDue) }
    var pickDate by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(initialCategory) }

    if (pickDate) {
        val pickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { pickDate = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = pickerState.selectedDateMillis
                        if (millis != null) {
                            due = LocalDate.ofEpochDay(millis / 86_400_000).toString()
                        }
                        pickDate = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { pickDate = false }) { Text("Cancel") }
            },
            content = { DatePicker(state = pickerState) }
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
                Row(Modifier.horizontalScroll(rememberScrollState())) {
                    CompactChip("None") { due = null }
                    CompactChip("Today") { due = LocalDate.now().toString() }
                    CompactChip("Tomorrow") { due = LocalDate.now().plusDays(1).toString() }
                    CompactChip("Every day") { due = "EVERYDAY" }
                    CompactChip("Date…") { pickDate = true }
                }
                Spacer(Modifier.height(8.dp))
                Row(Modifier.horizontalScroll(rememberScrollState())) {
                    allCategories.forEach { cat ->
                        Surface(
                            color = Color(cat.color),
                            shape = MaterialTheme.shapes.small,
                            tonalElevation = if (cat == selectedCategory) 4.dp else 1.dp,
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .clickable { selectedCategory = cat }
                        ) {
                            Text(
                                cat.title,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = when (due) {
                            null -> "No due date"
                            "EVERYDAY" -> "Every day"
                            else -> LocalDate.parse(due)
                                .format(DateTimeFormatter.ofPattern("dd MMM"))
                        },
                        style = MaterialTheme.typography.labelSmall
                    )
                    Spacer(Modifier.width(12.dp))
                    Surface(
                        color = Color(selectedCategory.color),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            selectedCategory.title,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.White
                        )
                    }
                }
            }

        },
        confirmButton = {
            TextButton(
                onClick = { if (text.isNotBlank()) onSave(text, due, selectedCategory.id) }
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

data class CategoryUiModel(val id: Int, val title: String, val color: Long)

@Composable
fun CompactChip(text: String, onClick: () -> Unit) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp,
        modifier = Modifier
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
