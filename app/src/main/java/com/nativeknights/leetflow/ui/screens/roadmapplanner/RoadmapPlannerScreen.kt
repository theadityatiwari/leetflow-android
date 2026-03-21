package com.nativeknights.leetflow.ui.screens.roadmapplanner

import PatternGroup
import RoadmapPlan
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.nativeknights.leetflow.ui.theme.*

private val RoadmapPink = Color(0xFFF9A8D4)
private val RoadmapPinkBg = Color(0xFF831843)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoadmapPlannerScreen(
    onNavigateBack: () -> Unit,
    viewModel: RoadmapPlannerViewModel = viewModel()
) {
    val topicInput by viewModel.topicInput.collectAsState()
    val roadmapState by viewModel.roadmapState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🗺️", fontSize = 24.sp)
                        Text("Topic Roadmap Planner")
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
                    titleContentColor = RoadmapPink,
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
            when (roadmapState) {
                is RoadmapState.Idle -> {
                    TopicInputView(
                        topic = topicInput,
                        suggestedTopics = viewModel.suggestedTopics,
                        onTopicChange = viewModel::onTopicChange,
                        onGenerate = viewModel::generateRoadmap
                    )
                }
                
                is RoadmapState.Loading -> {
                    LoadingView()
                }
                
                is RoadmapState.Success -> {
                    val plan = (roadmapState as RoadmapState.Success).plan
                    RoadmapView(
                        plan = plan,
                        onGenerateNew = viewModel::resetRoadmap
                    )
                }
                
                is RoadmapState.Error -> {
                    ErrorView(
                        message = (roadmapState as RoadmapState.Error).message,
                        onRetry = viewModel::resetRoadmap
                    )
                }
            }
        }
    }
}

@Composable
private fun TopicInputView(
    topic: String,
    suggestedTopics: List<String>,
    onTopicChange: (String) -> Unit,
    onGenerate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "🗺️", fontSize = 64.sp)
            
            Text(
                text = "Pattern-Based Roadmap",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            
            Text(
                text = "Master any topic by learning its core patterns",
                fontSize = 14.sp,
                color = TextTertiary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = topic,
            onValueChange = onTopicChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Enter DSA Topic") },
            placeholder = { Text("e.g., Dynamic Programming, Graphs") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = BackgroundCard,
                unfocusedContainerColor = BackgroundCard,
                focusedBorderColor = RoadmapPink,
                unfocusedBorderColor = CardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = RoadmapPink
            ),
            shape = RoundedCornerShape(12.dp)
        )
        
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "Popular Topics",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextTertiary
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestedTopics) { suggestedTopic ->
                    FilterChip(
                        selected = topic == suggestedTopic,
                        onClick = { onTopicChange(suggestedTopic) },
                        label = {
                            Text(
                                text = suggestedTopic,
                                fontSize = 13.sp
                            )
                        },
                        enabled = true,
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = CardElevated,
                            labelColor = TextSecondary,
                            selectedContainerColor = RoadmapPink.copy(alpha = 0.2f),
                            selectedLabelColor = RoadmapPink
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (topic == suggestedTopic) RoadmapPink else CardBorder
                        )
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = onGenerate,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            enabled = topic.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = RoadmapPink,
                disabledContainerColor = RoadmapPink.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(imageVector = Icons.Default.Star, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Generate Roadmap", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LoadingView() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp), color = RoadmapPink)
            Text(text = "Creating your roadmap...", fontSize = 16.sp, color = TextTertiary)
        }
    }
}

@Composable
private fun RoadmapView(
    plan: RoadmapPlan,
    onGenerateNew: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = RoadmapPinkBg.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, RoadmapPinkBg.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = plan.topic, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = RoadmapPink)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(label = "Patterns", value = plan.stats.totalPatterns.toString(), icon = "🎯")
                        StatItem(label = "Must Know", value = plan.stats.mustKnowPatterns.toString(), icon = "⭐")
                        StatItem(label = "Est. Time", value = plan.stats.estimatedWeeks.split(" ")[0], icon = "⏱️")
                    }
                }
            }
        }
        
        plan.patterns.forEach { pattern ->
            item { PatternCard(pattern = pattern) }
        }
        
        item {
            OutlinedButton(
                onClick = onGenerateNew,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                border = BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Generate New Roadmap", fontSize = 15.sp)
            }
        }
        
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun PatternCard(pattern: PatternGroup) {
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
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = pattern.patternName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    modifier = Modifier.weight(1f)
                )
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (pattern.mustKnow) {
                        Surface(
                            color = WarningYellowBg.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "⭐ Must Know",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarningYellowText
                            )
                        }
                    }
                    
                    val (bgColor, textColor) = when (pattern.difficulty) {
                        "Beginner" -> DifficultyEasyBg.copy(alpha = 0.2f) to DifficultyEasyText
                        "Advanced" -> DifficultyHardBg.copy(alpha = 0.2f) to DifficultyHardText
                        else -> DifficultyMediumBg.copy(alpha = 0.2f) to DifficultyMediumText
                    }
                    
                    Surface(color = bgColor, shape = RoundedCornerShape(6.dp)) {
                        Text(
                            text = pattern.difficulty,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }
                }
            }
            
            if (pattern.description.isNotEmpty()) {
                Text(text = pattern.description, fontSize = 13.sp, color = TextSecondary, lineHeight = 18.sp)
            }
            
            if (pattern.keyConcepts.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "💡", fontSize = 14.sp)
                        Text(text = "Key Concepts", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = InfoBlueText)
                    }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        pattern.keyConcepts.take(5).forEach { concept ->
                            Surface(
                                color = InfoBlueBg.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = concept,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 11.sp,
                                    color = InfoBlueText
                                )
                            }
                        }
                    }
                }
            }
            
            Divider(color = CardBorder)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (pattern.realWorldUse.isNotEmpty()) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "🌍", fontSize = 12.sp)
                            Text(text = "Use Case", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextTertiary)
                        }
                        Text(text = pattern.realWorldUse, fontSize = 11.sp, color = TextDisabled, lineHeight = 14.sp)
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (pattern.companiesAskThis.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🏢", fontSize = 14.sp)
                        Text(text = pattern.companiesAskThis, fontSize = 12.sp, color = SuccessGreen)
                    }
                }
                
                if (pattern.estimatedProblems.isNotEmpty()) {
                    Surface(
                        color = RoadmapPinkBg.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "📝 ${pattern.estimatedProblems}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = RoadmapPink
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, icon: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = icon, fontSize = 20.sp)
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(text = label, fontSize = 11.sp, color = TextTertiary)
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(imageVector = Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(64.dp), tint = ErrorRedText)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = message, fontSize = 16.sp, color = ErrorRedText, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)) {
            Text("Try Again")
        }
    }
}