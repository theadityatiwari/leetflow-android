package com.nativeknights.leetflow.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Dark Color Scheme (Primary theme for LeetFlow)
private val DarkColorScheme = darkColorScheme(
    // Primary colors
    primary = PrimaryBlue,
    onPrimary = TextPrimary,
    primaryContainer = PrimaryBlueDark,
    onPrimaryContainer = InfoBlueText,

    // Secondary colors
    secondary = SuccessGreen,
    onSecondary = TextPrimary,
    secondaryContainer = SuccessGreenBg,
    onSecondaryContainer = SuccessGreenText,

    // Tertiary colors
    tertiary = PurpleText,
    onTertiary = TextPrimary,
    tertiaryContainer = PurpleBg,
    onTertiaryContainer = PurpleText,

    // Background colors
    background = BackgroundPrimary,
    onBackground = TextPrimary,

    // Surface colors
    surface = BackgroundCard,
    onSurface = TextPrimary,
    surfaceVariant = CardElevated,
    onSurfaceVariant = TextSecondary,
    surfaceTint = PrimaryBlue,

    // Container colors
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerLow = SurfaceContainerLow,

    // Error colors
    error = ErrorRed,
    onError = TextPrimary,
    errorContainer = ErrorRedBg,
    onErrorContainer = ErrorRedText,

    // Outline colors
    outline = CardBorder,
    outlineVariant = CardBorder.copy(alpha = 0.5f),

    // Scrim
    scrim = Color.Black.copy(alpha = 0.5f),

    // Inverse colors
    inverseSurface = TextPrimary,
    inverseOnSurface = BackgroundPrimary,
    inversePrimary = PrimaryBlueHover
)

// Light Color Scheme (Optional, for future use)
private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D36),

    secondary = SuccessGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB8F3D5),
    onSecondaryContainer = Color(0xFF00210E),

    tertiary = Color(0xFF7D5260),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD8E4),
    onTertiaryContainer = Color(0xFF31111D),

    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),

    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),

    error = Color(0xFFB3261E),
    onError = Color.White,
    errorContainer = Color(0xFFF9DEDC),
    onErrorContainer = Color(0xFF410E0B),

    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),

    scrim = Color.Black,

    inverseSurface = Color(0xFF313033),
    inverseOnSurface = Color(0xFFF4EFF4),
    inversePrimary = Color(0xFFA0C9FF)
)

@Composable
fun LeetFlowTheme(
    darkTheme: Boolean = true, // ✅ Always dark by default
    dynamicColor: Boolean = false, // ✅ Disabled for consistent branding
    content: @Composable () -> Unit
) {
    // Always use dark theme for LeetFlow
    val colorScheme = DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Set status bar color to match background
            window.statusBarColor = BackgroundPrimary.toArgb()

            // Set navigation bar color
            window.navigationBarColor = BackgroundPrimary.toArgb()

            // Make status bar icons white (for dark background)
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}