package com.nativeknights.leetflow.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.nativeknights.leetflow.data.converters.StringListConverter

@Entity(tableName = "recall_notes")
@TypeConverters(StringListConverter::class)
data class RecallNote(
    @PrimaryKey
    val id: String,
    val problemTitle: String,
    val intuition: String,
    val explanation: String,
    val mistakesToAvoid: List<String>,
    val futureUseFacts: List<String>,
    val tags: List<String>,
    val createdAt: Long = System.currentTimeMillis()
)