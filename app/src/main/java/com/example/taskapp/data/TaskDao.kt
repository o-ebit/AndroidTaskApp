package com.example.taskapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM Task WHERE listId = :listId ORDER BY pos")
    fun tasks(listId: Int): Flow<List<Task>>
    @Query("SELECT MAX(pos) FROM Task WHERE listId = :id") suspend fun maxPos(id:Int): Int?
    @Insert suspend fun insert(task: Task)
    @Update suspend fun update(task: Task)
    @Update suspend fun updateMany(tasks: List<Task>)
    @Delete suspend fun delete(task: Task)
}