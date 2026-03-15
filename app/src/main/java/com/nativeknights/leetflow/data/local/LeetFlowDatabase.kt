package com.nativeknights.leetflow.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.nativeknights.leetflow.data.converters.StringListConverter
import com.nativeknights.leetflow.data.local.dao.RecallNoteDao
import com.nativeknights.leetflow.data.models.RecallNote

@Database(
    entities = [RecallNote::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(StringListConverter::class)
abstract class LeetFlowDatabase : RoomDatabase() {
    
    abstract fun recallNoteDao(): RecallNoteDao
    
    companion object {
        @Volatile
        private var INSTANCE: LeetFlowDatabase? = null
        
        fun getInstance(context: Context): LeetFlowDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LeetFlowDatabase::class.java,
                    "leetflow_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}