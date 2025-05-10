package com.example.taskapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface TaskDao {

    @Query("SELECT * FROM Task WHERE categoryId = :categoryId ORDER BY pos")
    fun tasks(categoryId: Int): Flow<List<Task>>
    @Query("SELECT MAX(pos) FROM Task WHERE categoryId = :id")
    suspend fun maxPos(id: Int): Int?

    @Query(
        """
SELECT Task.*, Category.title AS categoryTitle, Category.color AS categoryColor
FROM Task
JOIN Category ON Category.id = Task.categoryId
WHERE due <= :date
ORDER BY todoOrder ASC
"""
    )
    fun dueOnDate(date: String): Flow<List<TaskWithCategoryInfo>>

    @Query("SELECT MAX(todoOrder) FROM Task")
    suspend fun maxTodoOrder(): Int?

    @Query("DELETE FROM Task WHERE categoryId = :id AND completedDate IS NOT NULL AND recurrence = 'NONE'")
    suspend fun deleteCompleted(id: Int)

    @Query("SELECT MAX(pos) FROM Task WHERE categoryId = :categoryId")
    suspend fun getMaxPos(categoryId: Int): Int?

    @Update
    suspend fun updateMany(tasks: List<Task>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)
    @Update
    suspend fun update(task: Task)
    @Delete
    suspend fun delete(task: Task)

    // ❺ Remove completed recurring “next instance”
    @Query(
        """
DELETE FROM Task
WHERE categoryId = :categoryId
  AND text   = :text
  AND recurrence = :rec
  AND due    = :dueNext
  AND completedDate IS NULL
"""
    )
    suspend fun deleteNextInstance(
        categoryId: Int, text: String,
        rec: Recurrence, dueNext: String
    )
}