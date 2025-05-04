package com.example.taskapp.data

class ChecklistRepository(private val db: AppDatabase) {
    val lists = db.checklistDao().all()
    fun listItems(id: Int) = db.taskDao().tasks(id)

    suspend fun addList(title: String) = db.checklistDao().insert(Checklist(title = title))
    suspend fun deleteList(list: Checklist) = db.checklistDao().delete(list)

    suspend fun addTask(listId: Int, text: String) =
        db.taskDao().insert(Task(listId = listId, text = text))

    suspend fun toggle(task: Task) =
        db.taskDao().update(task.copy(done = !task.done))
}