package com.nativeknights.leetflow.data.models

data class ProblemRecommendation(
    val title: String,
    val topic: String,
    val difficulty: String, // "Easy", "Medium", "Hard"
    val description: String,
    val reason: String,
    val skillBuilder: String,
    val url: String = ""
)