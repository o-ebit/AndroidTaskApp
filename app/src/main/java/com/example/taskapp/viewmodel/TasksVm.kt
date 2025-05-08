/* TasksVm.kt */
package com.example.taskapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.taskapp.data.Task
import com.example.taskapp.data.CategoryRepository
import com.example.taskapp.util.db
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map

class TasksVm(app: Application, state: SavedStateHandle) : AndroidViewModel(app) {
    private val listId = state.get<Int>("listId")!!
    private val repo = CategoryRepository(app.db)

    /** stream of tasks in this category */
    val tasks: StateFlow<List<Task>> =
        repo.listItems(listId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val title: StateFlow<String> = repo.categoryId(listId)
        .map { it?.title ?: "" }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    fun add(text: String, due: String?) = viewModelScope.launch {
        repo.addTask(listId, text, due)
    }

    fun delete(task: Task) = viewModelScope.launch { repo.deleteTask(task) }

    fun move(from: Int, to: Int) = viewModelScope.launch {
        if (from == to) return@launch
        val current = tasks.value.toMutableList()
        val item = current.removeAt(from)
        current.add(to, item)
        val reordered = current.mapIndexed { i, t -> t.copy(pos = i) }
        repo.moveTasks(reordered)          // DAO method that does @Update on list
    }
    fun rename(task: Task, newText: String, newDue: String?) = viewModelScope.launch {
        repo.renameTask(task, newText, newDue)
    }
    val headerColor: StateFlow<Long> = repo.categoryId(listId)
        .map { it?.color ?: 0xFFEEEEEE }
        .stateIn(viewModelScope, SharingStarted.Lazily, 0xFFEEEEEE)

    fun clearCompleted() = viewModelScope.launch {
        repo.clearCompleted(listId)
    }
}
