package com.example.taskapp.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    foreignKeys = [ForeignKey(
        entity = Checklist::class,
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
    /**  null  ➜ no due date
     *  "EVERYDAY" ➜ special label
     *  ISO-8601 date (yyyy-MM-dd) ➜ single due date */
    val due: String? = null,
    /** yyyy-MM-dd when last completed, null = never completed */
    val completedDate: String? = null
)
