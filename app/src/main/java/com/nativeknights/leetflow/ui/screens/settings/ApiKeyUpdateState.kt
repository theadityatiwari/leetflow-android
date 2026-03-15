package com.nativeknights.leetflow.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nativeknights.leetflow.data.local.SecureStorageManager
import com.nativeknights.leetflow.data.remote.GeminiApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ApiKeyUpdateState {
    object Idle : ApiKeyUpdateState()
    object Validating : ApiKeyUpdateState()
    data class Success(val message: String = "API Key updated successfully!") : ApiKeyUpdateState()
    data class Error(val message: String) : ApiKeyUpdateState()
}

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val storageManager = SecureStorageManager(application)
    private val apiService = GeminiApiService()

    private val _updateState = MutableStateFlow<ApiKeyUpdateState>(ApiKeyUpdateState.Idle)
    val updateState: StateFlow<ApiKeyUpdateState> = _updateState.asStateFlow()

    private val _apiKeyInput = MutableStateFlow("")
    val apiKeyInput: StateFlow<String> = _apiKeyInput.asStateFlow()

    private val _currentApiKey = MutableStateFlow("")
    val currentApiKey: StateFlow<String> = _currentApiKey.asStateFlow()

    init {
        loadCurrentApiKey()
    }

    private fun loadCurrentApiKey() {
        val key = storageManager.getApiKey() ?: ""
        _currentApiKey.value = if (key.isNotEmpty()) {
            maskApiKey(key)
        } else {
            "No API Key configured"
        }
    }

    private fun maskApiKey(key: String): String {
        return if (key.length > 8) {
            "${key.take(4)}...${key.takeLast(4)}"
        } else {
            "****"
        }
    }

    fun onApiKeyChange(newKey: String) {
        _apiKeyInput.value = newKey
        if (_updateState.value is ApiKeyUpdateState.Error) {
            _updateState.value = ApiKeyUpdateState.Idle
        }
    }

    fun validateAndUpdateApiKey() {
        val apiKey = _apiKeyInput.value.trim()

        // Local validation
        if (apiKey.isEmpty()) {
            _updateState.value = ApiKeyUpdateState.Error("API Key cannot be empty")
            return
        }

        if (!apiKey.startsWith("AIza")) {
            _updateState.value = ApiKeyUpdateState.Error("Invalid API Key format. Should start with 'AIza'")
            return
        }

        // Remote validation
        _updateState.value = ApiKeyUpdateState.Validating

        viewModelScope.launch {
            val result = apiService.validateApiKey(apiKey)
            
            result.fold(
                onSuccess = {
                    storageManager.saveApiKey(apiKey)
                    loadCurrentApiKey() // Refresh masked key display
                    _apiKeyInput.value = "" // Clear input
                    _updateState.value = ApiKeyUpdateState.Success()
                },
                onFailure = { error ->
                    val errorMessage = when {
                        error.message?.contains("API key not valid") == true -> 
                            "Invalid API Key. Please check and try again."
                        error.message?.contains("quota", ignoreCase = true) == true ||
                        error.message?.contains("RESOURCE_EXHAUSTED", ignoreCase = true) == true ->
                            "API quota exceeded. Try again later or use a different key."
                        error.message?.contains("network") == true -> 
                            "Network error. Please check your connection."
                        else -> 
                            "Validation failed: ${error.message ?: "Unknown error"}"
                    }
                    _updateState.value = ApiKeyUpdateState.Error(errorMessage)
                }
            )
        }
    }

    fun resetUpdateState() {
        _updateState.value = ApiKeyUpdateState.Idle
        _apiKeyInput.value = ""
    }
}