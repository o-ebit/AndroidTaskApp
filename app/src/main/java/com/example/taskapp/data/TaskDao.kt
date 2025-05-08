package com.example.taskapp.data

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {
    data class TaskWithList(
        @Embedded val task: Task,
        @ColumnInfo(name = "listTitle") val listTitle: String
    )

    @Query("SELECT * FROM Task WHERE listId = :listId ORDER BY pos")
    fun tasks(listId: Int): Flow<List<Task>>
    @Query("SELECT MAX(pos) FROM Task WHERE listId = :id") suspend fun maxPos(id:Int): Int?
    @Query("""
SELECT Task.*, Checklist.title AS listTitle
FROM Task
JOIN Checklist ON Checklist.id = Task.listId
WHERE
    Task.due IS NULL
    OR Task.due = 'EVERYDAY'
    OR Task.due <= :today
""")
    fun dueToday(today: String): Flow<List<TaskWithList>>
    @Insert suspend fun insert(task: Task)
    @Update suspend fun update(task: Task)
    @Update suspend fun updateMany(tasks: List<Task>)
    @Delete suspend fun delete(task: Task)
}