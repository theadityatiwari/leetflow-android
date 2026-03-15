package com.nativeknights.leetflow.ui.screens.onboarding

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nativeknights.leetflow.data.local.SecureStorageManager
import com.nativeknights.leetflow.data.remote.GeminiApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ApiKeyValidationState {
    object Idle : ApiKeyValidationState()
    object Validating : ApiKeyValidationState()
    data class Success(val message: String = "API Key validated successfully!") : ApiKeyValidationState()
    data class Error(val message: String) : ApiKeyValidationState()
}

class OnboardingViewModel(application: Application) : AndroidViewModel(application) {
    
    private val storageManager = SecureStorageManager(application)
    private val apiService = GeminiApiService()

    private val _validationState = MutableStateFlow<ApiKeyValidationState>(ApiKeyValidationState.Idle)
    val validationState: StateFlow<ApiKeyValidationState> = _validationState.asStateFlow()

    private val _apiKeyInput = MutableStateFlow("")
    val apiKeyInput: StateFlow<String> = _apiKeyInput.asStateFlow()

    fun onApiKeyChange(newKey: String) {
        _apiKeyInput.value = newKey
        if (_validationState.value is ApiKeyValidationState.Error) {
            _validationState.value = ApiKeyValidationState.Idle
        }
    }

    fun validateAndSaveApiKey() {
        val apiKey = _apiKeyInput.value.trim()

        // Local validation
        if (apiKey.isEmpty()) {
            _validationState.value = ApiKeyValidationState.Error("API Key cannot be empty")
            return
        }

        if (!apiKey.startsWith("AIza")) {
            _validationState.value = ApiKeyValidationState.Error("Invalid API Key format. Should start with 'AIza'")
            return
        }

        // Remote validation
        _validationState.value = ApiKeyValidationState.Validating

        viewModelScope.launch {
            val result = apiService.validateApiKey(apiKey)
            
            result.fold(
                onSuccess = {
                    storageManager.saveApiKey(apiKey)
                    _validationState.value = ApiKeyValidationState.Success()
                },
                onFailure = { error ->
                    val errorMessage = when {
                        error.message?.contains("API key not valid") == true -> 
                            "Invalid API Key. Please check and try again."
                        error.message?.contains("network") == true -> 
                            "Network error. Please check your connection."
                        else -> 
                            "Validation failed: ${error.message ?: "Unknown error"}"
                    }
                    _validationState.value = ApiKeyValidationState.Error(errorMessage)
                }
            )
        }
    }

    fun resetValidationState() {
        _validationState.value = ApiKeyValidationState.Idle
    }
}