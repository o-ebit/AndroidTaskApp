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
    private val _showOutstanding = MutableStateFlow(true)
    val showOutstanding: StateFlow<Boolean> = _showOutstanding

    private val today = LocalDate.now().toString()


    fun move(from: Int, to: Int, date: String) = viewModelScope.launch {
        val current = itemsFor(date).first().toMutableList()

        if (from in current.indices && to in 0..current.size) {
            val itm = current.removeAt(from)
            current.add(to, itm)

            val resequenced = current.mapIndexed { i, row ->
                row.task.copy(todoOrder = i)
            }

            repo.reorderTodo(resequenced)
        }
    }


    fun toggleShow(outstanding: Boolean) {
        _showOutstanding.value = outstanding
    }

    fun toggleDone(item: TaskWithList, dateStr: String) = viewModelScope.launch {
        repo.toggleToday(item.task, dateStr)
    }

    fun itemsFor(dateStr: String): Flow<List<TaskWithList>> {
        return repo.todosForDate(dateStr).combine(_showOutstanding) { items, showOutstanding ->
            items.filter { item ->
                val task = item.task
                val due = task.due
                val completed = task.completedDate
                val isDue = due == "EVERYDAY" || (due != null && due <= dateStr)

                if (!isDue) return@filter false

                val isCompletedOnOrAfter = completed != null && completed >= dateStr

                if (showOutstanding) {
                    // A: show incomplete tasks only
                    completed == null
                } else {
                    // A + B: show incomplete OR completed on/after this date
                    completed == null || isCompletedOnOrAfter
                }
            }
        }
    }

}
