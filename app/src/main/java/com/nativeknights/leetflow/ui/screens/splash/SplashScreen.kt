package com.nativeknights.leetflow.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.nativeknights.leetflow.R
import com.nativeknights.leetflow.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onNavigateToOnboarding: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    hasApiKey: Boolean
) {
    // One-shot entrance animatables
    val logoScale = remember { Animatable(0.4f) }
    val logoAlpha = remember { Animatable(0f) }
    val titleOffsetY = remember { Animatable(24f) }
    val titleAlpha = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val dotsAlpha = remember { Animatable(0f) }

    // Staggered entrance sequence
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        // Logo pops in with spring
        scope.launch {
            launch {
                logoScale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
            launch {
                logoAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
                )
            }
        }

        // Title slides up after logo settles
        delay(200)
        scope.launch {
            launch {
                titleOffsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                )
            }
            launch {
                titleAlpha.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
                )
            }
        }

        // Subtitle fades in
        delay(350)
        subtitleAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 350, easing = LinearEasing)
        )

        // Dots fade in last
        delay(100)
        dotsAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300, easing = LinearEasing)
        )

        // Navigate after 2 seconds total
        delay(850)
        if (hasApiKey) onNavigateToDashboard() else onNavigateToOnboarding()
    }

    // Only the dots pulse — single infinite transition
    val infiniteTransition = rememberInfiniteTransition(label = "dots")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPrimary),
        contentAlignment = Alignment.Center
    ) {
        // Subtle radial glow behind logo
        Box(
            modifier = Modifier
                .size(280.dp)
                .alpha(0.14f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(PrimaryBlue, Color.Transparent)
                    )
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo with spring entrance
            Icon(
                painter = painterResource(id = R.drawable.ic_logo_transparent),
                contentDescription = "LeetFlow Logo",
                modifier = Modifier
                    .scale(logoScale.value)
                    .alpha(logoAlpha.value),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(28.dp))

            // App name slides up
            Text(
                text = "LeetFlow",
                fontSize = 52.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary,
                letterSpacing = 1.5.sp,
                modifier = Modifier
                    .offset(y = titleOffsetY.value.dp)
                    .alpha(titleAlpha.value)
            )

            // Thin blue accent line
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(2.dp)
                    .alpha(subtitleAlpha.value)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, PrimaryBlue, Color.Transparent)
                        )
                    )
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle fades in
            Text(
                text = "DSA Command Center",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = TextTertiary,
                letterSpacing = 2.sp,
                modifier = Modifier.alpha(subtitleAlpha.value)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Pulsing dots — only infinite animation
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.alpha(dotsAlpha.value)
            ) {
                repeat(3) { index ->
                    val dotAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.2f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = 500,
                                delayMillis = index * 170,
                                easing = FastOutSlowInEasing
                            ),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot_$index"
                    )
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .alpha(dotAlpha)
                            .background(color = PrimaryBlue, shape = CircleShape)
                    )
                }
            }
        }
    }
}
