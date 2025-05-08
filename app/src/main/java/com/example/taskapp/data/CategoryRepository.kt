package com.example.taskapp.data

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

    suspend fun clearCompleted(listId: Int) =
        db.taskDao().deleteCompleted(listId)

    suspend fun reorderTodo(list: List<Task>) =
        db.taskDao().updateMany(list)

    suspend fun deleteCategory(list: Category) = db.categoryDao().delete(list)

    suspend fun addTask(listId: Int, text: String, due: String?) {
        val max = db.taskDao().maxPos(listId) ?: -1            // ← add this query
        db.taskDao().insert(Task(listId = listId, text = text, pos = max + 1, due = due))
    }
    suspend fun moveTasks(list: List<Task>) = db.taskDao().updateMany(list)

    suspend fun deleteTask(task: Task) = db.taskDao().delete(task)
    fun categoryId(id: Int) = db.categoryDao().get(id)

    suspend fun renameCategory(list: Category, newTitle: String) =
        db.categoryDao().update(list.copy(title = newTitle))

    fun todosToday(localDate: String) = db.taskDao().dueToday(localDate)

    suspend fun toggleToday(task: Task, today: String) =
        db.taskDao().update(
            task.copy(
                completedDate = if (task.completedDate == today) null else today
            )
        )

    suspend fun renameTask(task: Task, newText: String, newDue: String?) =
        db.taskDao().update(task.copy(text = newText, due = newDue))
}
