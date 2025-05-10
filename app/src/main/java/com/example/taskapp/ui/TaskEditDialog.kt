// TaskEditDialog.kt
package com.example.taskapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import com.example.taskapp.data.Recurrence
import com.example.taskapp.data.displayName
import com.example.taskapp.data.recurrenceLabel
import com.example.taskapp.data.recurrenceNextDue
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditDialog(
    initialText: String,
    initialDue: String? = null,
    initialRecurrence: Recurrence = Recurrence.NONE,
    initialCategory: Category,
    allCategories: List<Category>,
    onSave: (String, String?, Recurrence, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(initialText) }
    var due by remember { mutableStateOf(initialDue) }
    var recurrence by remember { mutableStateOf(initialRecurrence) }
    var selectedCategory by remember { mutableStateOf(initialCategory) }
    var pickDate by remember { mutableStateOf(false) }

    if (pickDate) {
        val pickerState = rememberDatePickerState()
        DatePickerDialog(onDismissRequest = { pickDate = false }, confirmButton = {
            TextButton(onClick = {
                pickerState.selectedDateMillis?.let {
                    due = LocalDate.ofEpochDay(it / 86_400_000).toString()
                }
                pickDate = false
            }) { Text("OK") }
        }, dismissButton = {
            TextButton(onClick = { pickDate = false }) { Text("Cancel") }
        }, content = { DatePicker(state = pickerState) })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(onClick = onDismiss)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp), // Push the content down slightly from the top
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Surface(
                modifier = Modifier
                    .clickable(enabled = false) {}, // prevent dismissal on inner clicks
                shape = MaterialTheme.shapes.medium, tonalElevation = 6.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text("Task text") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            recurrenceLabel(due, recurrence),
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            ", in category: " + selectedCategory.title,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }

                    Spacer(Modifier.height(18.dp))
                    Row(Modifier.horizontalScroll(rememberScrollState())) {
                        CompactChip("None") { due = null; recurrence = Recurrence.NONE }
                        CompactChip("Today") {
                            due = LocalDate.now().toString(); recurrence = Recurrence.NONE
                        }
                        CompactChip("Tomorrow") {
                            due = LocalDate.now().plusDays(1).toString(); recurrence =
                            Recurrence.NONE
                        }
                        CompactChip("Date…") { pickDate = true }
                    }

                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.horizontalScroll(rememberScrollState())) {
                        Recurrence.entries.filter { it != Recurrence.NONE }.forEach { rec ->
                            CompactChip(rec.displayName()) {
                                recurrence = rec
                                due = rec.recurrenceNextDue()?.toString()
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Row(Modifier.horizontalScroll(rememberScrollState())) {
                        allCategories.forEach { cat ->
                            Surface(color = Color(cat.color),
                                shape = MaterialTheme.shapes.small,
                                tonalElevation = if (cat == selectedCategory) 4.dp else 1.dp,
                                modifier = Modifier
                                    .padding(end = 4.dp)
                                    .clickable { selectedCategory = cat }) {
                                Text(
                                    cat.title,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Row(
                        Modifier.align(Alignment.End),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(onClick = onDismiss) { Text("Cancel") }
                        TextButton(onClick = {
                            if (text.isNotBlank()) onSave(
                                text,
                                due,
                                recurrence,
                                selectedCategory.id
                            )
                        }) { Text("Save") }
                    }
                }
            }
        }
    }
}

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
