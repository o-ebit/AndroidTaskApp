package com.example.taskapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskapp.data.CategoryRepository
import com.example.taskapp.data.TaskWithList
import com.example.taskapp.util.db
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class TodosVm(app: Application) : AndroidViewModel(app) {

    private val repo = CategoryRepository(app.db)
    private val todayStr = LocalDate.now().toString()

    private val _showOutstanding = MutableStateFlow(true)
    val showOutstanding: StateFlow<Boolean> = _showOutstanding

    private val today = LocalDate.now().toString()

    val items: StateFlow<List<TaskWithList>> =
        repo.todosToday(today)
            .combine(_showOutstanding) { list, outstanding ->
                val sorted = list.sortedBy { it.task.todoOrder }
                if (outstanding) sorted.filter { it.task.completedDate != today } else sorted
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun move(from: Int, to: Int) = viewModelScope.launch {
        val current = items.value.toMutableList()
        val itm = current.removeAt(from)
        current.add(to, itm)
        val resequenced = current.mapIndexed { i, twl ->
            twl.task.copy(todoOrder = i)
        }
        repo.reorderTodo(resequenced)
    }

    fun toggleShow(outstanding: Boolean) {
        _showOutstanding.value = outstanding
    }

    fun toggleDone(item: TaskWithList) = viewModelScope.launch {
        repo.toggleToday(item.task, todayStr)
    }
}
