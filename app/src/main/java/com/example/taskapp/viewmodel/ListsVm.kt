package com.example.taskapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.taskapp.data.Checklist
import com.example.taskapp.data.ChecklistRepository
import com.example.taskapp.util.asStateFlow
import com.example.taskapp.util.db

class ListsVm(app: Application) : AndroidViewModel(app) {
    private val repo = ChecklistRepository(app.db)
    val lists = repo.lists.asStateFlow(viewModelScope, emptyList())

    fun add(title: String) = viewModelScope.launch { repo.addList(title) }
    fun delete(list: Checklist) = viewModelScope.launch { repo.deleteList(list) }
}