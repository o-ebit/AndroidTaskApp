package com.example.taskapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


// ❷ Give Task a new column (default = NONE)

@Entity(
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["categoryId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["categoryId"])]
)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val categoryId: Int,
    val text: String,
    val pos: Int,
    val due: String? = null,
    val completedDate: String? = null,
    val todoOrder: Int = 0,

    @ColumnInfo(defaultValue = "NONE")
    val recurrence: Recurrence = Recurrence.NONE // ✅ correctly annotated
)
