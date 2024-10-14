package com.example.taskmanagerapp.repository

import androidx.lifecycle.LiveData
import com.example.taskmanagerapp.model.Task
import com.example.taskmanagerapp.model.TaskDao
import com.example.taskmanagerapp.model.Priority


class TaskRepository(private val taskDao: TaskDao) {

    val allTasks: LiveData<List<Task>> = taskDao.getAllTasks()

    suspend fun insert(task: Task) {
        taskDao.insert(task)
    }

    suspend fun update(task: Task) {
        taskDao.update(task)
    }

    suspend fun delete(task: Task) {
        taskDao.delete(task)
    }

    suspend fun getTaskById(taskId: Int): Task? {
        return taskDao.getTaskById(taskId)
    }

    fun getTasksByPriority(): LiveData<List<Task>> {
        return taskDao.getTasksByPriority()
    }

    fun getTasksByPriorityFilter(priority: Priority): LiveData<List<Task>> {
        return taskDao.getTasksByPriorityFilter(priority)
    }

    fun getTasksByDeadline(): LiveData<List<Task>> {
        return taskDao.getTasksByDeadline()
    }

}
