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
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.android.play.core.review.ReviewManagerFactory
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
    val context = LocalContext.current
    val currentBackStackEntry by navController.currentBackStackEntryAsState()

    LaunchedEffect(currentBackStackEntry) {
        if (currentBackStackEntry?.destination?.route == Screen.Dashboard.route) {
            viewModel.checkRateDialog()
            viewModel.refreshStatsCache()
        }
    }

    val shareApp: () -> Unit = {
        val shareText = "🚀 Supercharge your DSA prep with LeetFlow!\n\n" +
            "✨ AI picks your next problem — no more decision fatigue\n" +
            "🔍 Instant code complexity & quality analysis\n" +
            "🧠 AI-generated flashcards for recall\n" +
            "🗺️ Topic roadmaps with company tags\n" +
            "💥 Complexity Blitz — timed Big-O quiz mode\n" +
            "📊 LeetCode stats, streaks & contest history\n\n" +
            "Free on Google Play 👇\n" +
            "https://play.google.com/store/apps/details?id=com.nativeknights.leetflow"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        context.startActivity(Intent.createChooser(intent, "Share LeetFlow"))
    }

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
                onShareClick = shareApp,
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
                onClick = {
                    viewModel.onFeatureNavigated()
                    navController.navigate(Screen.ProblemSelector.route)
                },
                enabled = state.hasApiKey
            )

            // LeetCode Stats Snapshot — only shown after user has fetched stats at least once
            if (state.hasStatsCache) {
                StatsSnapshotCard(
                    streak = state.streak,
                    totalSolved = state.lcSolved,
                    username = state.lcUsername,
                    onClick = {
                        viewModel.onFeatureNavigated()
                        navController.navigate(Screen.LeetCodeStats.route)
                    }
                )
            }

            // LeetCode Stats section
            SectionHeader(title = "PROFILE", accentColor = Color(0xFFFFA116))

            ToolCard(
                icon = Icons.Default.Star,
                title = "LeetCode Stats",
                subtitle = "Rank · Solved · Streak · Contest rating",
                accentColor = Color(0xFFFFA116),
                accentBg = Color(0xFF78350F),
                onClick = {
                    viewModel.onFeatureNavigated()
                    navController.navigate(Screen.LeetCodeStats.route)
                },
                enabled = true
            )

            // Training section
            SectionHeader(title = "TRAINING", accentColor = Color(0xFF22D3EE))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ComplexityBlitzWidget(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.onFeatureNavigated()
                        navController.navigate(Screen.ComplexityBlitz.route)
                    },
                    enabled = state.hasApiKey
                )
                BlindProblemWidget(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.onFeatureNavigated()
                        navController.navigate(Screen.BlindProblem.route)
                    },
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
                onClick = {
                    viewModel.onFeatureNavigated()
                    navController.navigate(Screen.RoadmapPlanner.route)
                },
                enabled = state.hasApiKey
            )

            ToolCard(
                icon = Icons.Default.Create,
                title = "Code Analyzer",
                subtitle = "Review complexity & quality",
                accentColor = Color(0xFF60A5FA),
                accentBg = Color(0xFF1E3A8A),
                onClick = {
                    viewModel.onFeatureNavigated()
                    navController.navigate(Screen.CodeAnalyzer.route)
                },
                enabled = state.hasApiKey
            )

            ToolCard(
                icon = Icons.Default.Star,
                title = "Recall Notes",
                subtitle = if (state.notesCount > 0) "${state.notesCount} flashcards saved" else "Build your flashcard library",
                accentColor = Color(0xFFFBBF24),
                accentBg = Color(0xFF78350F),
                onClick = {
                    viewModel.onFeatureNavigated()
                    navController.navigate(Screen.FlashCard.route)
                },
                enabled = true
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    val reviewManager = remember { ReviewManagerFactory.create(context) }
    val activity = context as? Activity

    if (state.showRateDialog) {
        RateAppDialog(
            onRateNow = {
                viewModel.onRateNow()
                reviewManager.requestReviewFlow().addOnCompleteListener { task ->
                    if (task.isSuccessful && activity != null) {
                        reviewManager.launchReviewFlow(activity, task.result)
                    } else {
                        // Fallback if Play Store review sheet unavailable
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=com.nativeknights.leetflow")
                        )
                        context.startActivity(intent)
                    }
                }
            },
            onLater = viewModel::onRateLater
        )
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
    onShareClick: () -> Unit,
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
                IconButton(onClick = onShareClick) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(CardElevated, CircleShape)
                            .border(1.dp, CardBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share LeetFlow",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
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

// ─────────────────────────────────────────────────────────
// STATS SNAPSHOT CARD
// ─────────────────────────────────────────────────────────

@Composable
private fun StatsSnapshotCard(
    streak: Int,
    totalSolved: Int,
    username: String,
    onClick: () -> Unit
) {
    // Count-up from 0 on first composition
    var targetStreak by remember { mutableIntStateOf(0) }
    var targetSolved by remember { mutableIntStateOf(0) }
    LaunchedEffect(streak, totalSolved) {
        targetStreak = streak
        targetSolved = totalSolved
    }
    val animatedStreak by animateIntAsState(
        targetValue = targetStreak,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "streak_count"
    )
    val animatedSolved by animateIntAsState(
        targetValue = targetSolved,
        animationSpec = tween(1300, easing = FastOutSlowInEasing),
        label = "solved_count"
    )

    // Arrow bounce
    val infiniteTransition = rememberInfiniteTransition(label = "snapshot_arrow")
    val arrowOffset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(700, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "arrow_offset"
    )

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF1A0F00), // dark amber-black
                            BackgroundCard,
                            Color(0xFF021A0D)  // dark green-black
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            WarningYellow.copy(alpha = 0.35f),
                            CardBorder.copy(alpha = 0.08f),
                            SuccessGreenText.copy(alpha = 0.28f)
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // ── Numbers row ──────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Streak
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("🔥", fontSize = 22.sp)
                            Text(
                                text = "$animatedStreak",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = WarningYellow,
                                letterSpacing = (-1).sp
                            )
                        }
                        Text(
                            text = "DAY STREAK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarningYellow.copy(alpha = 0.55f),
                            letterSpacing = 1.8.sp
                        )
                    }

                    // Fading vertical divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(52.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        CardBorder.copy(alpha = 0f),
                                        CardBorder,
                                        CardBorder.copy(alpha = 0f)
                                    )
                                )
                            )
                    )

                    // Solved
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("✅", fontSize = 19.sp)
                            Text(
                                text = "$animatedSolved",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SuccessGreenText,
                                letterSpacing = (-1).sp
                            )
                        }
                        Text(
                            text = "SOLVED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreenText.copy(alpha = 0.55f),
                            letterSpacing = 1.8.sp
                        )
                    }
                }

                // ── Bottom row: username pill + navigate hint ─────────────
                HorizontalDivider(color = CardBorder.copy(alpha = 0.35f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        color = CardElevated,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                    ) {
                        Text(
                            text = "@$username",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextTertiary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = "View full stats",
                            fontSize = 11.sp,
                            color = TextDisabled,
                            fontWeight = FontWeight.Medium
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier
                                .size(12.dp)
                                .offset(x = arrowOffset.dp),
                            tint = TextDisabled
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────
// RATE APP DIALOG
// ─────────────────────────────────────────────────────────

@Composable
private fun RateAppDialog(
    onRateNow: () -> Unit,
    onLater: () -> Unit
) {
    // Entry animation
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entered = true }
    val scale by animateFloatAsState(
        targetValue = if (entered) 1f else 0.86f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "dialog_scale"
    )
    val dialogAlpha by animateFloatAsState(
        targetValue = if (entered) 1f else 0f,
        animationSpec = tween(320),
        label = "dialog_alpha"
    )

    // Continuous glow pulse
    val infiniteTransition = rememberInfiniteTransition(label = "rate_anim")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.10f, targetValue = 0.28f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow"
    )
    val glowScale by infiniteTransition.animateFloat(
        initialValue = 0.85f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glow_scale"
    )

    // 5 stars with staggered twinkle — each animates independently
    val s0 by infiniteTransition.animateFloat(0.35f, 1f, infiniteRepeatable(tween(900,  delayMillis =   0), RepeatMode.Reverse), "s0")
    val s1 by infiniteTransition.animateFloat(0.35f, 1f, infiniteRepeatable(tween(750,  delayMillis = 200), RepeatMode.Reverse), "s1")
    val s2 by infiniteTransition.animateFloat(0.35f, 1f, infiniteRepeatable(tween(1100, delayMillis = 100), RepeatMode.Reverse), "s2")
    val s3 by infiniteTransition.animateFloat(0.35f, 1f, infiniteRepeatable(tween(800,  delayMillis = 350), RepeatMode.Reverse), "s3")
    val s4 by infiniteTransition.animateFloat(0.35f, 1f, infiniteRepeatable(tween(950,  delayMillis = 150), RepeatMode.Reverse), "s4")
    val starAlphas = listOf(s0, s1, s2, s3, s4)

    Dialog(onDismissRequest = onLater) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .scale(scale)
                .alpha(dialogAlpha)
        ) {
            // Ambient glow blob behind the card
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .scale(glowScale)
                    .align(Alignment.TopCenter)
                    .offset(y = (-30).dp)
                    .background(WarningYellow.copy(alpha = glowAlpha), CircleShape)
            )

            // Card surface
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF1C1200), Color(0xFF0F172A))
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                WarningYellow.copy(alpha = 0.55f),
                                CardBorder.copy(alpha = 0.20f)
                            )
                        ),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(horizontal = 26.dp, vertical = 28.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ── Twinkling stars row ──────────────────────────────
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        starAlphas.forEachIndexed { index, alpha ->
                            // Centre star is largest: sizes 16 19 22 19 16
                            val size = (16 + (2 - kotlin.math.abs(2 - index)) * 3)
                            Text(
                                text = "★",
                                fontSize = size.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarningYellow.copy(alpha = alpha)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // ── Trophy icon badge ────────────────────────────────
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(Color(0xFF92400E), Color(0xFF78350F))
                                ),
                                shape = RoundedCornerShape(22.dp)
                            )
                            .border(
                                width = 1.dp,
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        WarningYellow.copy(alpha = 0.70f),
                                        WarningYellow.copy(alpha = 0.15f)
                                    )
                                ),
                                shape = RoundedCornerShape(22.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🏆", fontSize = 36.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Headline ─────────────────────────────────────────
                    Text(
                        text = "You're on a roll! 🔥",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        textAlign = TextAlign.Center,
                        letterSpacing = (-0.4).sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Loving LeetFlow? A quick rating helps more developers discover their DSA sidekick.",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    // ── Rate button — amber → orange gradient ─────────────
                    Card(
                        onClick = onRateNow,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(Color(0xFFF59E0B), Color(0xFFF97316))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("★", fontSize = 15.sp, color = Color(0xFF1C1407), fontWeight = FontWeight.ExtraBold)
                                Text(
                                    text = "Rate on Google Play",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1C1407),
                                    letterSpacing = (-0.2).sp
                                )
                                Text("★", fontSize = 15.sp, color = Color(0xFF1C1407), fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ── Later button ─────────────────────────────────────
                    OutlinedButton(
                        onClick = onLater,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, CardBorder),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextTertiary)
                    ) {
                        Text(
                            text = "Maybe Later",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
