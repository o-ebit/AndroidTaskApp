package com.example.taskapp.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM Checklist") fun all(): Flow<List<Checklist>>
    @Query("SELECT * FROM Checklist WHERE id = :id LIMIT 1")
    fun get(id: Int): Flow<Checklist?>
    @Insert suspend fun insert(list: Checklist)
    @Delete suspend fun delete(list: Checklist)
}