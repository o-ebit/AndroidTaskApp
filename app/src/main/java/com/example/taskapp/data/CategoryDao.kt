package com.example.taskapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM Category")
    fun all(): Flow<List<Category>>

    @Query("SELECT * FROM Category WHERE id = :id LIMIT 1")
    fun get(id: Int): Flow<Category?>
    @Insert
    suspend fun insert(category: Category)
    @Delete
    suspend fun delete(category: Category)
    @Update
    suspend fun update(category: Category)
}