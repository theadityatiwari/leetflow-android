package com.nativeknights.leetflow.ui.screens.dashboard

/**
 * Pure function — no Android dependencies, no clock reading.
 * Accepts [hour] (0–23) so it can be tested with any value.
 */
fun getGreeting(hour: Int): String {
    return when (hour) {
        in 0..11  -> "Good Morning"
        in 12..16 -> "Good Afternoon"
        in 17..20 -> "Good Evening"
        else      -> "Good Night"
    }
}
