package com.example.taskapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Category::class, Task::class], version = 8, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun taskDao(): TaskDao
}