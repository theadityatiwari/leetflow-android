package com.nativeknights.leetflow.ui.screens.complexityblitz

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.type.generationConfig
import com.nativeknights.leetflow.data.local.SecureStorageManager
import com.nativeknights.leetflow.data.models.BlitzQuestion
import com.nativeknights.leetflow.data.remote.GeminiApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

sealed class BlitzState {
    object Idle : BlitzState()
    object Loading : BlitzState()
    data class Question(
        val data: BlitzQuestion,
        val selectedTime: String? = null,
        val isCorrect: Boolean? = null
    ) : BlitzState()
    data class Error(val message: String) : BlitzState()
}

class ComplexityBlitzViewModel(application: Application) : AndroidViewModel(application) {
    private val storageManager = SecureStorageManager(application)
    private val apiService = GeminiApiService()
    private val json = Json { ignoreUnknownKeys = true }

    private val _state = MutableStateFlow<BlitzState>(BlitzState.Idle)
    val state: StateFlow<BlitzState> = _state.asStateFlow()

    private val _selectedLanguage = MutableStateFlow("Python")
    val selectedLanguage: StateFlow<String> = _selectedLanguage.asStateFlow()

    val availableLanguages = listOf(
        "Python",
        "JavaScript",
        "Java",
        "C++",
        "Go",
        "Rust"
    )

    fun selectLanguage(language: String) {
        _selectedLanguage.value = language
    }

    fun generateQuestion() {
        val apiKey = storageManager.getApiKey()
        if (apiKey.isNullOrEmpty()) {
            _state.value = BlitzState.Error("API key not configured")
            return
        }

        _state.value = BlitzState.Loading

        viewModelScope.launch {
            try {
                val model = apiService.createModel(apiKey)

                val prompt = buildPrompt(_selectedLanguage.value)

                val response = model.generateContent(prompt)
                val cleanJson = response.text?.trim()
                    ?.replace("```json", "")
                    ?.replace("```", "")
                    ?.trim() ?: "{}"

                val question = json.decodeFromString<BlitzQuestion>(cleanJson)
                _state.value = BlitzState.Question(question)

            } catch (e: Exception) {
                _state.value = BlitzState.Error(e.message ?: "Failed to generate question")
            }
        }
    }

    fun checkAnswer(answer: String) {
        val currentState = _state.value
        if (currentState is BlitzState.Question) {
            val correct = answer == currentState.data.correctTime
            _state.value = currentState.copy(
                selectedTime = answer,
                isCorrect = correct
            )
        }
    }

    fun nextQuestion() {
        generateQuestion()
    }

    fun reset() {
        _state.value = BlitzState.Idle
    }

    private fun buildPrompt(language: String): String {
        return """
Generate a code complexity analysis question in $language.

STRICT JSON FORMAT (no markdown, no extra text):
{
  "codeSnippet": "actual $language code (3-8 lines)",
  "options": ["O(1)", "O(n)", "O(n log n)", "O(n²)"],
  "correctTime": "O(n)",
  "correctSpace": "O(1)",
  "explanation": "Brief explanation of why"
}

RULES:
1. Code must be valid $language syntax
2. Keep code simple and readable (3-8 lines)
3. Mix difficulty: 50% easy/medium, 50% medium/hard
4. Include variety: loops, recursion, nested structures
5. correctTime must be ONE of the options array values
6. options must have exactly 4 different complexities
7. Return ONLY the JSON, no markdown formatting

Examples of good code snippets:
- Simple loops (O(n))
- Nested loops (O(n²))
- Binary search patterns (O(log n))
- Constant time operations (O(1))
- Sorting algorithms (O(n log n))

Generate the question now.
        """.trimIndent()
    }
}