package com.nativeknights.leetflow.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nativeknights.leetflow.ui.theme.*
import com.nativeknights.leetflow.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    hasApiKey: Boolean
) {
    // Rocket scale animation
    val infiniteTransition = rememberInfiniteTransition(label = "splash_animation")
    val rocketScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rocket_scale"
    )

    // Fade in animation for content
    val fadeAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "fade_alpha"
    )

    // Subtitle slide animation
    val slideOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "slide_offset"
    )

    // Navigation logic
    LaunchedEffect(Unit) {
        delay(2500) // Show splash for 2.5 seconds
        if (hasApiKey) {
            onNavigateToDashboard()
        } else {
            onNavigateToOnboarding()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BackgroundPrimary,
                        BackgroundCard,
                        BackgroundPrimary
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Background decorative elements
        Box(
            modifier = Modifier
                .size(300.dp)
                .alpha(0.05f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            PrimaryBlue,
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Rocket Emoji with animation
            Icon(
                painter = painterResource(id = R.drawable.ic_logo_transparent),
                contentDescription = "Rocket",
                modifier = Modifier
                    .scale(rocketScale)
                    .offset(y = (-slideOffset).dp),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(4.dp))

            // App Name
            Text(
                text = "LeetFlow",
                fontSize = 56.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                modifier = Modifier.alpha(fadeAlpha),
                letterSpacing = 2.sp
            )

            // Subtitle
            Text(
                text = "DSA Command Center",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = TextTertiary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Loading indicator dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(3) { index ->
                    val dotAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = 600,
                                delayMillis = index * 200,
                                easing = LinearEasing
                            ),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot_alpha_$index"
                    )

                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .alpha(dotAlpha)
                            .background(
                                color = PrimaryBlue,
                                shape = androidx.compose.foundation.shape.CircleShape
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Bottom tagline
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Powered by",
                    fontSize = 12.sp,
                    color = TextDisabled,
                    fontWeight = FontWeight.Light
                )
                Text(
                    text = "AI",
                    fontSize = 12.sp,
                    color = SuccessGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "⚡",
                    fontSize = 14.sp
                )
            }
        }
    }
}