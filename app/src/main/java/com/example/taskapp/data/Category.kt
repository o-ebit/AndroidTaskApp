package com.example.taskapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val color: Long            /* ARGB hex, e.g. 0xFF3F51B5 */
)
