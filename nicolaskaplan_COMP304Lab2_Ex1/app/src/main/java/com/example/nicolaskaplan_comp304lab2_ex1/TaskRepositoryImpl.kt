package com.example.nicolaskaplan_comp304lab2_ex1

import android.icu.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

class TaskRepositoryImpl : TaskRepository {
    private val tasks = mutableListOf<Task>(
        Task(1, "Do something", "Description...", getLocalDateFromString("2023-12-01"), false),
        Task(2, "Wake up", "Description...", getLocalDateFromString("2023-11-25"), true),
        Task(3, "Go to sleep", "Description...", getLocalDateFromString("2023-11-25"), false)
    )

    override fun getTasks(): List<Task> = tasks

    override fun addTask(task: Task) {
        val newId = (tasks.maxOfOrNull { it.id } ?: 0) + 1
        tasks.add(task.copy(id = newId))
    }

    override fun updateTask(task: Task) {
        val index = tasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tasks[index] = task
        }
    }
    private fun getLocalDateFromString(dateString: String): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = formatter.parse(dateString)
        val calendar = Calendar.getInstance()
        calendar.time = date

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Month is 0-indexed
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return "$year-$month-$day"
    }
}