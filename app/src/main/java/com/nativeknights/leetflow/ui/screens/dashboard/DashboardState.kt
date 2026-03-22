package com.nativeknights.leetflow.ui.screens.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nativeknights.leetflow.data.local.AppPreferences
import com.nativeknights.leetflow.data.local.LeetFlowDatabase
import com.nativeknights.leetflow.data.local.SecureStorageManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class DashboardState(
    val userName: String = "User",
    val hasApiKey: Boolean = true,
    val greeting: String = "Good Morning",
    val totalSolved: Int = 0,
    val progressPercent: Int = 0,
    val notesCount: Int = 0,
    val showRateDialog: Boolean = false,
    // LeetCode stats snapshot (cached from last Stats screen visit)
    val streak: Int = 0,
    val lcSolved: Int = 0,
    val lcUsername: String = "",
    val hasStatsCache: Boolean = false
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val storageManager = SecureStorageManager(application)
    private val database = LeetFlowDatabase.getInstance(application)
    private val appPreferences = AppPreferences(application)

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadDashboardData()
        checkRateDialog()
    }

    private fun loadDashboardData() {
        val hasKey = storageManager.hasApiKey()
        val greeting = getGreeting()
        val cachedStreak = appPreferences.lastKnownStreak
        val cachedSolved = appPreferences.lastKnownSolved
        val cachedUsername = appPreferences.lastKnownUsername

        // Load notes count from database
        viewModelScope.launch {
            database.recallNoteDao().getNotesCount().collectLatest { count ->
                _state.value = _state.value.copy(
                    userName = "Champion",
                    hasApiKey = hasKey,
                    greeting = greeting,
                    totalSolved = 0,
                    progressPercent = 0,
                    notesCount = count,
                    streak = cachedStreak,
                    lcSolved = cachedSolved,
                    lcUsername = cachedUsername,
                    hasStatsCache = cachedUsername.isNotEmpty()
                )
            }
        }
    }

    private fun getGreeting(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 0..11 -> "Good Morning"
            in 12..16 -> "Good Afternoon"
            in 17..20 -> "Good Evening"
            else -> "Good Night"
        }
    }

    fun checkRateDialog() {
        _state.value = _state.value.copy(showRateDialog = appPreferences.shouldShowRateDialog())
    }

    fun refreshStatsCache() {
        val username = appPreferences.lastKnownUsername
        _state.value = _state.value.copy(
            streak = appPreferences.lastKnownStreak,
            lcSolved = appPreferences.lastKnownSolved,
            lcUsername = username,
            hasStatsCache = username.isNotEmpty()
        )
    }

    fun onFeatureNavigated() {
        appPreferences.incrementFeatureUseCount()
    }

    fun onRateLater() {
        appPreferences.rateLaterTimestamp = System.currentTimeMillis()
        _state.value = _state.value.copy(showRateDialog = false)
    }

    fun onRateNow() {
        appPreferences.hasRated = true
        _state.value = _state.value.copy(showRateDialog = false)
    }
}