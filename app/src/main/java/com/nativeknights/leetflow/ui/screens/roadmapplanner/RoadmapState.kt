package com.nativeknights.leetflow.ui.screens.roadmapplanner

import RoadmapPlan
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.type.generationConfig
import com.nativeknights.leetflow.data.local.SecureStorageManager
import com.nativeknights.leetflow.data.models.*
import com.nativeknights.leetflow.data.remote.GeminiApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

sealed class RoadmapState {
    object Idle : RoadmapState()
    object Loading : RoadmapState()
    data class Success(val plan: RoadmapPlan) : RoadmapState()
    data class Error(val message: String) : RoadmapState()
}

class RoadmapPlannerViewModel(application: Application) : AndroidViewModel(application) {

    private val storageManager = SecureStorageManager(application)
    private val apiService = GeminiApiService()

    // Configured JSON parser
    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val _topicInput = MutableStateFlow("")
    val topicInput: StateFlow<String> = _topicInput.asStateFlow()

    private val _roadmapState = MutableStateFlow<RoadmapState>(RoadmapState.Idle)
    val roadmapState: StateFlow<RoadmapState> = _roadmapState.asStateFlow()

    val suggestedTopics = listOf(
        "Dynamic Programming", "Graphs", "Binary Trees", "Backtracking",
        "Sliding Window", "Two Pointers", "Binary Search", "Linked Lists",
        "Stacks & Queues", "Heaps"
    )

    fun onTopicChange(topic: String) {
        _topicInput.value = topic
    }

    fun generateRoadmap() {
        val apiKey = storageManager.getApiKey()
        if (apiKey.isNullOrEmpty()) {
            _roadmapState.value = RoadmapState.Error("API key not configured")
            return
        }

        if (_topicInput.value.isBlank()) {
            _roadmapState.value = RoadmapState.Error("Please enter a topic")
            return
        }

        _roadmapState.value = RoadmapState.Loading

        viewModelScope.launch {
            try {


                val model = apiService.createModel(apiKey)
                val prompt = buildRoadmapPrompt(_topicInput.value)

                val response = model.generateContent(prompt)
                val responseText = response.text ?: throw Exception("Empty response from AI")

                // Direct parsing into your existing data models
                val roadmap = jsonParser.decodeFromString<RoadmapPlan>(responseText)

                _roadmapState.value = RoadmapState.Success(roadmap)

            } catch (e: Exception) {
                _roadmapState.value = RoadmapState.Error(
                    e.message ?: "Failed to generate roadmap"
                )
            }
        }
    }

    fun resetRoadmap() {
        _roadmapState.value = RoadmapState.Idle
    }

    private fun buildRoadmapPrompt(topic: String): String {
        return """
            You are a DSA expert. Generate a pattern-based roadmap for the topic: $topic.
            
            Return the output strictly as a JSON object matching this structure:
            {
              "topic": "$topic",
              "patterns": [
                {
                  "patternName": "Name",
                  "difficulty": "Beginner or Intermediate or Advanced",
                  "description": "Max 10 words",
                  "keyConcepts": ["concept1", "concept2"],
                  "realWorldUse": "Short phrase",
                  "companiesAskThis": "Company Names",
                  "estimatedProblems": "5-10 problems",
                  "mustKnow": true
                }
              ],
              "stats": {
                "totalPatterns": number,
                "mustKnowPatterns": number,
                "estimatedWeeks": "X weeks"
              }
            }
            
            Rules:
            1. Limit to max 6 patterns.
            2. Difficulty must be exactly: "Beginner", "Intermediate", or "Advanced".
            3. Order from Beginner to Advanced.
            4. Do not include any text other than the JSON object.
        """.trimIndent()
    }
}