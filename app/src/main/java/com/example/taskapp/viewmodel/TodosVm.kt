package com.example.taskapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskapp.data.ChecklistRepository
import com.example.taskapp.data.TaskDao
import com.example.taskapp.util.db
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

class TodosVm(app: Application) : AndroidViewModel(app) {

    private val repo = ChecklistRepository(app.db)
    private val todayStr = LocalDate.now().toString()

    private val _showOutstanding = MutableStateFlow(true)
    val showOutstanding: StateFlow<Boolean> = _showOutstanding

    val items: StateFlow<List<TaskDao.TaskWithList>> =
        repo.todosToday(todayStr)
            .combine(_showOutstanding) { list, outstanding ->
                if (outstanding) list.filter { it.task.completedDate != todayStr } else list
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun toggleShow(outstanding: Boolean) {
        _showOutstanding.value = outstanding
    }

    fun toggleDone(item: TaskDao.TaskWithList) = viewModelScope.launch {
        repo.toggleToday(item.task, todayStr)
    }
}
