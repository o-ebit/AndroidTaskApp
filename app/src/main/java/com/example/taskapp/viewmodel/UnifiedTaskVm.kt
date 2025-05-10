package com.example.taskapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.taskapp.data.CategoryRepository
import com.example.taskapp.data.Recurrence
import com.example.taskapp.data.Task
import com.example.taskapp.data.TaskWithList
import com.example.taskapp.util.db
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UnifiedTaskViewModel(app: Application, state: SavedStateHandle? = null) :
    AndroidViewModel(app) {

    private val repo = CategoryRepository(app.db)

    private val listId = state?.get<Int>("listId")

    val tasksForList: StateFlow<List<Task>> =
        if (listId != null) {
            repo.listItems(listId)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
        } else {
            MutableStateFlow(emptyList())
        }

    val title: StateFlow<String> =
        if (listId != null) {
            repo.categoryId(listId)
                .map { it?.title ?: "" }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")
        } else {
            MutableStateFlow("")
        }

    val headerColor: StateFlow<Long> =
        if (listId != null) {
            repo.categoryId(listId)
                .map { it?.color ?: 0xFFEEEEEE }
                .stateIn(viewModelScope, SharingStarted.Lazily, 0xFFEEEEEE)
        } else {
            MutableStateFlow(0xFFEEEEEE)
        }

    private val _showOutstanding = MutableStateFlow(true)
    val showOutstanding: StateFlow<Boolean> = _showOutstanding

    fun toggleShow(outstanding: Boolean) {
        _showOutstanding.value = outstanding
    }

    fun dueOnDate(dateStr: String): Flow<List<TaskWithList>> =
        combine(repo.dueOnDate(dateStr), _showOutstanding) { all, show ->
            if (show) all.filter { it.task.completedDate == null } else all
        }

    fun add(text: String, due: String?, rec: Recurrence, categoryId: Int) = viewModelScope.launch {
        repo.addTask(categoryId, text, due, rec)
    }

    fun rename(task: Task, newText: String, newDue: String?, rec: Recurrence, newCategoryId: Int) =
        viewModelScope.launch {
            repo.updateTask(task, newText, newDue, rec, newCategoryId)
        }

    fun delete(task: Task) = viewModelScope.launch {
        repo.deleteTask(task)
    }

    fun clearCompleted(listId: Int) = viewModelScope.launch {
        repo.clearCompleted(listId)
    }

    fun toggleDone(item: TaskWithList, dateStr: String) = viewModelScope.launch {
        repo.toggleToday(item.task, dateStr)
    }

    fun moveInList(list: List<Task>, from: Int, to: Int) = viewModelScope.launch {
        val current = list.toMutableList()
        val itm = current.removeAt(from)
        current.add(to, itm)
        val reordered = current.mapIndexed { i, row -> row.copy(pos = i) }
        repo.moveTasks(reordered)
    }

    fun moveInToDo(list: List<Task>, from: Int, to: Int) = viewModelScope.launch {
        val current = list.toMutableList()
        val itm = current.removeAt(from)
        current.add(to, itm)
        val reordered = current.mapIndexed { i, row -> row.copy(todoOrder = i) }
        repo.moveTasks(reordered)
    }
}

