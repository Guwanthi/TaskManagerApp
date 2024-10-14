package com.example.taskmanagerapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "tasks")

data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val priority: Priority,
    val deadline: Date
)

//enum class Priority {
//    LOW,
//    MEDIUM,
//    HIGH
//}