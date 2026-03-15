package com.nativeknights.leetflow.data.converters

import androidx.room.TypeConverter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

class StringListConverter {
    private val json = Json { ignoreUnknownKeys = true }
    
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return json.encodeToString(value)
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        return try {
            json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }
}