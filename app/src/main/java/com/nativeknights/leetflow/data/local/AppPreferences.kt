package com.nativeknights.leetflow.data.local

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("leetflow_app_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_FEATURE_USE_COUNT = "feature_use_count"
        private const val KEY_RATE_LATER_TIMESTAMP = "rate_later_timestamp"
        private const val KEY_HAS_RATED = "has_rated"
        private const val KEY_LAST_STREAK = "last_streak"
        private const val KEY_LAST_SOLVED = "last_solved"
        private const val KEY_LAST_USERNAME = "last_lc_username"

        private const val MIN_FEATURE_USES = 3
        private const val RATE_SNOOZE_MS = 3 * 24 * 60 * 60 * 1000L // 3 days
    }

    private var featureUseCount: Int
        get() = prefs.getInt(KEY_FEATURE_USE_COUNT, 0)
        set(value) = prefs.edit().putInt(KEY_FEATURE_USE_COUNT, value).apply()

    var rateLaterTimestamp: Long
        get() = prefs.getLong(KEY_RATE_LATER_TIMESTAMP, 0L)
        set(value) = prefs.edit().putLong(KEY_RATE_LATER_TIMESTAMP, value).apply()

    var hasRated: Boolean
        get() = prefs.getBoolean(KEY_HAS_RATED, false)
        set(value) = prefs.edit().putBoolean(KEY_HAS_RATED, value).apply()

    var lastKnownStreak: Int
        get() = prefs.getInt(KEY_LAST_STREAK, 0)
        set(value) = prefs.edit().putInt(KEY_LAST_STREAK, value).apply()

    var lastKnownSolved: Int
        get() = prefs.getInt(KEY_LAST_SOLVED, 0)
        set(value) = prefs.edit().putInt(KEY_LAST_SOLVED, value).apply()

    var lastKnownUsername: String
        get() = prefs.getString(KEY_LAST_USERNAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LAST_USERNAME, value).apply()

    fun incrementFeatureUseCount() {
        featureUseCount += 1
    }

    fun shouldShowRateDialog(): Boolean {
        if (hasRated) return false
        if (featureUseCount < MIN_FEATURE_USES) return false
        val laterTs = rateLaterTimestamp
        if (laterTs == 0L) return true
        return System.currentTimeMillis() - laterTs >= RATE_SNOOZE_MS
    }
}
