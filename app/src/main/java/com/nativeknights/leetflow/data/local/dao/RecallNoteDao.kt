package com.nativeknights.leetflow.data.local.dao

import androidx.room.*
import com.nativeknights.leetflow.data.models.RecallNote
import kotlinx.coroutines.flow.Flow

@Dao
interface RecallNoteDao {
    
    @Query("SELECT * FROM recall_notes ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<RecallNote>>
    
    @Query("SELECT * FROM recall_notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: String): RecallNote?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: RecallNote)
    
    @Delete
    suspend fun deleteNote(note: RecallNote)
    
    @Query("DELETE FROM recall_notes WHERE id = :noteId")
    suspend fun deleteNoteById(noteId: String)
    
    @Query("SELECT COUNT(*) FROM recall_notes")
    fun getNotesCount(): Flow<Int>
    
    @Query("SELECT * FROM recall_notes WHERE problemTitle LIKE '%' || :query || '%' OR intuition LIKE '%' || :query || '%'")
    fun searchNotes(query: String): Flow<List<RecallNote>>
}