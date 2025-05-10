package com.example.taskapp.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

import android.util.Log

class CategoryRepository(private val db: AppDatabase) {
    val lists = db.categoryDao().all()
    fun listItems(id: Int) = db.taskDao().tasks(id)

    val PALETTE = listOf(
        0xFFF44336, // Red
        0xFFE91E63, // Pink
        0xFF9C27B0, // Purple
        0xFF673AB7, // Deep Purple
        0xFF3F51B5, // Indigo
        0xFF2196F3, // Blue
        0xFF03A9F4, // Light Blue
        0xFF00BCD4, // Cyan
        0xFF009688, // Teal
        0xFF4CAF50, // Green
        0xFF8BC34A, // Light Green
        0xFFCDDC39, // Lime
        0xFFFFEB3B, // Yellow
        0xFFFFC107, // Amber
        0xFFFF9800, // Orange
        0xFFFF5722, // Deep Orange
        0xFF795548, // Brown
        0xFF9E9E9E, // Grey
        0xFF607D8B, // Blue Grey
        0xFF6D4C41, // Dark Brown
        0xFFB71C1C, // Dark Red
        0xFF880E4F, // Dark Pink
        0xFF4A148C, // Dark Purple
        0xFF1A237E, // Dark Indigo
        0xFF0D47A1, // Dark Blue
        0xFF006064, // Dark Cyan
        0xFF1B5E20, // Dark Green
        0xFFF57F17, // Dark Yellow
        0xFFE65100, // Dark Orange
        0xFFBF360C  // Dark Deep Orange
    )

    suspend fun addCategory(title: String) {
        val color = PALETTE.random()   // simple auto-assign
        db.categoryDao().insert(Category(title = title, color = color))
    }

    private suspend fun getMaxPos(categoryId: Int): Int {
        return db.taskDao().getMaxPos(categoryId) ?: -1
    }
    suspend fun clearCompleted(listId: Int) =
        db.taskDao().deleteCompleted(listId)

    suspend fun reorderTodo(list: List<Task>) =
        db.taskDao().updateMany(list)

    suspend fun deleteCategory(list: Category) = db.categoryDao().delete(list)

    suspend fun addTask(categoryId: Int, text: String, due: String?, rec: Recurrence = Recurrence.NONE) {
        val task = Task(
            text = text,
            due = due,
            listId = categoryId,
            pos = getMaxPos(categoryId) + 1,
            recurrence = rec
        )
        Log.d("TaskAdd", "Inserting task: $task")
        db.taskDao().insert(task)
    }
    suspend fun moveTasks(list: List<Task>) = db.taskDao().updateMany(list)

    suspend fun deleteTask(task: Task) {
        Log.d("TaskAdd", "Deleting task: $task")
        db.taskDao().delete(task)
    }

    fun categoryId(id: Int) = db.categoryDao().get(id)

    suspend fun renameCategory(list: Category, newTitle: String) =
        db.categoryDao().update(list.copy(title = newTitle))

    fun dueOnDate(date: String): Flow<List<TaskWithList>> {
        return db.taskDao().dueOnDate(date)
    }

    suspend fun toggleToday(task:Task, today:String) {
        val nowDone = task.completedDate == null
        val newCompleted = if (nowDone) today else null
        db.taskDao().update(task.copy(completedDate = newCompleted))

        if (task.recurrence != Recurrence.NONE) {
            val baseDate = task.due?.let { LocalDate.parse(it) } ?: LocalDate.now()
            val nextDate = task.recurrence.recurrenceNextDate(baseDate)?.toString()
            if (nowDone) {
                if (nextDate != null) {
                    // create next task only if nextDate is valid
                    addTask(task.listId, task.text, nextDate, task.recurrence)
                }
            } else {
                if (nextDate != null) {
                    db.taskDao().deleteNextInstance(
                        task.listId, task.text, task.recurrence, nextDate
                    )
                }
            }
        }
    }
    suspend fun updateTask(
        task: Task,
        newText: String,
        newDue: String?,
        newRec: Recurrence,
        newListId: Int,
    ) {
        val updated = task.copy(
            text = newText,
            due = newDue,
            listId = newListId,
            recurrence = newRec
        )
        Log.d("TaskAdd", "Updating task: $task")
        db.taskDao().update(updated)
    }

}
