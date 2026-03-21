package com.nativeknights.leetflow.ui.navigations

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.nativeknights.leetflow.ui.screens.blindproblem.BlindProblemScreen
import com.nativeknights.leetflow.ui.screens.codeanalyzer.CodeAnalyzerScreen
import com.nativeknights.leetflow.ui.screens.complexityblitz.ComplexityBlitzScreen
import com.nativeknights.leetflow.ui.screens.dashboard.DashboardScreen
import com.nativeknights.leetflow.ui.screens.flashcards.FlashcardsScreen
import com.nativeknights.leetflow.ui.screens.onboarding.OnboardingScreen
import com.nativeknights.leetflow.ui.screens.problemselector.ProblemSelectorScreen
import com.nativeknights.leetflow.ui.screens.roadmapplanner.RoadmapPlannerScreen
import com.nativeknights.leetflow.ui.screens.developer.DeveloperScreen
import com.nativeknights.leetflow.ui.screens.settings.SettingsScreen
import com.nativeknights.leetflow.ui.screens.splash.SplashScreen

@Composable
fun NavigationGraph(
    navController: NavHostController,
    hasApiKey: Boolean
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                hasApiKey = hasApiKey,
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.ProblemSelector.route) {
            ProblemSelectorScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.FlashCard.route) {
            FlashcardsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(navController)
        }

        // Add this composable
        composable(Screen.CodeAnalyzer.route) {
            CodeAnalyzerScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.RoadmapPlanner.route) {
            RoadmapPlannerScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ✅ Complexity Blitz Screen
        composable(Screen.ComplexityBlitz.route) {
            ComplexityBlitzScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ✅ Blind Problem Screen
        composable(Screen.BlindProblem.route) {
            BlindProblemScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDeveloper = { navController.navigate(Screen.Developer.route) }
            )
        }

        composable(Screen.Developer.route) {
            DeveloperScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}