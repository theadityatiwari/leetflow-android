package com.nativeknights.leetflow.data.remote

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiApiService {

    companion object {
        // ✅ Using Gemini 2.0 Flash model
        private const val MODEL_NAME = "gemini-2.5-flash"
    }

    suspend fun validateApiKey(apiKey: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val generativeModel = GenerativeModel(
                modelName = MODEL_NAME,
                apiKey = apiKey,
                generationConfig = generationConfig {
                    temperature = 0.1f
                    maxOutputTokens = 50
                }
            )

            val response = generativeModel.generateContent("Say OK")
            val responseText = response.text?.trim() ?: ""

            if (responseText.isNotEmpty()) {
                Result.success(true)
            } else {
                Result.failure(Exception("No response from API"))
            }

        } catch (e: Exception) {
            val errorMessage = parseGeminiError(e)
            Result.failure(Exception(errorMessage))
        }
    }

    private fun parseGeminiError(exception: Exception): String {
        val message = exception.message ?: return "Unknown error occurred"

        return when {
            message.contains("API key not valid", ignoreCase = true) ||
                    message.contains("invalid api key", ignoreCase = true) ->
                "Invalid API Key. Get a key from ai.google.dev"

            message.contains("404", ignoreCase = true) ||
                    message.contains("not found", ignoreCase = true) ->
                "Model unavailable. Please update the app or try again."

            message.contains("PERMISSION_DENIED", ignoreCase = true) ->
                "Enable 'Generative Language API' for this key in Google AI Studio."

            message.contains("RESOURCE_EXHAUSTED", ignoreCase = true) ||
                    message.contains("quota", ignoreCase = true) ->
                "API quota exceeded. Try again later or upgrade your plan."

            message.contains("network", ignoreCase = true) ||
                    message.contains("timeout", ignoreCase = true) ||
                    message.contains("unable to resolve host", ignoreCase = true) ->
                "Network error. Check your internet connection."

            else -> "Validation failed: ${message.take(80)}"
        }
    }

    fun createModel(apiKey: String): GenerativeModel {
        return GenerativeModel(
            modelName = MODEL_NAME,
            apiKey = apiKey,
            generationConfig = generationConfig {
                temperature = 0.2f // Lower temperature is better for structured data
                maxOutputTokens = 2048
                responseMimeType = "application/json" //
            }
        )
    }
}