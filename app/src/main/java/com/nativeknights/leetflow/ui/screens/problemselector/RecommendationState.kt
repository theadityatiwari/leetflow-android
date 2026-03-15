package com.nativeknights.leetflow.ui.screens.problemselector

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nativeknights.leetflow.data.local.SecureStorageManager
import com.nativeknights.leetflow.data.models.ProblemRecommendation
import com.nativeknights.leetflow.data.remote.GeminiApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RecommendationState {
    object Idle : RecommendationState()
    object Loading : RecommendationState()
    data class Success(val recommendation: ProblemRecommendation) : RecommendationState()
    data class Error(val message: String) : RecommendationState()
}

class ProblemSelectorViewModel(application: Application) : AndroidViewModel(application) {
    
    private val storageManager = SecureStorageManager(application)
    private val apiService = GeminiApiService()
    
    private val _state = MutableStateFlow<RecommendationState>(RecommendationState.Idle)
    val state: StateFlow<RecommendationState> = _state.asStateFlow()
    
    private val _preference = MutableStateFlow("")
    val preference: StateFlow<String> = _preference.asStateFlow()
    
    private val _rejectedTitles = MutableStateFlow<List<String>>(emptyList())
    
    fun onPreferenceChange(newPreference: String) {
        _preference.value = newPreference
    }
    
    fun getRecommendation() {
        val apiKey = storageManager.getApiKey()
        if (apiKey.isNullOrEmpty()) {
            _state.value = RecommendationState.Error("API key not configured")
            return
        }
        
        _state.value = RecommendationState.Loading
        
        viewModelScope.launch {
            try {
                val model = apiService.createModel(apiKey)
                
                val prompt = buildPrompt(
                    preference = _preference.value.ifEmpty { "Balanced growth" },
                    rejectedTitles = _rejectedTitles.value
                )
                
                val response = model.generateContent(prompt)
                val recommendation = parseRecommendation(response.text ?: "")
                
                _state.value = RecommendationState.Success(recommendation)
                
            } catch (e: Exception) {
                _state.value = RecommendationState.Error(
                    e.message ?: "Failed to get recommendation"
                )
            }
        }
    }
    
    fun rerollRecommendation() {
        // Add current recommendation title to rejected list
        val currentState = _state.value
        if (currentState is RecommendationState.Success) {
            _rejectedTitles.value = _rejectedTitles.value + currentState.recommendation.title
        }
        
        // Get new recommendation
        getRecommendation()
    }
    
    fun acceptRecommendation() {
        // TODO: Save problem to database
        // For now, just reset state
        _state.value = RecommendationState.Idle
        _preference.value = ""
        _rejectedTitles.value = emptyList()
    }
    
    fun resetState() {
        _state.value = RecommendationState.Idle
        _rejectedTitles.value = emptyList()
    }
    
    private fun buildPrompt(preference: String, rejectedTitles: List<String>): String {
        return """
You are a DSA coach. Recommend ONE specific LeetCode problem based on user's preference.

User Preference: "$preference"
${if (rejectedTitles.isNotEmpty()) "Already rejected: ${rejectedTitles.joinToString(", ")}" else ""}

Respond ONLY in this exact JSON format (no markdown, no extra text):
{
  "title": "Problem Name",
  "topic": "Arrays/DP/Graphs/Trees/etc",
  "difficulty": "Easy/Medium/Hard",
  "description": "Brief 1-line description",
  "reason": "Why this problem addresses their gaps/preference",
  "skillBuilder": "What specific skill/pattern they'll learn"
}

IMPORTANT: 
- Choose a problem NOT in the rejected list
- Match the difficulty to their preference (if specified)
- Focus on the topic they mentioned (if specified)
- Keep all fields concise (max 2 sentences each)
        """.trimIndent()
    }
    
    private fun parseRecommendation(response: String): ProblemRecommendation {
        return try {
            // Remove markdown code blocks if present
            val cleanJson = response
                .replace("```json", "")
                .replace("```", "")
                .trim()
            
            // Basic JSON parsing (you can use Gson/Kotlinx Serialization for production)
            val titleRegex = """"title":\s*"([^"]+)"""".toRegex()
            val topicRegex = """"topic":\s*"([^"]+)"""".toRegex()
            val difficultyRegex = """"difficulty":\s*"([^"]+)"""".toRegex()
            val descriptionRegex = """"description":\s*"([^"]+)"""".toRegex()
            val reasonRegex = """"reason":\s*"([^"]+)"""".toRegex()
            val skillBuilderRegex = """"skillBuilder":\s*"([^"]+)"""".toRegex()
            
            val title = titleRegex.find(cleanJson)?.groupValues?.get(1) ?: "Two Sum"
            val topic = topicRegex.find(cleanJson)?.groupValues?.get(1) ?: "Arrays"
            val difficulty = difficultyRegex.find(cleanJson)?.groupValues?.get(1) ?: "Medium"
            val description = descriptionRegex.find(cleanJson)?.groupValues?.get(1) ?: "Classic problem"
            val reason = reasonRegex.find(cleanJson)?.groupValues?.get(1) ?: "Good starting point"
            val skillBuilder = skillBuilderRegex.find(cleanJson)?.groupValues?.get(1) ?: "Problem solving skills"
            
            ProblemRecommendation(
                title = title,
                topic = topic,
                difficulty = difficulty,
                description = description,
                reason = reason,
                skillBuilder = skillBuilder,
                url = "https://leetcode.com/problemset/all/?search=${title.replace(" ", "+")}"
            )
        } catch (e: Exception) {
            // Fallback recommendation
            ProblemRecommendation(
                title = "Two Sum",
                topic = "Arrays",
                difficulty = "Easy",
                description = "Find two numbers that add up to target",
                reason = "Great warm-up problem to start with",
                skillBuilder = "Hash map pattern and array traversal",
                url = "https://leetcode.com/problems/two-sum/"
            )
        }
    }
}