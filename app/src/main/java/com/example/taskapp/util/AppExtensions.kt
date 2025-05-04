package com.example.taskapp.util

import android.content.Context
import androidx.room.Room
import com.example.taskapp.data.AppDatabase

val Context.db: AppDatabase
    get() = Room.databaseBuilder(
        applicationContext,
        AppDatabase::class.java,
        "checklists.db"
    ).build()