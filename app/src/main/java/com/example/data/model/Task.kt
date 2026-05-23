package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String, // Work, Personal, Study, Fitness
    val priority: String, // High, Medium, Low
    val deadline: Long, // timestamp
    val isCompleted: Boolean = false,
    val aiScheduledTime: String? = null, // e.g. "09:00 AM"
    val aiReasoning: String? = null // explanation of order/priority
)
