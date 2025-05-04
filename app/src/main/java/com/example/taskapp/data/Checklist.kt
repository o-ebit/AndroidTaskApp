package com.example.taskapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Checklist(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String
)
