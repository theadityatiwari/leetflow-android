package com.nativeknights.leetflow.ui.screens.flashcards

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nativeknights.leetflow.data.local.LeetFlowDatabase
import com.nativeknights.leetflow.data.local.SecureStorageManager
import com.nativeknights.leetflow.data.models.RecallNote
import com.nativeknights.leetflow.data.remote.GeminiApiService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

enum class FlashcardTab {
    GENERATE, LIBRARY
}

sealed class GenerationState {
    object Idle : GenerationState()
    object Loading : GenerationState()
    data class Success(val note: RecallNote) : GenerationState()
    data class Error(val message: String) : GenerationState()
}

class FlashcardsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val storageManager = SecureStorageManager(application)
    private val apiService = GeminiApiService()
    private val database = LeetFlowDatabase.getInstance(application)
    private val noteDao = database.recallNoteDao()
    
    // Tab State
    private val _activeTab = MutableStateFlow(FlashcardTab.GENERATE)
    val activeTab: StateFlow<FlashcardTab> = _activeTab.asStateFlow()
    
    // Generation Form State
    private val _problemTitle = MutableStateFlow("")
    val problemTitle: StateFlow<String> = _problemTitle.asStateFlow()
    
    private val _solutionInput = MutableStateFlow("")
    val solutionInput: StateFlow<String> = _solutionInput.asStateFlow()
    
    private val _generationState = MutableStateFlow<GenerationState>(GenerationState.Idle)
    val generationState: StateFlow<GenerationState> = _generationState.asStateFlow()
    
    // Library State
    val allNotes: StateFlow<List<RecallNote>> = noteDao.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    private val _viewingNote = MutableStateFlow<RecallNote?>(null)
    val viewingNote: StateFlow<RecallNote?> = _viewingNote.asStateFlow()
    
    fun onTabChange(tab: FlashcardTab) {
        _activeTab.value = tab
        _viewingNote.value = null
        _generationState.value = GenerationState.Idle
    }
    
    fun onProblemTitleChange(title: String) {
        _problemTitle.value = title
    }
    
    fun onSolutionInputChange(solution: String) {
        _solutionInput.value = solution
    }
    
    fun generateFlashcard() {
        val apiKey = storageManager.getApiKey()
        if (apiKey.isNullOrEmpty()) {
            _generationState.value = GenerationState.Error("API key not configured")
            return
        }
        
        if (_problemTitle.value.isBlank() || _solutionInput.value.isBlank()) {
            _generationState.value = GenerationState.Error("Please fill in all fields")
            return
        }
        
        _generationState.value = GenerationState.Loading
        
        viewModelScope.launch {
            try {
                val model = apiService.createModel(apiKey)
                
                val prompt = buildGenerationPrompt(
                    title = _problemTitle.value,
                    solution = _solutionInput.value
                )
                
                val response = model.generateContent(prompt)
                val note = parseRecallNote(response.text ?: "")
                
                _generationState.value = GenerationState.Success(note)
                
            } catch (e: Exception) {
                _generationState.value = GenerationState.Error(
                    e.message ?: "Failed to generate flashcard"
                )
            }
        }
    }
    
    fun saveFlashcard(note: RecallNote) {
        viewModelScope.launch {
            try {
                noteDao.insertNote(note)
                _generationState.value = GenerationState.Idle
                _problemTitle.value = ""
                _solutionInput.value = ""
                _activeTab.value = FlashcardTab.LIBRARY
                _viewingNote.value = note
            } catch (e: Exception) {
                _generationState.value = GenerationState.Error("Failed to save: ${e.message}")
            }
        }
    }
    
    fun discardGeneration() {
        _generationState.value = GenerationState.Idle
    }
    
    fun viewNote(note: RecallNote) {
        _viewingNote.value = note
    }
    
    fun closeNoteView() {
        _viewingNote.value = null
    }
    
    fun deleteNote(note: RecallNote) {
        viewModelScope.launch {
            noteDao.deleteNote(note)
            _viewingNote.value = null
        }
    }
    
    private fun buildGenerationPrompt(title: String, solution: String): String {
        return """
You are a DSA learning expert. Create a concise flashcard for this problem.

Problem: "$title"
Solution/Logic:
```
$solution
```

Respond ONLY in this exact JSON format (no markdown, no extra text):
{
  "intuition": "One-line insight that captures the core idea",
  "explanation": "2-3 sentence simple explanation",
  "mistakesToAvoid": ["Common mistake 1", "Common mistake 2"],
  "futureUseFacts": ["Key fact to remember 1", "Key fact to remember 2"],
  "tags": ["Pattern", "DataStructure", "Technique"]
}

RULES:
- Intuition: Single powerful sentence (15-20 words max)
- Explanation: Simple, no jargon (50 words max)
- Mistakes: 2-3 common pitfalls
- Facts: 2-3 key takeaways for future problems
- Tags: 3-5 relevant tags (e.g., "Two Pointers", "Arrays", "Sliding Window")
        """.trimIndent()
    }
    
    private fun parseRecallNote(response: String): RecallNote {
        return try {
            val cleanJson = response
                .replace("```json", "")
                .replace("```", "")
                .trim()
            
            val intuitionRegex = """"intuition":\s*"([^"]+)"""".toRegex()
            val explanationRegex = """"explanation":\s*"([^"]+)"""".toRegex()
            val mistakesRegex = """"mistakesToAvoid":\s*\[(.*?)\]""".toRegex(RegexOption.DOT_MATCHES_ALL)
            val factsRegex = """"futureUseFacts":\s*\[(.*?)\]""".toRegex(RegexOption.DOT_MATCHES_ALL)
            val tagsRegex = """"tags":\s*\[(.*?)\]""".toRegex(RegexOption.DOT_MATCHES_ALL)
            
            val intuition = intuitionRegex.find(cleanJson)?.groupValues?.get(1) ?: "Key insight"
            val explanation = explanationRegex.find(cleanJson)?.groupValues?.get(1) ?: "Simple explanation"
            
            val mistakesRaw = mistakesRegex.find(cleanJson)?.groupValues?.get(1) ?: ""
            val mistakes = mistakesRaw
                .split("\",")
                .map { it.trim().removeSurrounding("\"").trim() }
                .filter { it.isNotBlank() }
            
            val factsRaw = factsRegex.find(cleanJson)?.groupValues?.get(1) ?: ""
            val facts = factsRaw
                .split("\",")
                .map { it.trim().removeSurrounding("\"").trim() }
                .filter { it.isNotBlank() }
            
            val tagsRaw = tagsRegex.find(cleanJson)?.groupValues?.get(1) ?: ""
            val tags = tagsRaw
                .split("\",")
                .map { it.trim().removeSurrounding("\"").trim() }
                .filter { it.isNotBlank() }
            
            RecallNote(
                id = UUID.randomUUID().toString(),
                problemTitle = _problemTitle.value,
                intuition = intuition,
                explanation = explanation,
                mistakesToAvoid = mistakes.ifEmpty { listOf("Check edge cases") },
                futureUseFacts = facts.ifEmpty { listOf("Practice similar problems") },
                tags = tags.ifEmpty { listOf("General") },
                createdAt = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            RecallNote(
                id = UUID.randomUUID().toString(),
                problemTitle = _problemTitle.value,
                intuition = "Parse error - please try again",
                explanation = "Failed to parse AI response",
                mistakesToAvoid = listOf("Check edge cases"),
                futureUseFacts = listOf("Review the solution"),
                tags = listOf("General"),
                createdAt = System.currentTimeMillis()
            )
        }
    }
}