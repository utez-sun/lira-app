package com.lira.tracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "listener_records")
data class ListenerRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dateString: String,      // Например, "22.11.2026" или "2026-11-22"
    val timestamp: Long,        // System.currentTimeMillis()
    val listeners: Long,        // Количество слушателей
    val delta: Long             // Разница с предыдущим показателем
)
