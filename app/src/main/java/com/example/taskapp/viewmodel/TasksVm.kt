/* TasksVm.kt */
package com.example.taskapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.taskapp.data.Task
import com.example.taskapp.data.ChecklistRepository
import com.example.taskapp.util.db
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map

class TasksVm(app: Application, state: SavedStateHandle) : AndroidViewModel(app) {
    private val listId = state.get<Int>("listId")!!
    private val repo = ChecklistRepository(app.db)

    /** stream of tasks in this checklist */
    val tasks: StateFlow<List<Task>> =
        repo.listItems(listId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val title: StateFlow<String> = repo.checklist(listId)
        .map { it?.title ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    fun add(text: String) = viewModelScope.launch {
        repo.addTask(listId, text)
    }

    fun toggle(task: Task) = viewModelScope.launch {
        repo.toggle(task)
    }
    fun delete(task: Task) = viewModelScope.launch { repo.deleteTask(task) }
}
