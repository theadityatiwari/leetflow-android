package com.nativeknights.leetflow.ui.screens.problemselector

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nativeknights.leetflow.ui.screens.problemselector.components.RecommendationCard
import com.nativeknights.leetflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProblemSelectorScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProblemSelectorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val preference by viewModel.preference.collectAsState()
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "⚡", fontSize = 22.sp)
                        Text("Decision Fatigue Remover")
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
                    titleContentColor = PrimaryBlue,
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            when (state) {
                is RecommendationState.Idle -> {
                    HeroBanner()
                    InputSection(
                        preference = preference,
                        onPreferenceChange = viewModel::onPreferenceChange,
                        onGetRecommendation = viewModel::getRecommendation
                    )
                }

                is RecommendationState.Loading -> {
                    LoadingSection()
                }

                is RecommendationState.Success -> {
                    val recommendation = (state as RecommendationState.Success).recommendation

                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 })
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            RecommendationCard(recommendation = recommendation)

                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(recommendation.url))
                                    context.startActivity(intent)
                                    viewModel.acceptRecommendation()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SuccessGreen
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Start Solving",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = viewModel::rerollRecommendation,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = TextSecondary
                                ),
                                border = BorderStroke(1.dp, CardBorder),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Give me another", fontSize = 14.sp)
                            }
                        }
                    }
                }

                is RecommendationState.Error -> {
                    ErrorSection(
                        message = (state as RecommendationState.Error).message,
                        onRetry = { viewModel.resetState() }
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroBanner() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.08f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, PrimaryBlue.copy(alpha = 0.18f), RoundedCornerShape(20.dp))
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(PrimaryBlue.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("⚡", fontSize = 26.sp)
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Stop asking. Start solving.",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "AI picks the right problem for you — instantly.",
                        fontSize = 13.sp,
                        color = TextTertiary,
                        lineHeight = 18.sp
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BenefitPill("🎯 Fills gaps")
                BenefitPill("⚡ Instant pick")
                BenefitPill("⏱ Time-aware")
            }
        }
    }
}

@Composable
private fun BenefitPill(text: String) {
    Surface(
        color = PrimaryBlue.copy(alpha = 0.12f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            fontSize = 11.sp,
            color = PrimaryBlue,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun InputSection(
    preference: String,
    onPreferenceChange: (String) -> Unit,
    onGetRecommendation: () -> Unit
) {
    val topics = listOf("Arrays", "DP", "Trees", "Graphs", "Strings", "Backtracking", "Greedy")

    val diffLower = preference.lowercase()
    val easySelected = "easy" in diffLower
    val mediumSelected = "medium" in diffLower
    val hardSelected = "hard" in diffLower
    val anySelected = !easySelected && !mediumSelected && !hardSelected

    Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {

        // Difficulty
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionLabel("Difficulty")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    Triple("Any",    anySelected,    PrimaryBlue),
                    Triple("Easy",   easySelected,   DifficultyEasyText),
                    Triple("Medium", mediumSelected,  DifficultyMediumText),
                    Triple("Hard",   hardSelected,   DifficultyHardText)
                ).forEach { (label, selected, color) ->
                    FilterChip(
                        selected = selected,
                        onClick = {
                            onPreferenceChange(if (label == "Any") "" else label)
                        },
                        label = { Text(label, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = color.copy(alpha = 0.2f),
                            selectedLabelColor = color,
                            containerColor = CardElevated,
                            labelColor = TextSecondary
                        ),
                        border = BorderStroke(1.dp, if (selected) color else CardBorder)
                    )
                }
            }
        }

        // Topics
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionLabel("Topic  (optional)")
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                topics.forEach { topic ->
                    val isSelected = topic.lowercase() in preference.lowercase()
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) {
                                val updated = preference
                                    .replace("Focus on $topic", "")
                                    .replace(topic, "")
                                    .trim()
                                    .trimEnd(',')
                                    .trim()
                                onPreferenceChange(updated)
                            } else {
                                val current = preference.trim()
                                onPreferenceChange(
                                    if (current.isBlank()) "Focus on $topic"
                                    else "$current, $topic"
                                )
                            }
                        },
                        label = { Text(topic, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryBlue.copy(alpha = 0.2f),
                            selectedLabelColor = PrimaryBlue,
                            containerColor = CardElevated,
                            labelColor = TextSecondary
                        ),
                        border = BorderStroke(1.dp, if (isSelected) PrimaryBlue else CardBorder)
                    )
                }
            }
        }

        // Fine-tune field
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SectionLabel("Fine-tune  (optional)")
            OutlinedTextField(
                value = preference,
                onValueChange = onPreferenceChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "e.g., 'Only 20 mins', 'Hard DP only'",
                        color = TextDisabled,
                        fontSize = 14.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BackgroundCard,
                    unfocusedContainerColor = BackgroundCard,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = PrimaryBlue
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // CTA
        Button(
            onClick = onGetRecommendation,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("⚡", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Pick My Problem",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextTertiary,
        letterSpacing = 1.sp
    )
}


@Composable
private fun LoadingSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardElevated),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(PrimaryBlue.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(44.dp),
                    color = PrimaryBlue,
                    strokeWidth = 3.dp
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Finding your problem...",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = "Analyzing gaps · Matching difficulty · Best fit",
                    fontSize = 13.sp,
                    color = TextTertiary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun ErrorSection(
    message: String,
    onRetry: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ErrorRedBg.copy(alpha = 0.12f)),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, ErrorRedText.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "⚠️", fontSize = 44.sp)
            Text(
                text = message,
                fontSize = 15.sp,
                color = ErrorRedText,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Try Again")
            }
        }
    }
}
