package com.example.taskapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskapp.data.Category
import com.example.taskapp.data.CategoryRepository
import com.example.taskapp.util.asStateFlow
import com.example.taskapp.util.db
import kotlinx.coroutines.launch

class CategoriesVm(app: Application) : AndroidViewModel(app) {
    private val repo = CategoryRepository(app.db)
    val categories = repo.categories.asStateFlow(viewModelScope, emptyList())

    fun add(title: String) = viewModelScope.launch { repo.addCategory(title) }
    fun delete(category: Category) = viewModelScope.launch { repo.deleteCategory(category) }
    fun rename(category: Category, newTitle: String) = viewModelScope.launch {
        repo.renameCategory(category, newTitle)
    }
}