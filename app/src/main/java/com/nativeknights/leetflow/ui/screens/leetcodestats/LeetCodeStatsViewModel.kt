package com.nativeknights.leetflow.ui.screens.leetcodestats

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nativeknights.leetflow.data.models.GraphQLRequest
import com.nativeknights.leetflow.data.remote.LeetCodeClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LeetCodeStatsViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("leetflow_lc_prefs", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow<LeetCodeStatsUiState>(LeetCodeStatsUiState.Idle)
    val uiState: StateFlow<LeetCodeStatsUiState> = _uiState

    private val _username = MutableStateFlow(prefs.getString("lc_username", "") ?: "")
    val username: StateFlow<String> = _username

    // Auto-fetch on launch if username was saved
    init {
        if (_username.value.isNotBlank()) fetchStats()
    }

    fun onUsernameChange(value: String) {
        _username.value = value
    }

    fun fetchStats() {
        val user = _username.value.trim()
        if (user.isBlank()) return

        prefs.edit().putString("lc_username", user).apply()
        _uiState.value = LeetCodeStatsUiState.Loading

        viewModelScope.launch {
            try {
                // ── Query 1: profile + solved + calendar ─────────────────────
                val statsResp = LeetCodeClient.service.getUserStats(
                    GraphQLRequest(STATS_QUERY, mapOf("username" to user))
                )

                val matchedUser = statsResp.data?.matchedUser
                if (matchedUser == null) {
                    _uiState.value = LeetCodeStatsUiState.Error("User \"$user\" not found on LeetCode")
                    return@launch
                }

                val questionCounts = statsResp.data?.allQuestionsCount ?: emptyList()
                val submissions = matchedUser.submitStats?.acSubmissionNum ?: emptyList()

                fun solvedFor(diff: String) = submissions.find { it.difficulty == diff }?.count ?: 0
                fun subsFor(diff: String) = submissions.find { it.difficulty == diff }?.submissions ?: 0
                fun totalFor(diff: String) = questionCounts.find { it.difficulty == diff }?.count ?: 0

                // ── Query 2: contest (non-fatal if user never participated) ──
                val contestData = try {
                    LeetCodeClient.service.getContestStats(
                        GraphQLRequest(CONTEST_QUERY, mapOf("username" to user))
                    ).data
                } catch (e: Exception) { null }

                val ranking = contestData?.userContestRanking
                val history = contestData?.userContestRankingHistory
                    ?.filter { it.attended && (it.rating ?: 0.0) > 0.0 }
                    ?.reversed() // API returns newest-first; we want chronological
                    ?: emptyList()

                val calMap = parseCalendar(matchedUser.userCalendar?.submissionCalendar ?: "{}")

                _uiState.value = LeetCodeStatsUiState.Success(
                    LeetCodeStats(
                        username = user,
                        ranking = matchedUser.profile?.ranking ?: 0,
                        totalSolved = solvedFor("All"),
                        totalProblems = totalFor("All"),
                        easySolved = solvedFor("Easy"),
                        easyTotal = totalFor("Easy"),
                        mediumSolved = solvedFor("Medium"),
                        mediumTotal = totalFor("Medium"),
                        hardSolved = solvedFor("Hard"),
                        hardTotal = totalFor("Hard"),
                        totalSubmissions = subsFor("All"),
                        streak = matchedUser.userCalendar?.streak ?: 0,
                        totalActiveDays = matchedUser.userCalendar?.totalActiveDays ?: 0,
                        submissionCalendar = calMap,
                        contestRating = ranking?.rating,
                        contestGlobalRank = ranking?.globalRanking,
                        attendedContests = ranking?.attendedContestsCount,
                        topPercentage = ranking?.topPercentage,
                        contestHistory = history
                    )
                )
            } catch (e: Exception) {
                _uiState.value = LeetCodeStatsUiState.Error(
                    when {
                        e.message?.contains("Unable to resolve host", true) == true ->
                            "No internet connection"
                        e.message?.contains("timeout", true) == true ->
                            "Request timed out. Try again."
                        else -> "Failed to fetch stats. Try again."
                    }
                )
            }
        }
    }

    companion object {
        private val STATS_QUERY = """
            query getUserData(${'$'}username: String!) {
              allQuestionsCount { difficulty count }
              matchedUser(username: ${'$'}username) {
                profile { ranking }
                submitStats {
                  acSubmissionNum { difficulty count submissions }
                }
                userCalendar { streak totalActiveDays submissionCalendar }
              }
            }
        """.trimIndent()

        fun parseCalendar(json: String): Map<Long, Int> = try {
            val obj = org.json.JSONObject(json)
            buildMap { obj.keys().forEach { key -> put(key.toLong(), obj.getInt(key)) } }
        } catch (e: Exception) { emptyMap() }

        private val CONTEST_QUERY = """
            query userContestRankingInfo(${'$'}username: String!) {
              userContestRanking(username: ${'$'}username) {
                attendedContestsCount rating globalRanking topPercentage
              }
              userContestRankingHistory(username: ${'$'}username) {
                attended rating ranking contest { title startTime }
              }
            }
        """.trimIndent()
    }
}
