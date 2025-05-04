/* TasksVm.kt */
package com.example.taskapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.example.taskapp.data.Task
import com.example.taskapp.data.ChecklistRepository
import com.example.taskapp.util.db
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TasksVm(
    app: Application,
    private val listId: Int
) : AndroidViewModel(app) {

    private val repo = ChecklistRepository(app.db)

    /** stream of tasks in this checklist */
    val tasks: StateFlow<List<Task>> =
        repo.listItems(listId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun add(text: String) = viewModelScope.launch {
        repo.addTask(listId, text)
    }

    fun toggle(task: Task) = viewModelScope.launch {
        repo.toggle(task)
    }

    companion object {
        /** pass `listId` from the composable */
        fun Factory(app: Application, listId: Int) = viewModelFactory {
            initializer { TasksVm(app, listId) }
        }
    }
}
