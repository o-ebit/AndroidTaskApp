package com.example.taskapp.data

class ChecklistRepository(private val db: AppDatabase) {
    val lists = db.checklistDao().all()
    fun listItems(id: Int) = db.taskDao().tasks(id)

    suspend fun addList(title: String) = db.checklistDao().insert(Checklist(title = title))
    suspend fun deleteList(list: Checklist) = db.checklistDao().delete(list)

    suspend fun addTask(listId: Int, text: String, due: String?) {
        val max = db.taskDao().maxPos(listId) ?: -1            // ← add this query
        db.taskDao().insert(Task(listId = listId, text = text, pos = max + 1, due = due))
    }
    suspend fun moveTasks(list: List<Task>) = db.taskDao().updateMany(list)

    suspend fun deleteTask(task: Task) = db.taskDao().delete(task)
    fun checklist(id: Int) = db.checklistDao().get(id)

    suspend fun renameList(list: Checklist, newTitle: String) =
        db.checklistDao().update(list.copy(title = newTitle))

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
