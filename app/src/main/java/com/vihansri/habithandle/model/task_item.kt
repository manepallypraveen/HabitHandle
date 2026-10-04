package com.vihansri.habithandle.model

data class TaskItem(
    val id: Int,
    val name: String,
    val totalSeconds: Long,
    val completed: Boolean = false
)
