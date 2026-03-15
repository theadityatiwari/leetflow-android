package com.nativeknights.leetflow.ui.screens.blindproblem

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nativeknights.leetflow.data.models.BlindProblem
import com.nativeknights.leetflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlindProblemScreen(
    onNavigateBack: () -> Unit,
    viewModel: BlindProblemViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🎯", fontSize = 24.sp)
                        Text("Blind Challenge")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundCard,
                    titleContentColor = ErrorRedText,
                    navigationIconContentColor = TextPrimary
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // Hero Section
            HeroSection()

            // Difficulty Selector
            DifficultySelector(
                selectedDifficulty = null,
                onSelectDifficulty = { difficulty ->
                    viewModel.suggestProblem(difficulty)
                }
            )

            // State Content
            when (val currentState = state) {
                is BlindState.Idle -> {
                    IdleView()
                }

                is BlindState.Loading -> {
                    LoadingView()
                }

                is BlindState.Success -> {
                    ProblemCard(
                        problem = currentState.problem,
                        onOpenLeetCode = {
                            val url = currentState.problem.leetCodeUrl
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        },
                        onNext = { difficulty ->
                            viewModel.suggestProblem(difficulty)
                        }
                    )
                }

                is BlindState.Error -> {
                    ErrorView(message = currentState.message)
                }
            }
        }
    }
}

@Composable
private fun HeroSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ErrorRedBg.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ErrorRed.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .padding(24.dp)
        ) {
            // Decorative background
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 30.dp, y = (-30).dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                ErrorRed.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = ErrorRed.copy(alpha = 0.2f),
                        shape = CircleShape,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = "🎯", fontSize = 28.sp)
                        }
                    }

                    Column {
                        Text(
                            text = "Blind Challenge Mode",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ErrorRedText
                        )
                        Text(
                            text = "Train like FAANG",
                            fontSize = 13.sp,
                            color = TextTertiary
                        )
                    }
                }

                Divider(color = ErrorRed.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FeatureBadge(icon = "🚫", text = "No Patterns", modifier = Modifier.weight(1f))
                    FeatureBadge(icon = "🏷️", text = "No Tags", modifier = Modifier.weight(1f))
                    FeatureBadge(icon = "💪", text = "Pure Skill", modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun FeatureBadge(
    icon: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = BackgroundCard,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun DifficultySelector(
    selectedDifficulty: String?,
    onSelectDifficulty: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Select Difficulty",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DifficultyButton(
                difficulty = "Easy",
                icon = "🟢",
                color = SuccessGreen,
                selected = selectedDifficulty == "Easy",
                onClick = { onSelectDifficulty("Easy") },
                modifier = Modifier.weight(1f)
            )

            DifficultyButton(
                difficulty = "Medium",
                icon = "🟡",
                color = WarningYellowText,
                selected = selectedDifficulty == "Medium",
                onClick = { onSelectDifficulty("Medium") },
                modifier = Modifier.weight(1f)
            )

            DifficultyButton(
                difficulty = "Hard",
                icon = "🔴",
                color = ErrorRedText,
                selected = selectedDifficulty == "Hard",
                onClick = { onSelectDifficulty("Hard") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun DifficultyButton(
    difficulty: String,
    icon: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected)
                color.copy(alpha = 0.15f)
            else
                BackgroundCard
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) color else CardBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = icon,
                fontSize = 32.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = difficulty,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) color else TextSecondary
            )
        }
    }
}

@Composable
private fun IdleView() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundCard
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "🤔", fontSize = 64.sp)
            Text(
                text = "Choose Your Challenge",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Select a difficulty above to get a random problem",
                fontSize = 13.sp,
                color = TextTertiary,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun LoadingView() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundCard
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Animated emoji
            val infiniteTransition = rememberInfiniteTransition(label = "loading")
            val rotation by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rotation"
            )

            Text(
                text = "🎲",
                fontSize = 48.sp,
                modifier = Modifier.scale(1.2f)
            )

            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = PrimaryBlue,
                trackColor = CardBorder
            )

            Text(
                text = "Picking a problem...",
                fontSize = 14.sp,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun ProblemCard(
    problem: BlindProblem,
    onOpenLeetCode: () -> Unit,
    onNext: (String) -> Unit
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 })
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = BackgroundCard
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Challenge",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextTertiary,
                        letterSpacing = 1.sp
                    )

                    DifficultyBadge(difficulty = problem.difficulty)
                }

                // Problem Title
                Text(
                    text = problem.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    lineHeight = 28.sp
                )

                Divider(color = CardBorder)

                // Description
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = CardElevated.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = problem.description,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp,
                        color = TextSecondary,
                        lineHeight = 22.sp
                    )
                }

                // Blind Mode Info
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = InfoBlueBg.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, InfoBlueBg.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "🎯", fontSize = 20.sp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Blind Mode Active",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = InfoBlueText
                            )
                            Text(
                                text = "No hints, patterns, or tags. Think independently!",
                                fontSize = 12.sp,
                                color = TextTertiary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Open LeetCode Button
                    Button(
                        onClick = onOpenLeetCode,
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SuccessGreen
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Start Solving",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Next Problem Button
                    OutlinedButton(
                        onClick = { onNext(problem.difficulty) },
                        modifier = Modifier.height(54.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = TextPrimary
                        ),
                        border = BorderStroke(1.dp, CardBorder),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DifficultyBadge(difficulty: String) {
    val (bgColor, textColor) = when (difficulty) {
        "Easy" -> DifficultyEasyBg.copy(alpha = 0.2f) to DifficultyEasyText
        "Hard" -> DifficultyHardBg.copy(alpha = 0.2f) to DifficultyHardText
        else -> DifficultyMediumBg.copy(alpha = 0.2f) to DifficultyMediumText
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = difficulty,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
private fun ErrorView(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = ErrorRedBg.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ErrorRed.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "⚠️", fontSize = 48.sp)
            Text(
                text = message,
                fontSize = 14.sp,
                color = ErrorRedText,
                textAlign = TextAlign.Center
            )
        }
    }
}
