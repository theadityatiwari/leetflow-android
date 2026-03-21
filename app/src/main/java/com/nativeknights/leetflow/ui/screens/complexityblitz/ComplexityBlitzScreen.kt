package com.nativeknights.leetflow.ui.screens.complexityblitz

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nativeknights.leetflow.data.models.BlitzQuestion
import com.nativeknights.leetflow.ui.theme.*

private val BlitzCyan = Color(0xFF22D3EE)
private val BlitzCyanBg = Color(0xFF0C4A6E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplexityBlitzScreen(
    onNavigateBack: () -> Unit,
    viewModel: ComplexityBlitzViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    Scaffold(
        containerColor = BackgroundPrimary,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "⚡", fontSize = 24.sp)
                        Text("Complexity Blitz")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundCard,
                    titleContentColor = BlitzCyan
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = BlitzCyan.copy(alpha = 0.10f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BlitzCyan.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "⚡", fontSize = 32.sp)
                    Column {
                        Text(
                            text = "Complexity Blitz",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = BlitzCyan
                        )
                        Text(
                            text = "Test your Big-O knowledge",
                            fontSize = 13.sp,
                            color = TextTertiary
                        )
                    }
                }
            }

            // Language Selector
            LanguageSelector(
                languages = viewModel.availableLanguages,
                selectedLanguage = selectedLanguage,
                onSelectLanguage = { viewModel.selectLanguage(it) }
            )

            // State Content
            when (val currentState = state) {
                is BlitzState.Idle -> {
                    IdleView(
                        language = selectedLanguage,
                        onStart = { viewModel.generateQuestion() }
                    )
                }

                is BlitzState.Loading -> {
                    LoadingView()
                }

                is BlitzState.Question -> {
                    QuestionView(
                        data = currentState.data,
                        selectedTime = currentState.selectedTime,
                        isCorrect = currentState.isCorrect,
                        onSelectAnswer = { viewModel.checkAnswer(it) },
                        onNext = { viewModel.nextQuestion() }
                    )
                }

                is BlitzState.Error -> {
                    ErrorView(
                        message = currentState.message,
                        onRetry = { viewModel.reset() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LanguageSelector(
    languages: List<String>,
    selectedLanguage: String,
    onSelectLanguage: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Select Language",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            languages.take(3).forEach { language ->
                FilterChip(
                    selected = selectedLanguage == language,
                    onClick = { onSelectLanguage(language) },
                    label = {
                        Text(
                            text = language,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = CardElevated,
                        labelColor = TextSecondary,
                        selectedContainerColor = BlitzCyan.copy(alpha = 0.2f),
                        selectedLabelColor = BlitzCyan
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (selectedLanguage == language) BlitzCyan else CardBorder
                    )
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            languages.drop(3).forEach { language ->
                FilterChip(
                    selected = selectedLanguage == language,
                    onClick = { onSelectLanguage(language) },
                    label = {
                        Text(
                            text = language,
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = CardElevated,
                        labelColor = TextSecondary,
                        selectedContainerColor = BlitzCyan.copy(alpha = 0.2f),
                        selectedLabelColor = BlitzCyan
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (selectedLanguage == language) BlitzCyan else CardBorder
                    )
                )
            }
        }
    }
}

@Composable
private fun IdleView(
    language: String,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "🧠", fontSize = 80.sp)

        Text(
            text = "Ready to test your $language skills?",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = TextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BlitzCyan
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = BackgroundPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Start Blitz",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = BackgroundPrimary
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
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = BlitzCyan
            )
            Text(
                text = "Generating question...",
                fontSize = 14.sp,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun QuestionView(
    data: BlitzQuestion,
    selectedTime: String?,
    isCorrect: Boolean?,
    onSelectAnswer: (String) -> Unit,
    onNext: () -> Unit
) {
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Code Card
            CodeCard(code = data.codeSnippet)

            // Question
            Text(
                text = "What is the time complexity?",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )

            // Options
            data.options.forEach { option ->
                OptionButton(
                    option = option,
                    isSelected = selectedTime == option,
                    isCorrect = isCorrect,
                    onClick = { if (selectedTime == null) onSelectAnswer(option) }
                )
            }

            // Feedback & Next Button
            if (selectedTime != null && isCorrect != null) {
                FeedbackCard(
                    isCorrect = isCorrect,
                    explanation = data.explanation
                )

                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Next Question", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CodeCard(code: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = BackgroundCard
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
        ) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CardElevated,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(ErrorRed, shape = androidx.compose.foundation.shape.CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(WarningYellowText, shape = androidx.compose.foundation.shape.CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(SuccessGreen, shape = androidx.compose.foundation.shape.CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "code.py",
                        fontSize = 12.sp,
                        color = TextTertiary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Code
            Text(
                text = code,
                modifier = Modifier.padding(16.dp),
                fontFamily = FontFamily.Monospace,
                color = InfoBlueText,
                fontSize = 13.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun OptionButton(
    option: String,
    isSelected: Boolean,
    isCorrect: Boolean?,
    onClick: () -> Unit
) {
    val borderColor = when {
        isSelected && isCorrect == true -> SuccessGreen
        isSelected && isCorrect == false -> ErrorRed
        else -> CardBorder
    }

    val backgroundColor = when {
        isSelected && isCorrect == true -> SuccessGreen.copy(alpha = 0.1f)
        isSelected && isCorrect == false -> ErrorRed.copy(alpha = 0.1f)
        else -> BackgroundCard
    }

    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = backgroundColor,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = option,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = TextPrimary,
                fontFamily = FontFamily.Monospace
            )

            if (isSelected && isCorrect != null) {
                Text(
                    text = if (isCorrect) "✓" else "✗",
                    fontSize = 20.sp,
                    color = if (isCorrect) SuccessGreen else ErrorRed
                )
            }
        }
    }
}

@Composable
private fun FeedbackCard(isCorrect: Boolean, explanation: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect)
                SuccessGreen.copy(alpha = 0.15f)
            else
                ErrorRed.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    if (isCorrect) SuccessGreen.copy(alpha = 0.3f) else ErrorRed.copy(alpha = 0.3f),
                    RoundedCornerShape(12.dp)
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = if (isCorrect) "✅" else "❌",
                    fontSize = 24.sp
                )
                Column {
                    Text(
                        text = if (isCorrect) "Correct!" else "Incorrect",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCorrect) SuccessGreenText else ErrorRedText
                    )
                    if (explanation.isNotEmpty()) {
                        Text(
                            text = explanation,
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "⚠️", fontSize = 48.sp)
            Text(
                text = message,
                fontSize = 14.sp,
                color = ErrorRedText
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                )
            ) {
                Text("Try Again")
            }
        }
    }
}