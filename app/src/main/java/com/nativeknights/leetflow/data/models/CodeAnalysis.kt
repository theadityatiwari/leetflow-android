package com.nativeknights.leetflow.data.models

data class CodeAnalysis(
    val isOptimal: Boolean,
    val feedback: String,
    val timeComplexity: String,
    val spaceComplexity: String,
    val pattern: String,
    val optimalApproach: String?,
    val refactoredCode: String
)
