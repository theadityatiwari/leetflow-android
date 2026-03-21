package com.nativeknights.leetflow.ui.screens.problemselector

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
                        Text(
                            text = "⚡",
                            fontSize = 24.sp
                        )
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // Description
            Text(
                text = "Don't know what to solve? Let AI decide based on your gaps.",
                fontSize = 15.sp,
                color = TextTertiary,
                lineHeight = 22.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Main Content
            when (state) {
                is RecommendationState.Idle -> {
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
                            
                            // Action Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Accept Button
                                Button(
                                    onClick = {
                                        // Open LeetCode link
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(recommendation.url))
                                        context.startActivity(intent)
                                        viewModel.acceptRecommendation()
                                    },
                                    modifier = Modifier.weight(1f).height(56.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SuccessGreen
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowForward,
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
                                
                                // Reroll Button
                                OutlinedButton(
                                    onClick = viewModel::rerollRecommendation,
                                    modifier = Modifier.height(56.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = CardElevated,
                                        contentColor = TextSecondary
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Reroll", fontSize = 14.sp)
                                }
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
private fun InputSection(
    preference: String,
    onPreferenceChange: (String) -> Unit,
    onGetRecommendation: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Input Field
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Any specific constraint? (Optional)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = TextSecondary
            )

            OutlinedTextField(
                value = preference,
                onValueChange = onPreferenceChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = "e.g., 'Only 20 mins', 'Focus on Graphs', 'Hard DP only'",
                        color = TextDisabled
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
                shape = RoundedCornerShape(12.dp)
            )
        }
        
        // Get Recommendation Button
        Button(
            onClick = onGetRecommendation,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryBlue
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tell Me What To Solve",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun LoadingSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CardElevated
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = PrimaryBlue
            )
            Text(
                text = "Analyzing Gaps...",
                fontSize = 16.sp,
                color = TextTertiary
            )
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
        colors = CardDefaults.cardColors(
            containerColor = ErrorRedBg.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "⚠️",
                fontSize = 48.sp
            )
            Text(
                text = message,
                fontSize = 15.sp,
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