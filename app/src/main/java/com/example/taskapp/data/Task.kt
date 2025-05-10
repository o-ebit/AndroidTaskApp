package com.example.taskapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index


// ❷ Give Task a new column (default = NONE)

@Entity(
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["id"],
        childColumns = ["listId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["listId"])]
)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val listId: Int,
    val text: String,
    val pos: Int,
    val due: String? = null,
    val completedDate: String? = null,
    val todoOrder: Int = 0,

    @ColumnInfo(defaultValue = "NONE")
    val recurrence: Recurrence = Recurrence.NONE // ✅ correctly annotated
)
