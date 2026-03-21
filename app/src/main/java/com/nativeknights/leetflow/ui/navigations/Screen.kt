package com.nativeknights.leetflow.ui.navigations

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Dashboard : Screen("dashboard")
    object ProblemSelector : Screen("problem_selector") // ✅ Add this
    object FlashCard : Screen("flashcards") // ✅ Add this
    object  CodeAnalyzer :Screen("code_analyzer")  // ✅ Add this
    object  RoadmapPlanner : Screen("roadmap_planner") // ✅ Add this
    // ✅ Add these new features
    object ComplexityBlitz : Screen("complexity_blitz")
    object BlindProblem : Screen("blind_problem")
    object Settings : Screen("settings")
    object Developer : Screen("developer")
    object LeetCodeStats : Screen("leetcode_stats")
}