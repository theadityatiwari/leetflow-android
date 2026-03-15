package com.nativeknights.leetflow.ui.screens.blindproblem

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.type.generationConfig
import com.nativeknights.leetflow.data.local.SecureStorageManager
import com.nativeknights.leetflow.data.models.BlindProblem
import com.nativeknights.leetflow.data.remote.GeminiApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

sealed class BlindState {
    object Idle : BlindState()
    object Loading : BlindState()
    data class Success(val problem: BlindProblem) : BlindState()
    data class Error(val message: String) : BlindState()
}

class BlindProblemViewModel(application: Application) : AndroidViewModel(application) {
    private val storageManager = SecureStorageManager(application)
    private val apiService = GeminiApiService()
    private val json = Json { ignoreUnknownKeys = true }

    private val _state = MutableStateFlow<BlindState>(BlindState.Idle)
    val state = _state.asStateFlow()

    fun suggestProblem(difficulty: String) {
        val apiKey = storageManager.getApiKey() ?: return
        _state.value = BlindState.Loading
        
        viewModelScope.launch {
            try {
                val config = generationConfig { responseMimeType = "application/json" }
                val model = apiService.createModel(apiKey)
                
                // CRITICAL PROMPT: Tell Gemini NOT to reveal the topic/pattern
                val prompt = """
                    Suggest a $difficulty LeetCode problem. 
                    STRICT RULES: Do NOT mention the data structure, algorithm, or pattern used.
                    Return JSON: {"title": "...", "difficulty": "$difficulty", "description": "...", "leetCodeUrl": "..."}
                """.trimIndent()
                
                val response = model.generateContent(prompt)
                val problem = json.decodeFromString<BlindProblem>(response.text ?: "")
                _state.value = BlindState.Success(problem)
            } catch (e: Exception) {
                _state.value = BlindState.Error("API Limit hit or network error")
            }
        }
    }
}