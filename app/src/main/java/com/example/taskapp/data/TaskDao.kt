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
    @Query("""
SELECT Task.*, Category.title AS listTitle, Category.color AS listColor
FROM Task
JOIN Category ON Category.id = Task.listId
WHERE (
    Task.due = 'EVERYDAY'
    OR Task.due <= :date
)
AND (
    Task.completedDate IS NULL
    OR Task.completedDate >= :date
)
ORDER BY todoOrder ASC
""")
    fun todosForDate(date: String): Flow<List<TaskWithList>>
    @Query("SELECT MAX(todoOrder) FROM Task")
    suspend fun maxTodoOrder(): Int?

    @Query("DELETE FROM Task WHERE listId = :id AND completedDate IS NOT NULL AND due != 'EVERYDAY'")
    suspend fun deleteCompleted(id: Int)

    @Update suspend fun updateMany(tasks: List<Task>)
    @Insert suspend fun insert(task: Task)
    @Update suspend fun update(task: Task)
    @Delete suspend fun delete(task: Task)
}