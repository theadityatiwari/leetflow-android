package com.nativeknights.leetflow.ui.screens.codeanalyzer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nativeknights.leetflow.data.local.SecureStorageManager
import com.nativeknights.leetflow.data.models.CodeAnalysis
import com.nativeknights.leetflow.data.remote.GeminiApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AnalysisState {
    object Idle : AnalysisState()
    object Loading : AnalysisState()
    data class Success(val analysis: CodeAnalysis) : AnalysisState()
    data class Error(val message: String) : AnalysisState()
}

class CodeAnalyzerViewModel(application: Application) : AndroidViewModel(application) {

    private val storageManager = SecureStorageManager(application)
    private val apiService = GeminiApiService()

    private val _codeInput = MutableStateFlow("")
    val codeInput: StateFlow<String> = _codeInput.asStateFlow()

    private val _analysisState = MutableStateFlow<AnalysisState>(AnalysisState.Idle)
    val analysisState: StateFlow<AnalysisState> = _analysisState.asStateFlow()

    fun onCodeInputChange(code: String) {
        _codeInput.value = code
    }

    fun analyzeSolution() {
        val apiKey = storageManager.getApiKey()
        if (apiKey.isNullOrEmpty()) {
            _analysisState.value = AnalysisState.Error("API key not configured")
            return
        }

        if (_codeInput.value.isBlank()) {
            _analysisState.value = AnalysisState.Error("Please paste your code first")
            return
        }

        _analysisState.value = AnalysisState.Loading

        viewModelScope.launch {
            try {
                val model = apiService.createModel(apiKey)

                val prompt = buildAnalysisPrompt(_codeInput.value)

                val response = model.generateContent(prompt)
                val analysis = parseAnalysis(response.text ?: "")

                _analysisState.value = AnalysisState.Success(analysis)

            } catch (e: Exception) {
                _analysisState.value = AnalysisState.Error(
                    e.message ?: "Failed to analyze code"
                )
            }
        }
    }

    fun resetAnalysis() {
        _analysisState.value = AnalysisState.Idle
    }

    private fun buildAnalysisPrompt(code: String): String {
        return """
You are a code quality expert. Analyze this DSA solution and provide feedback.

CODE:Respond ONLY in this exact JSON format (no markdown, no extra text):
{
  "isOptimal": true/false,
  "feedback": "Brief summary of solution quality",
  "timeComplexity": "O(n)",
  "spaceComplexity": "O(1)",
  "pattern": "Two Pointers / DP / Sliding Window / etc",
  "optimalApproach": "If not optimal, suggest better approach. If optimal, leave empty string",
  "refactoredCode": "Cleaner/optimized version of the code with comments"
}

RULES:
- feedback: 1-2 sentences (concise)
- pattern: Single primary pattern name
- optimalApproach: Only if isOptimal is false, otherwise ""
- refactoredCode: Well-formatted, commented version
- Keep complexities in Big-O notation
        """.trimIndent()
    }

    private fun parseAnalysis(response: String): CodeAnalysis {
        return try {
            val cleanJson = response
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val isOptimalRegex = """"isOptimal":\s*(true|false)""".toRegex()
            val feedbackRegex = """"feedback":\s*"([^"]+)"""".toRegex()
            val timeRegex = """"timeComplexity":\s*"([^"]+)"""".toRegex()
            val spaceRegex = """"spaceComplexity":\s*"([^"]+)"""".toRegex()
            val patternRegex = """"pattern":\s*"([^"]+)"""".toRegex()
            val optimalRegex = """"optimalApproach":\s*"([^"]*)"""".toRegex()
            val codeRegex = """"refactoredCode":\s*"(.*?)"\s*}""".toRegex(RegexOption.DOT_MATCHES_ALL)

            val isOptimal = isOptimalRegex.find(cleanJson)?.groupValues?.get(1) == "true"
            val feedback = feedbackRegex.find(cleanJson)?.groupValues?.get(1) ?: "Analysis completed"
            val timeComplexity = timeRegex.find(cleanJson)?.groupValues?.get(1) ?: "O(n)"
            val spaceComplexity = spaceRegex.find(cleanJson)?.groupValues?.get(1) ?: "O(1)"
            val pattern = patternRegex.find(cleanJson)?.groupValues?.get(1) ?: "General"
            val optimalApproach = optimalRegex.find(cleanJson)?.groupValues?.get(1)?.takeIf { it.isNotBlank() }
            val refactoredCode = codeRegex.find(cleanJson)?.groupValues?.get(1)
                ?.replace("\\n", "\n")
                ?.replace("\\t", "    ")
                ?.trim() ?: _codeInput.value

            CodeAnalysis(
                isOptimal = isOptimal,
                feedback = feedback,
                timeComplexity = timeComplexity,
                spaceComplexity = spaceComplexity,
                pattern = pattern,
                optimalApproach = optimalApproach,
                refactoredCode = refactoredCode
            )
        } catch (e: Exception) {
            CodeAnalysis(
                isOptimal = false,
                feedback = "Failed to parse analysis. Please try again.",
                timeComplexity = "Unknown",
                spaceComplexity = "Unknown",
                pattern = "Unknown",
                optimalApproach = null,
                refactoredCode = _codeInput.value
            )
        }
    }
}