package com.nativeknights.leetflow.ui.screens.leetcodestats

import com.nativeknights.leetflow.data.models.ContestHistory

data class LeetCodeStats(
    val username: String,
    val ranking: Int,
    // Solved counts
    val totalSolved: Int,
    val totalProblems: Int,
    val easySolved: Int,
    val easyTotal: Int,
    val mediumSolved: Int,
    val mediumTotal: Int,
    val hardSolved: Int,
    val hardTotal: Int,
    // Submissions
    val totalSubmissions: Int,
    // Activity
    val streak: Int,
    val totalActiveDays: Int,
    val submissionCalendar: Map<Long, Int>, // UTC unix-seconds -> submission count
    // Contest
    val contestRating: Double?,
    val contestGlobalRank: Int?,
    val attendedContests: Int?,
    val topPercentage: Double?,
    val contestHistory: List<ContestHistory>
)

sealed class LeetCodeStatsUiState {
    object Idle : LeetCodeStatsUiState()
    object Loading : LeetCodeStatsUiState()
    data class Success(val stats: LeetCodeStats) : LeetCodeStatsUiState()
    data class Error(val message: String) : LeetCodeStatsUiState()
}
