package com.example.taskapp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.taskapp.viewmodel.ListsVm
import com.example.taskapp.data.Checklist

@Composable
fun ListsScreen(nav: NavHostController, vm: ListsVm = viewModel()) {
    val lists by vm.lists.collectAsState()
    var newTitle by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { if (newTitle.isNotBlank()) { vm.add(newTitle); newTitle = "" }},
                content = { Icon(Icons.Default.Add, null) }
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

            LazyColumn(Modifier.fillMaxSize()) {
                items(lists, key = Checklist::id) { list ->
                    ListItem(
                        headlineContent = { Text(list.title) },
                        modifier = Modifier
                            .clickable { nav.navigate("list/${list.id}") }
                            .fillMaxWidth(),
                        trailingContent = {
                            IconButton(onClick = { vm.delete(list) }) {
                                Icon(Icons.Default.Delete, null)
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
