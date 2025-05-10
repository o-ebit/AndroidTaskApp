package com.example.taskapp.data

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class TaskWithCategoryInfo(
    @Embedded val task: Task,
    @ColumnInfo(name = "categoryTitle") val categoryTitle: String,
    @ColumnInfo(name = "categoryColor") val categoryColor: Long
)