package com.example.taskapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Checklist::class, Task::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun checklistDao(): ChecklistDao
    abstract fun taskDao(): TaskDao
}