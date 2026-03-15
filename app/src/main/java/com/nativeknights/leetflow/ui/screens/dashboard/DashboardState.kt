package com.nativeknights.leetflow.ui.screens.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
    val notesCount: Int = 0
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val storageManager = SecureStorageManager(application)
    private val database = LeetFlowDatabase.getInstance(application)

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        val hasKey = storageManager.hasApiKey()
        val greeting = getGreeting()

        // Load notes count from database
        viewModelScope.launch {
            database.recallNoteDao().getNotesCount().collectLatest { count ->
                _state.value = _state.value.copy(
                    userName = "Champion",
                    hasApiKey = hasKey,
                    greeting = greeting,
                    totalSolved = 0, // TODO: Fetch from LeetCode API
                    progressPercent = 0, // TODO: Calculate from user stats
                    notesCount = count
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
}