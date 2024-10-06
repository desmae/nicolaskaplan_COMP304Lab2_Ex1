package com.example.nicolaskaplan_comp304lab2_ex1

interface TaskRepository {
    fun getTasks(): List<Task>
    fun addTask(task: Task)
    fun updateTask(task: Task)
}