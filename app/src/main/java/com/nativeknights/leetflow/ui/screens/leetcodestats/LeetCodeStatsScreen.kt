package com.nativeknights.leetflow.ui.screens.leetcodestats

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalFocusManager
import java.util.Calendar
import java.util.TimeZone
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.nativeknights.leetflow.data.models.ContestHistory
import com.nativeknights.leetflow.data.models.UserBadge
import com.nativeknights.leetflow.ui.theme.*
import kotlin.math.roundToInt

private val LeetcodeOrange = Color(0xFFFFA116)
private val LeetcodeOrangeDark = Color(0xFF78350F)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeetCodeStatsScreen(
    onNavigateBack: () -> Unit,
    viewModel: LeetCodeStatsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val username by viewModel.username.collectAsState()
    val focusManager = LocalFocusManager.current

    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { contentVisible = true }
    val contentAlpha by animateFloatAsState(
        targetValue = if (contentVisible) 1f else 0f,
        animationSpec = tween(400),
        label = "alpha"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("📊", fontSize = 22.sp)
                        Text(
                            "LeetCode Stats",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    if (uiState is LeetCodeStatsUiState.Success) {
                        IconButton(onClick = { viewModel.fetchStats() }) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = LeetcodeOrange
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundCard,
                    titleContentColor = TextPrimary
                )
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
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ── Username Input ────────────────────────────────────────────────
            UsernameInputCard(
                username = username,
                isLoading = uiState is LeetCodeStatsUiState.Loading,
                onUsernameChange = viewModel::onUsernameChange,
                onFetch = {
                    focusManager.clearFocus()
                    viewModel.fetchStats()
                }
            )

            // ── Loading ───────────────────────────────────────────────────────
            AnimatedVisibility(
                visible = uiState is LeetCodeStatsUiState.Loading,
                enter = fadeIn(), exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        CircularProgressIndicator(
                            color = LeetcodeOrange,
                            modifier = Modifier.size(40.dp),
                            strokeWidth = 3.dp
                        )
                        Text(
                            "Fetching profile...",
                            color = TextTertiary,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // ── Error ─────────────────────────────────────────────────────────
            AnimatedVisibility(
                visible = uiState is LeetCodeStatsUiState.Error,
                enter = fadeIn(), exit = fadeOut()
            ) {
                val msg = (uiState as? LeetCodeStatsUiState.Error)?.message ?: ""
                Card(
                    colors = CardDefaults.cardColors(containerColor = ErrorRedBg.copy(alpha = 0.25f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, ErrorRed.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("❌", fontSize = 20.sp)
                        Text(msg, color = ErrorRedText, fontSize = 13.sp, lineHeight = 18.sp)
                    }
                }
            }

            // ── Stats ─────────────────────────────────────────────────────────
            if (uiState is LeetCodeStatsUiState.Success) {
                val stats = (uiState as LeetCodeStatsUiState.Success).stats
                StatsContent(stats)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── Username Input Card ──────────────────────────────────────────────────────
@Composable
private fun UsernameInputCard(
    username: String,
    isLoading: Boolean,
    onUsernameChange: (String) -> Unit,
    onFetch: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BackgroundCard),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = LeetcodeOrange.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = LeetcodeOrange,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    "Enter LeetCode Username",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = username,
                    onValueChange = onUsernameChange,
                    placeholder = { Text("e.g. theadityatiwari") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                    keyboardActions = KeyboardActions(onGo = { onFetch() }),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardElevated,
                        unfocusedContainerColor = CardElevated,
                        focusedBorderColor = LeetcodeOrange,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = LeetcodeOrange
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
                Button(
                    onClick = onFetch,
                    enabled = username.isNotBlank() && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LeetcodeOrange,
                        disabledContainerColor = LeetcodeOrange.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(56.dp)
                ) {
                    Text("Fetch", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
        }
    }
}

// ── All Stats ────────────────────────────────────────────────────────────────
@Composable
private fun StatsContent(stats: LeetCodeStats) {
    // ── Profile Overview ──────────────────────────────────────────────────
    ProfileOverviewCard(stats)

    // ── Problems Solved ───────────────────────────────────────────────────
    SolvedCard(stats)

    // ── Activity ──────────────────────────────────────────────────────────
    ActivityRow(stats)

    // ── Heatmap ───────────────────────────────────────────────────────────
    HeatmapCard(stats.submissionCalendar)

    // ── Contest ───────────────────────────────────────────────────────────
    if ((stats.contestRating ?: 0.0) > 0.0) {
        ContestCard(stats)
    }

    // ── Badges ────────────────────────────────────────────────────────────
    if (stats.badges.isNotEmpty()) {
        BadgesCard(stats.badges)
    }
}

// ── Profile Overview Card ────────────────────────────────────────────────────
@Composable
private fun ProfileOverviewCard(stats: LeetCodeStats) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BackgroundCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            LeetcodeOrangeDark.copy(alpha = 0.3f),
                            BackgroundCard
                        )
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
                .border(1.dp, LeetcodeOrange.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Surface(
                        color = LeetcodeOrange.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            "@${stats.username}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = LeetcodeOrange
                        )
                    }
                    Text(
                        "LeetCode Profile",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Text(
                        "Tracked live from leetcode.com",
                        fontSize = 11.sp,
                        color = TextDisabled
                    )
                }
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "#${"%,d".format(stats.ranking)}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = LeetcodeOrange
                    )
                    Text("Global Rank", fontSize = 10.sp, color = TextTertiary)
                }
            }
        }
    }
}

// ── Solved Card ──────────────────────────────────────────────────────────────
@Composable
private fun SolvedCard(stats: LeetCodeStats) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BackgroundCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "PROBLEMS SOLVED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDisabled,
                        letterSpacing = 1.5.sp
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "${stats.totalSolved}",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            lineHeight = 36.sp
                        )
                        Text(
                            "/ ${stats.totalProblems}",
                            fontSize = 14.sp,
                            color = TextDisabled,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
                // Donut-style ring
                SolvedRing(stats)
            }

            HorizontalDivider(color = CardBorder)

            // Difficulty rows
            DifficultyRow(
                label = "Easy",
                solved = stats.easySolved,
                total = stats.easyTotal,
                color = DifficultyEasyText
            )
            DifficultyRow(
                label = "Medium",
                solved = stats.mediumSolved,
                total = stats.mediumTotal,
                color = DifficultyMediumText
            )
            DifficultyRow(
                label = "Hard",
                solved = stats.hardSolved,
                total = stats.hardTotal,
                color = DifficultyHardText
            )
        }
    }
}

@Composable
private fun SolvedRing(stats: LeetCodeStats) {
    val totalProgress = if (stats.totalProblems > 0) stats.totalSolved.toFloat() / stats.totalProblems else 0f
    val easyProgress = if (stats.easyTotal > 0) stats.easySolved.toFloat() / stats.easyTotal else 0f
    val mediumProgress = if (stats.mediumTotal > 0) stats.mediumSolved.toFloat() / stats.mediumTotal else 0f
    val hardProgress = if (stats.hardTotal > 0) stats.hardSolved.toFloat() / stats.hardTotal else 0f

    val animTotal by animateFloatAsState(totalProgress, tween(1000), label = "total")
    val animEasy by animateFloatAsState(easyProgress, tween(1000, 100), label = "easy")
    val animMedium by animateFloatAsState(mediumProgress, tween(1000, 200), label = "medium")
    val animHard by animateFloatAsState(hardProgress, tween(1000, 300), label = "hard")

    val easyColor = DifficultyEasyText
    val mediumColor = DifficultyMediumText
    val hardColor = DifficultyHardText
    val trackColor = CardElevated

    Canvas(modifier = Modifier.size(90.dp)) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val stroke = 7.dp.toPx()
        val gap = 4.dp.toPx()

        // Three concentric arcs: outer=easy, middle=medium, inner=hard
        val outerR = size.minDimension / 2f - stroke / 2
        val middleR = outerR - stroke - gap
        val innerR = middleR - stroke - gap

        fun drawRing(radius: Float, progress: Float, color: Color) {
            // track
            drawCircle(color = trackColor, radius = radius, center = Offset(cx, cy), style = Stroke(stroke))
            // progress arc
            if (progress > 0f) {
                drawArc(
                    color = color,
                    startAngle = -90f,
                    sweepAngle = 360f * progress,
                    useCenter = false,
                    style = Stroke(stroke, cap = StrokeCap.Round),
                    topLeft = Offset(cx - radius, cy - radius),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
                )
            }
        }

        drawRing(outerR, animEasy, easyColor)
        drawRing(middleR, animMedium, mediumColor)
        drawRing(innerR, animHard, hardColor)
    }
}

@Composable
private fun DifficultyRow(label: String, solved: Int, total: Int, color: Color) {
    val progress = if (total > 0) solved.toFloat() / total else 0f
    val animProgress by animateFloatAsState(progress, tween(1000), label = "prog_$label")

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Surface(
            color = color.copy(alpha = 0.15f),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier.width(56.dp)
        ) {
            Text(
                label,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = TextAlign.Center
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(CardElevated)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animProgress)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(color.copy(alpha = 0.7f), color)
                        )
                    )
            )
        }

        Text(
            "$solved / $total",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary,
            modifier = Modifier.width(72.dp),
            textAlign = TextAlign.End
        )
    }
}

// ── Activity Row ─────────────────────────────────────────────────────────────
@Composable
private fun ActivityRow(stats: LeetCodeStats) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatBox(
            modifier = Modifier.weight(1f),
            value = "${stats.streak}",
            label = "Streak",
            emoji = "🔥",
            accentColor = Color(0xFFFB923C)
        )
        StatBox(
            modifier = Modifier.weight(1f),
            value = "${stats.totalActiveDays}",
            label = "Active Days",
            emoji = "📅",
            accentColor = PrimaryBlue
        )
        StatBox(
            modifier = Modifier.weight(1f),
            value = formatCount(stats.totalSubmissions),
            label = "Submissions",
            emoji = "📤",
            accentColor = Color(0xFF22D3EE)
        )
    }
}

@Composable
private fun StatBox(
    modifier: Modifier = Modifier,
    value: String,
    label: String,
    emoji: String,
    accentColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BackgroundCard),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, accentColor.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                .padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(emoji, fontSize = 18.sp)
            Text(
                value,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor,
                textAlign = TextAlign.Center
            )
            Text(
                label,
                fontSize = 10.sp,
                color = TextDisabled,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Contest Card ─────────────────────────────────────────────────────────────
@Composable
private fun ContestCard(stats: LeetCodeStats) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BackgroundCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, LeetcodeOrange.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "CONTEST RATING",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDisabled,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        "${stats.contestRating?.roundToInt() ?: "—"}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = LeetcodeOrange,
                        lineHeight = 36.sp
                    )
                }
                if (stats.topPercentage != null) {
                    Surface(
                        color = LeetcodeOrange.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .border(1.dp, LeetcodeOrange.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Top ${stats.topPercentage.let { "%.1f".format(it) }}%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = LeetcodeOrange
                            )
                            Text("percentile", fontSize = 10.sp, color = TextDisabled)
                        }
                    }
                }
            }

            // Contest rating line chart
            if (stats.contestHistory.size >= 2) {
                RatingGraph(
                    history = stats.contestHistory,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CardElevated)
                )
            }

            HorizontalDivider(color = CardBorder)

            // Bottom stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ContestStat(
                    value = "#${"%,d".format(stats.contestGlobalRank ?: 0)}",
                    label = "Contest Rank"
                )
                Box(modifier = Modifier.width(1.dp).height(32.dp).background(CardBorder))
                ContestStat(
                    value = "${stats.attendedContests ?: 0}",
                    label = "Contests"
                )
                Box(modifier = Modifier.width(1.dp).height(32.dp).background(CardBorder))
                ContestStat(
                    value = "${stats.contestHistory.size} rated",
                    label = "History"
                )
            }
        }
    }
}

@Composable
private fun ContestStat(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(label, fontSize = 10.sp, color = TextDisabled)
    }
}

// ── Contest Rating Graph (Canvas) ────────────────────────────────────────────
@Composable
private fun RatingGraph(history: List<ContestHistory>, modifier: Modifier = Modifier) {
    val ratings = history.mapNotNull { it.rating?.toFloat() }
    if (ratings.size < 2) return

    val minR = ratings.min()
    val maxR = ratings.max()
    val range = (maxR - minR).coerceAtLeast(1f)
    val lineColor = LeetcodeOrange

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val padH = 12.dp.toPx()
        val padV = 12.dp.toPx()

        val xStep = (w - padH * 2) / (ratings.size - 1).coerceAtLeast(1)
        fun xAt(i: Int) = padH + i * xStep
        fun yAt(r: Float) = h - padV - ((r - minR) / range) * (h - padV * 2)

        // Area fill
        val areaPath = Path().apply {
            moveTo(xAt(0), h)
            lineTo(xAt(0), yAt(ratings[0]))
            for (i in 1 until ratings.size) lineTo(xAt(i), yAt(ratings[i]))
            lineTo(xAt(ratings.lastIndex), h)
            close()
        }
        drawPath(
            areaPath,
            Brush.verticalGradient(
                colors = listOf(lineColor.copy(alpha = 0.25f), Color.Transparent),
                startY = 0f,
                endY = h
            )
        )

        // Line
        for (i in 0 until ratings.size - 1) {
            drawLine(
                color = lineColor,
                start = Offset(xAt(i), yAt(ratings[i])),
                end = Offset(xAt(i + 1), yAt(ratings[i + 1])),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round
            )
        }

        // Dot at latest rating
        drawCircle(
            color = lineColor,
            radius = 4.dp.toPx(),
            center = Offset(xAt(ratings.lastIndex), yAt(ratings.last()))
        )
        drawCircle(
            color = BackgroundCard,
            radius = 2.dp.toPx(),
            center = Offset(xAt(ratings.lastIndex), yAt(ratings.last()))
        )
    }
}

// ── Helpers ──────────────────────────────────────────────────────────────────
private fun formatCount(n: Int): String = when {
    n >= 1_000_000 -> "${"%.1f".format(n / 1_000_000.0)}M"
    n >= 1_000 -> "${"%.1f".format(n / 1_000.0)}K"
    else -> "$n"
}

// ── Heatmap data helpers ─────────────────────────────────────────────────────

private val MONTH_SHORT = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")

private data class HeatDay(val month: Int, val dayOfMonth: Int, val count: Int)

/** One month's worth of week-columns (each inner list = 7 cells Sun→Sat, null = padding). */
private data class HeatMonth(val monthIdx: Int, val weeks: List<List<HeatDay?>>)

/**
 * Builds 12 independent month grids for [year].
 * Each month starts fresh at its own day-of-week, so every day of that month
 * is contained within its own month block — no days bleed into an adjacent month.
 */
private fun buildHeatMonths(year: Int, calendar: Map<Long, Int>): List<HeatMonth> {
    val utc = TimeZone.getTimeZone("UTC")

    // Build day-level lookup: "M/D" -> count (UTC dates)
    val lookup = mutableMapOf<String, Int>()
    val tmpCal = Calendar.getInstance(utc)
    calendar.forEach { (ts, count) ->
        tmpCal.timeInMillis = ts * 1000L
        if (tmpCal.get(Calendar.YEAR) == year) {
            val key = "${tmpCal.get(Calendar.MONTH) + 1}/${tmpCal.get(Calendar.DAY_OF_MONTH)}"
            lookup[key] = (lookup[key] ?: 0) + count
        }
    }

    return (1..12).map { month ->
        val cal = Calendar.getInstance(utc).apply {
            set(year, month - 1, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val leadingNulls = cal.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Sunday

        val flat = mutableListOf<HeatDay?>()
        repeat(leadingNulls) { flat.add(null) }
        for (d in 1..daysInMonth) {
            flat.add(HeatDay(month = month, dayOfMonth = d, count = lookup["$month/$d"] ?: 0))
        }
        while (flat.size % 7 != 0) flat.add(null) // trailing pad to complete last week

        HeatMonth(monthIdx = month - 1, weeks = flat.chunked(7))
    }
}

// ── Heatmap Card ─────────────────────────────────────────────────────────────

@Composable
private fun HeatmapCard(submissionCalendar: Map<Long, Int>) {
    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }

    // Derive available years from the data
    val availableYears = remember(submissionCalendar) {
        val utc = TimeZone.getTimeZone("UTC")
        val cal = Calendar.getInstance(utc)
        submissionCalendar.keys
            .map { ts -> cal.also { it.timeInMillis = ts * 1000L }.get(Calendar.YEAR) }
            .distinct()
            .sortedDescending()
            .let { years -> if (currentYear !in years) listOf(currentYear) + years else years }
    }

    var selectedYear by remember(availableYears) { mutableIntStateOf(availableYears.first()) }
    var dropdownOpen by remember { mutableStateOf(false) }

    // Active days for selected year
    val activeDaysThisYear = remember(submissionCalendar, selectedYear) {
        val utc = TimeZone.getTimeZone("UTC")
        val cal = Calendar.getInstance(utc)
        submissionCalendar.count { (ts, _) ->
            cal.timeInMillis = ts * 1000L
            cal.get(Calendar.YEAR) == selectedYear
        }
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = BackgroundCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── Header ────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        "CONSISTENCY",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextDisabled,
                        letterSpacing = 1.5.sp
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "$activeDaysThisYear",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = LeetcodeOrange,
                            lineHeight = 28.sp
                        )
                        Text(
                            "active days",
                            fontSize = 12.sp,
                            color = TextTertiary,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                }

                // Year dropdown
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(LeetcodeOrange.copy(alpha = 0.12f))
                            .border(1.dp, LeetcodeOrange.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                            .clickable { dropdownOpen = true }
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "$selectedYear",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = LeetcodeOrange
                        )
                        Text("▾", fontSize = 11.sp, color = LeetcodeOrange)
                    }
                    DropdownMenu(
                        expanded = dropdownOpen,
                        onDismissRequest = { dropdownOpen = false },
                        containerColor = CardElevated
                    ) {
                        availableYears.forEach { year ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        "$year",
                                        color = if (year == selectedYear) LeetcodeOrange else TextPrimary,
                                        fontWeight = if (year == selectedYear) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp
                                    )
                                },
                                onClick = {
                                    selectedYear = year
                                    dropdownOpen = false
                                }
                            )
                        }
                    }
                }
            }

            HorizontalDivider(color = CardBorder)

            // ── Grid ──────────────────────────────────────────────────────
            key(selectedYear) {
                SubmissionHeatmap(
                    year = selectedYear,
                    submissionCalendar = submissionCalendar
                )
            }

            // ── Legend ────────────────────────────────────────────────────
            HeatmapLegend()
        }
    }
}

// ── Heatmap Grid ─────────────────────────────────────────────────────────────

private val DAY_LABELS = listOf("S", "M", "T", "W", "T", "F", "S")

@Composable
private fun SubmissionHeatmap(year: Int, submissionCalendar: Map<Long, Int>) {
    val months = remember(year, submissionCalendar) { buildHeatMonths(year, submissionCalendar) }

    val cellSize = 11.dp
    val cellGap = 3.dp
    val monthLabelHeight = 14.dp
    val dayLabelWidth = 10.dp

    Row(verticalAlignment = Alignment.Top) {
        // ── Fixed day-of-week labels ─────────────────────────────────────
        Column(
            modifier = Modifier.padding(top = monthLabelHeight + cellGap),
            verticalArrangement = Arrangement.spacedBy(cellGap)
        ) {
            DAY_LABELS.forEachIndexed { idx, label ->
                Text(
                    text = if (idx % 2 == 1) label else "",
                    fontSize = 8.sp,
                    color = TextDisabled,
                    modifier = Modifier
                        .width(dayLabelWidth)
                        .height(cellSize),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.width(4.dp))

        // ── Scrollable month blocks ───────────────────────────────────────
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            months.forEachIndexed { mIdx, heatMonth ->
                Column {
                    // Month label
                    Text(
                        text = MONTH_SHORT[heatMonth.monthIdx],
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextTertiary,
                        modifier = Modifier.height(monthLabelHeight)
                    )
                    Spacer(modifier = Modifier.height(cellGap))
                    // Week columns for this month
                    Row {
                        heatMonth.weeks.forEachIndexed { wIdx, week ->
                            Column(verticalArrangement = Arrangement.spacedBy(cellGap)) {
                                week.forEach { day ->
                                    HeatCell(count = day?.count, isVisible = day != null)
                                }
                            }
                            if (wIdx != heatMonth.weeks.lastIndex) {
                                Spacer(modifier = Modifier.width(cellGap))
                            }
                        }
                    }
                }
                if (mIdx != months.lastIndex) {
                    Spacer(modifier = Modifier.width(cellGap * 3))
                }
            }
        }
    }
}

@Composable
private fun HeatCell(count: Int?, isVisible: Boolean) {
    val cellSize = 11.dp
    val color = when {
        !isVisible || count == null -> Color.Transparent
        count == 0 -> CardElevated
        count == 1 -> LeetcodeOrange.copy(alpha = 0.22f)
        count <= 3 -> LeetcodeOrange.copy(alpha = 0.44f)
        count <= 6 -> LeetcodeOrange.copy(alpha = 0.68f)
        else -> LeetcodeOrange
    }
    Box(
        modifier = Modifier
            .size(cellSize)
            .clip(RoundedCornerShape(2.dp))
            .background(color)
    )
}

// ── Legend ───────────────────────────────────────────────────────────────────

@Composable
private fun HeatmapLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text("Less", fontSize = 9.sp, color = TextDisabled)
        Spacer(Modifier.width(4.dp))
        listOf(
            CardElevated,
            LeetcodeOrange.copy(alpha = 0.22f),
            LeetcodeOrange.copy(alpha = 0.44f),
            LeetcodeOrange.copy(alpha = 0.68f),
            LeetcodeOrange
        ).forEach { bg ->
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(bg)
            )
            Spacer(Modifier.width(3.dp))
        }
        Text("More", fontSize = 9.sp, color = TextDisabled)
    }
}

// ── Badges Card ───────────────────────────────────────────────────────────────

@Composable
private fun BadgesCard(badges: List<UserBadge>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = BackgroundCard),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🏅", fontSize = 18.sp)
                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Text(
                            "BADGES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextDisabled,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            "Earned achievements",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    }
                }
                Surface(
                    color = LeetcodeOrange.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        "${badges.size}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = LeetcodeOrange
                    )
                }
            }

            HorizontalDivider(color = CardBorder)

            // Horizontal badge list
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                badges.forEach { badge -> BadgeItem(badge) }
            }
        }
    }
}

@Composable
private fun BadgeItem(badge: UserBadge) {
    val iconUrl = badge.icon?.let { path ->
        if (path.startsWith("http")) path else "https://leetcode.com$path"
    }
    val earnedDate = badge.creationDate?.let { raw ->
        // "YYYY-MM-DD" → "MMM YYYY"
        try {
            val parts = raw.split("-")
            val monthNames = listOf("Jan","Feb","Mar","Apr","May","Jun",
                "Jul","Aug","Sep","Oct","Nov","Dec")
            val m = parts[1].toInt() - 1
            "${monthNames[m]} ${parts[0]}"
        } catch (e: Exception) { raw }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.width(80.dp)
    ) {
        // Badge icon circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(CardElevated)
                .border(1.dp, LeetcodeOrange.copy(alpha = 0.25f), CircleShape)
        ) {
            if (iconUrl != null) {
                AsyncImage(
                    model = iconUrl,
                    contentDescription = badge.displayName,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
            } else {
                Text("🏅", fontSize = 28.sp)
            }
        }

        // Badge name
        Text(
            text = badge.displayName ?: "Badge",
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            textAlign = TextAlign.Center,
            lineHeight = 13.sp,
            maxLines = 2,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )

        // Earned date
        if (earnedDate != null) {
            Text(
                text = earnedDate,
                fontSize = 9.sp,
                color = TextDisabled,
                textAlign = TextAlign.Center
            )
        }
    }
}
