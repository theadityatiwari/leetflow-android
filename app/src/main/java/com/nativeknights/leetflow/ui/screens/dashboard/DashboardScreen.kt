package com.nativeknights.leetflow.ui.screens.dashboard

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.nativeknights.leetflow.R
import com.nativeknights.leetflow.ui.navigations.Screen
import com.nativeknights.leetflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    // Entrance animation
    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { contentVisible = true }
    val contentAlpha by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 450),
        label = "content_alpha"
    )

    Scaffold(
        topBar = {
            DashboardHeader(
                greeting = state.greeting,
                notesCount = state.notesCount,
                hasApiKey = state.hasApiKey,
                onSettingsClick = { navController.navigate(Screen.Settings.route) }
            )
        },
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .alpha(contentAlpha)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            // Hero
            HeroCard(
                onClick = { navController.navigate(Screen.ProblemSelector.route) },
                enabled = state.hasApiKey
            )

            // Training section
            SectionHeader(title = "TRAINING", accentColor = Color(0xFF22D3EE))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ComplexityBlitzWidget(
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.ComplexityBlitz.route) },
                    enabled = state.hasApiKey
                )
                BlindProblemWidget(
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.BlindProblem.route) },
                    enabled = state.hasApiKey
                )
            }

            // AI Tools section
            SectionHeader(title = "AI TOOLS", accentColor = PrimaryBlue)

            ToolCard(
                icon = Icons.Default.LocationOn,
                title = "Topic Roadmap",
                subtitle = "Master all important patterns",
                accentColor = Color(0xFFF9A8D4),
                accentBg = Color(0xFF831843),
                onClick = { navController.navigate(Screen.RoadmapPlanner.route) },
                enabled = state.hasApiKey
            )

            ToolCard(
                icon = Icons.Default.Create,
                title = "Code Analyzer",
                subtitle = "Review complexity & quality",
                accentColor = Color(0xFF60A5FA),
                accentBg = Color(0xFF1E3A8A),
                onClick = { navController.navigate(Screen.CodeAnalyzer.route) },
                enabled = state.hasApiKey
            )

            ToolCard(
                icon = Icons.Default.Star,
                title = "Recall Notes",
                subtitle = if (state.notesCount > 0) "${state.notesCount} flashcards saved" else "Build your flashcard library",
                accentColor = Color(0xFFFBBF24),
                accentBg = Color(0xFF78350F),
                onClick = { navController.navigate(Screen.FlashCard.route) },
                enabled = true
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────
// HEADER
// ─────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardHeader(
    greeting: String,
    notesCount: Int,
    hasApiKey: Boolean,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundCard, BackgroundPrimary)
                )
            )
    ) {
        TopAppBar(
            title = {
                Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                    Text(
                        text = "$greeting 👋",
                        fontSize = 12.sp,
                        color = TextTertiary,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = 0.3.sp
                    )
                    Text(
                        text = "Champion",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        letterSpacing = (-0.5).sp
                    )
                }
            },
            actions = {
                // AI status pill
                if (hasApiKey) {
                    AiActiveBadge()
                    Spacer(modifier = Modifier.width(4.dp))
                }
                // Notes pill
                if (notesCount > 0) {
                    NotesPill(count = notesCount)
                    Spacer(modifier = Modifier.width(2.dp))
                }
                IconButton(onClick = onSettingsClick) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(CardElevated, CircleShape)
                            .border(1.dp, CardBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
    }
}

@Composable
private fun AiActiveBadge() {
    val infiniteTransition = rememberInfiniteTransition(label = "ai_pulse")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "dot_alpha"
    )
    Surface(
        color = SuccessGreen.copy(alpha = 0.15f),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.border(1.dp, SuccessGreenText.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .alpha(dotAlpha)
                    .background(SuccessGreenText, CircleShape)
            )
            Text("AI", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SuccessGreenText)
        }
    }
}

@Composable
private fun NotesPill(count: Int) {
    Surface(
        color = CardElevated,
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.border(1.dp, CardBorder, RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("📝", fontSize = 11.sp)
            Text(
                "$count",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )
        }
    }
}

// ─────────────────────────────────────────────────────────
// SECTION HEADER
// ─────────────────────────────────────────────────────────

@Composable
fun SectionHeader(title: String, accentColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 2.dp, start = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .width(18.dp)
                .height(2.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(accentColor, Color.Transparent)
                    ),
                    shape = RoundedCornerShape(1.dp)
                )
        )
        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor.copy(alpha = 0.75f),
            letterSpacing = 1.8.sp
        )
    }
}

// ─────────────────────────────────────────────────────────
// HERO CARD
// ─────────────────────────────────────────────────────────

@Composable
private fun HeroCard(onClick: () -> Unit, enabled: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "hero")
    val arrowOffset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 5f,
        animationSpec = infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "arrow"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.08f, targetValue = 0.16f,
        animationSpec = infiniteRepeatable(tween(1800), RepeatMode.Reverse),
        label = "glow"
    )

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(215.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(24.dp),
        enabled = enabled
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF1E3A8A).copy(alpha = if (enabled) 1f else 0.4f),
                            Color(0xFF1E40AF).copy(alpha = if (enabled) 0.6f else 0.2f),
                            BackgroundCard
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            PrimaryBlue.copy(alpha = if (enabled) 0.5f else 0.15f),
                            CardBorder.copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            // Animated glow orb
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 50.dp, y = (-50).dp)
                    .background(PrimaryBlue.copy(alpha = glowAlpha), CircleShape)
            )
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .align(Alignment.BottomStart)
                    .offset(x = (-25).dp, y = 25.dp)
                    .background(PrimaryBlue.copy(alpha = 0.07f), CircleShape)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // AI badge
                    Surface(
                        color = SuccessGreen.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.border(
                            1.dp,
                            SuccessGreenText.copy(alpha = 0.3f),
                            RoundedCornerShape(20.dp)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .background(SuccessGreenText, CircleShape)
                            )
                            Text(
                                "AI POWERED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SuccessGreenText,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    Text(
                        text = "Decision Fatigue\nRemover",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (enabled) TextPrimary else TextPrimary.copy(alpha = 0.4f),
                        lineHeight = 28.sp,
                        letterSpacing = (-0.3).sp
                    )
                    Text(
                        text = "Don't know what to solve? Let AI pick the right problem based on your gaps.",
                        fontSize = 13.sp,
                        color = if (enabled) TextSecondary else TextSecondary.copy(alpha = 0.4f),
                        lineHeight = 19.sp
                    )
                }

                if (enabled) {
                    Surface(
                        color = PrimaryBlue,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Get Recommendation",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                null,
                                tint = TextPrimary,
                                modifier = Modifier
                                    .size(14.dp)
                                    .offset(x = arrowOffset.dp)
                            )
                        }
                    }
                }
                else {
                    Surface(
                        color = ErrorRedBg.copy(alpha = 0.35f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.border(1.dp, ErrorRed.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
                    ) {
                        Text(
                            "⚠️  API Key Required",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = ErrorRedText
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// TRAINING WIDGETS
// ─────────────────────────────────────────────────────────

@Composable
private fun ComplexityBlitzWidget(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean
) {
    val cyanColor = Color(0xFF22D3EE)
    val alpha = if (enabled) 1f else 0.4f

    Card(
        onClick = onClick,
        modifier = modifier.height(165.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(20.dp),
        enabled = enabled
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0C4A6E).copy(alpha = alpha * 0.85f),
                            BackgroundCard
                        )
                    )
                )
                .border(
                    1.dp,
                    Color(0xFF0891B2).copy(alpha = alpha * 0.35f),
                    RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            // Decorative background icon
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 18.dp, y = 18.dp),
                tint = cyanColor.copy(alpha = 0.09f)
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(cyanColor.copy(alpha = alpha), CircleShape)
                        )
                        Text(
                            "TRAINING",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = cyanColor.copy(alpha = alpha),
                            letterSpacing = 1.2.sp
                        )
                    }
                    Text(
                        "Complexity\nBlitz",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary.copy(alpha = alpha),
                        lineHeight = 23.sp,
                        letterSpacing = (-0.3).sp
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = cyanColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            "Big O Quiz",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = cyanColor.copy(alpha = alpha)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            "Tap to start",
                            fontSize = 10.sp,
                            color = TextDisabled.copy(alpha = alpha)
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            null,
                            modifier = Modifier.size(10.dp),
                            tint = TextDisabled.copy(alpha = alpha)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BlindProblemWidget(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean
) {
    val purpleColor = Color(0xFFA78BFA)
    val alpha = if (enabled) 1f else 0.4f

    Card(
        onClick = onClick,
        modifier = modifier.height(165.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(20.dp),
        enabled = enabled
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF2E1065).copy(alpha = alpha * 0.85f),
                            BackgroundCard
                        )
                    )
                )
                .border(
                    1.dp,
                    Color(0xFF7C3AED).copy(alpha = alpha * 0.35f),
                    RoundedCornerShape(20.dp)
                )
                .padding(16.dp)
        ) {
            // Decorative background icon
            Icon(
                painter = painterResource(id = R.drawable.ic_visibility_off_v2),
                contentDescription = null,
                modifier = Modifier
                    .size(70.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = 18.dp, y = 18.dp),
                tint = purpleColor.copy(alpha = 0.09f)
            )

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .background(purpleColor.copy(alpha = alpha), CircleShape)
                        )
                        Text(
                            "CHALLENGE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = purpleColor.copy(alpha = alpha),
                            letterSpacing = 1.2.sp
                        )
                    }
                    Text(
                        "Blind\nProblem",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary.copy(alpha = alpha),
                        lineHeight = 23.sp,
                        letterSpacing = (-0.3).sp
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        color = purpleColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            "No Hints Mode",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = purpleColor.copy(alpha = alpha)
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            "Tap to start",
                            fontSize = 10.sp,
                            color = TextDisabled.copy(alpha = alpha)
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            null,
                            modifier = Modifier.size(10.dp),
                            tint = TextDisabled.copy(alpha = alpha)
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// TOOL CARD
// ─────────────────────────────────────────────────────────

@Composable
private fun ToolCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    accentBg: Color,
    onClick: () -> Unit,
    enabled: Boolean
) {
    val enabledAlpha = if (enabled) 1f else 0.4f

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(18.dp),
        enabled = enabled
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            accentBg.copy(alpha = enabledAlpha * 0.28f),
                            BackgroundCard
                        )
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
                .border(
                    1.dp,
                    accentColor.copy(alpha = enabledAlpha * 0.18f),
                    RoundedCornerShape(18.dp)
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon box
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .background(
                            accentBg.copy(alpha = enabledAlpha * 0.5f),
                            RoundedCornerShape(14.dp)
                        )
                        .border(
                            1.dp,
                            accentColor.copy(alpha = enabledAlpha * 0.2f),
                            RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor.copy(alpha = enabledAlpha),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary.copy(alpha = enabledAlpha)
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = TextTertiary.copy(alpha = enabledAlpha)
                    )
                }

                // Arrow circle
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .background(
                            accentBg.copy(alpha = enabledAlpha * 0.35f),
                            CircleShape
                        )
                        .border(
                            1.dp,
                            accentColor.copy(alpha = enabledAlpha * 0.25f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        null,
                        tint = accentColor.copy(alpha = enabledAlpha),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
