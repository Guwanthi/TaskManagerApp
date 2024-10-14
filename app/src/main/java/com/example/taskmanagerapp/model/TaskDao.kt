package com.example.taskmanagerapp.model

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY deadline ASC")
    fun getAllTasks(): LiveData<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: Int): Task?

    @Query("SELECT * FROM tasks ORDER BY priority DESC")
    fun getTasksByPriority(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY deadline ASC")
    fun getTasksByPriorityFilter(priority: Priority): LiveData<List<Task>>

    @Query("SELECT * FROM tasks ORDER BY deadline ASC")
    fun getTasksByDeadline(): LiveData<List<Task>>

}
