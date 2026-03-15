package com.nativeknights.leetflow.data.models// ComplexityModels.kt
import kotlinx.serialization.Serializable

@Serializable
data class BlitzQuestion(
    val codeSnippet: String,
    val options: List<String>,
    val correctTime: String,
    val correctSpace: String,
    val explanation: String
)

// BlindModels.kt
@Serializable
data class BlindProblem(
    val title: String,
    val difficulty: String,
    val description: String,
    val leetCodeUrl: String
)