package com.nativeknights.leetflow.ui.screens.codeanalyzer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import com.nativeknights.leetflow.data.models.CodeAnalysis
import com.nativeknights.leetflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CodeAnalyzerScreen(
    onNavigateBack: () -> Unit,
    viewModel: CodeAnalyzerViewModel = viewModel()
) {
    val codeInput by viewModel.codeInput.collectAsState()
    val analysisState by viewModel.analysisState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🔍", fontSize = 24.sp)
                        Text("Solution Quality Analyzer")
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
                    titleContentColor = InfoBlueText,
                    navigationIconContentColor = TextPrimary
                )
            )
        },
        containerColor = BackgroundPrimary
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (analysisState) {
                is AnalysisState.Idle -> {
                    CodeInputView(
                        code = codeInput,
                        onCodeChange = viewModel::onCodeInputChange,
                        onAnalyze = viewModel::analyzeSolution
                    )
                }
                
                is AnalysisState.Loading -> {
                    LoadingView()
                }
                
                is AnalysisState.Success -> {
                    val analysis = (analysisState as AnalysisState.Success).analysis
                    AnalysisResultView(
                        analysis = analysis,
                        onAnalyzeAnother = viewModel::resetAnalysis
                    )
                }
                
                is AnalysisState.Error -> {
                    ErrorView(
                        message = (analysisState as AnalysisState.Error).message,
                        onRetry = viewModel::resetAnalysis
                    )
                }
            }
        }
    }
}

@Composable
private fun CodeInputView(
    code: String,
    onCodeChange: (String) -> Unit,
    onAnalyze: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Input Area
        OutlinedTextField(
            value = code,
            onValueChange = onCodeChange,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            placeholder = {
                Text(
                    text = "Paste your solution code here (Python, C++, Java, JS)...",
                    color = TextDisabled
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BackgroundCard,
                unfocusedContainerColor = BackgroundCard,
                focusedBorderColor = InfoBlueText,
                unfocusedBorderColor = CardBorder,
                focusedTextColor = TextSecondary,
                unfocusedTextColor = TextSecondary,
                cursorColor = InfoBlueText
            ),
            shape = RoundedCornerShape(12.dp),
            textStyle = LocalTextStyle.current.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
            )
        )
        
        // Analyze Button
        Button(
            onClick = onAnalyze,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            enabled = code.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = InfoBlueText,
                disabledContainerColor = InfoBlueText.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Review My Code",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = InfoBlueText
            )
            Text(
                text = "Analyzing your code...",
                fontSize = 16.sp,
                color = TextTertiary
            )
        }
    }
}

@Composable
private fun AnalysisResultView(
    analysis: CodeAnalysis,
    onAnalyzeAnother: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Status Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (analysis.isOptimal) 
                    Color(0xFF14532D).copy(alpha = 0.2f) 
                else 
                    Color(0xFF78350F).copy(alpha = 0.2f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = if (analysis.isOptimal) 
                            Color(0xFF14532D) 
                        else 
                            Color(0xFF78350F),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = if (analysis.isOptimal) 
                        Icons.Default.CheckCircle 
                    else 
                        Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (analysis.isOptimal)
                        SuccessGreenText
                    else
                        WarningYellowText,
                    modifier = Modifier.size(24.dp)
                )
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (analysis.isOptimal)
                            "Optimal Solution!"
                        else
                            "Can be Optimized",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (analysis.isOptimal) SuccessGreenText else WarningYellowText
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = analysis.feedback,
                        fontSize = 14.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }
        }
        
        // Complexity Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ComplexityCard(
                label = "Time Complexity",
                value = analysis.timeComplexity,
                color = Color(0xFF60A5FA),
                modifier = Modifier.weight(1f)
            )
            
            ComplexityCard(
                label = "Space Complexity",
                value = analysis.spaceComplexity,
                color = Color(0xFFA78BFA),
                modifier = Modifier.weight(1f)
            )
        }
        
        // Pattern Section
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "DETECTED PATTERN",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary,
                letterSpacing = 1.sp
            )

            Surface(
                color = BackgroundCard,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Text(
                    text = analysis.pattern,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    fontSize = 14.sp,
                    color = InfoBlueText
                )
            }
        }
        
        // Optimal Approach (if not optimal)
        if (!analysis.isOptimal && !analysis.optimalApproach.isNullOrBlank()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "OPTIMAL APPROACH",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextTertiary,
                    letterSpacing = 1.sp
                )

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = BackgroundCard.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = analysis.optimalApproach,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        fontSize = 14.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }
        }
        
        // Refactored Code
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "REFACTORED / CLEANER VERSION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary,
                letterSpacing = 1.sp
            )

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = BackgroundCard
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(8.dp))
                        .horizontalScroll(rememberScrollState())
                ) {
                    Text(
                        text = analysis.refactoredCode,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Monospace,
                        color = TextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Analyze Another Button
        Button(
            onClick = onAnalyzeAnother,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CardElevated
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Analyze Another",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ComplexityCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = CardElevated
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label.uppercase(),
                fontSize = 11.sp,
                color = TextDisabled,
                letterSpacing = 0.5.sp
            )
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = color
            )
        }
    }
}

@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = ErrorRedText
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            fontSize = 16.sp,
            color = ErrorRedText,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
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