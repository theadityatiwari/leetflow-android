package com.nativeknights.leetflow.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class SecureStorageManager(context: Context) {
    
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "leetflow_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_API_KEY = "gemini_api_key"
        private const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
    }

    fun saveApiKey(apiKey: String) {
        sharedPreferences.edit()
            .putString(KEY_API_KEY, apiKey)
            .putBoolean(KEY_ONBOARDING_COMPLETE, true)
            .apply()
    }

    fun getApiKey(): String? {
        return sharedPreferences.getString(KEY_API_KEY, null)
    }

    fun isOnboardingComplete(): Boolean {
        return sharedPreferences.getBoolean(KEY_ONBOARDING_COMPLETE, false)
    }

    fun clearApiKey() {
        sharedPreferences.edit()
            .remove(KEY_API_KEY)
            .putBoolean(KEY_ONBOARDING_COMPLETE, false)
            .apply()
    }

    fun hasApiKey(): Boolean {
        return !getApiKey().isNullOrEmpty()
    }
}