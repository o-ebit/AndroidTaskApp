package com.example.taskapp.data

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class TaskWithList(
    @Embedded val task: Task,
    @ColumnInfo(name = "listTitle") val listTitle: String,
    @ColumnInfo(name = "listColor") val listColor: Long
)