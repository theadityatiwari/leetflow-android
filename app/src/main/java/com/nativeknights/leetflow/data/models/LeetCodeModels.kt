package com.nativeknights.leetflow.data.models

// ── GraphQL Request ──────────────────────────────────────────────────────────
data class GraphQLRequest(
    val query: String,
    val variables: Map<String, String>
)

// ── Stats Query Response ─────────────────────────────────────────────────────
data class StatsResponse(val data: StatsData?)

data class StatsData(
    val allQuestionsCount: List<QuestionCount>?,
    val matchedUser: MatchedUser?
)

data class QuestionCount(val difficulty: String, val count: Int)

data class UserBadge(
    val id: String?,
    val displayName: String?,
    val icon: String?,          // relative path e.g. "/static/images/badges/..."
    val creationDate: String?   // "YYYY-MM-DD"
)

data class MatchedUser(
    val profile: UserProfile?,
    val submitStats: SubmitStats?,
    val userCalendar: UserCalendar?,
    val badges: List<UserBadge>?
)

data class UserProfile(val ranking: Int?)

data class SubmitStats(val acSubmissionNum: List<DifficultySubmission>?)

data class DifficultySubmission(
    val difficulty: String,
    val count: Int,
    val submissions: Int
)

data class UserCalendar(
    val streak: Int?,
    val totalActiveDays: Int?,
    val submissionCalendar: String?   // JSON string: {"timestamp": count, ...}
)

// ── Contest Query Response ───────────────────────────────────────────────────
data class ContestResponse(val data: ContestData?)

data class ContestData(
    val userContestRanking: ContestRanking?,
    val userContestRankingHistory: List<ContestHistory>?
)

data class ContestRanking(
    val attendedContestsCount: Int?,
    val rating: Double?,
    val globalRanking: Int?,
    val topPercentage: Double?
)

data class ContestHistory(
    val attended: Boolean,
    val rating: Double?,
    val ranking: Int?,
    val contest: ContestInfo?
)

data class ContestInfo(val title: String?, val startTime: Long?)
