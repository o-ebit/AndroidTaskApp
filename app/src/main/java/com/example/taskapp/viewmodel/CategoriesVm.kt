package com.example.taskapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.taskapp.data.Category
import com.example.taskapp.data.CategoryRepository
import com.example.taskapp.util.asStateFlow
import com.example.taskapp.util.db

class CategoriesVm(app: Application) : AndroidViewModel(app) {
    private val repo = CategoryRepository(app.db)
    val lists = repo.lists.asStateFlow(viewModelScope, emptyList())

    fun add(title: String) = viewModelScope.launch { repo.addCategory(title) }
    fun delete(list: Category) = viewModelScope.launch { repo.deleteCategory(list) }
    fun rename(list: Category, newTitle: String) = viewModelScope.launch {
        repo.renameCategory(list, newTitle)
    }
}