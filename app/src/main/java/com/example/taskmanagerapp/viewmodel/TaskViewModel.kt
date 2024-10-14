package com.example.taskmanagerapp.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.taskmanagerapp.model.Task
import com.example.taskmanagerapp.model.TaskDatabase
import com.example.taskmanagerapp.repository.TaskRepository
import kotlinx.coroutines.launch
import com.example.taskmanagerapp.model.Priority


class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository
    val allTasks: LiveData<List<Task>>

    init {
        val taskDao = TaskDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(taskDao)
        allTasks = repository.allTasks
    }

    fun insert(task: Task) = viewModelScope.launch {
        repository.insert(task)
    }

    fun update(task: Task) = viewModelScope.launch {
        repository.update(task)
    }

    fun delete(task: Task) = viewModelScope.launch {
        repository.delete(task)
    }

    fun getTaskById(taskId: Int): LiveData<Task?> {
        val taskLiveData = MutableLiveData<Task?>()
        viewModelScope.launch {
            val task = repository.getTaskById(taskId)
            taskLiveData.postValue(task)
        }
        return taskLiveData
    }

    fun getTasksByPriority(): LiveData<List<Task>> {
        return repository.getTasksByPriority()
    }

    fun getTasksByPriorityFilter(priority: Priority): LiveData<List<Task>> {
        return repository.getTasksByPriorityFilter(priority)
    }

    fun getTasksByDeadline(): LiveData<List<Task>> {
        return repository.getTasksByDeadline()
    }

}
