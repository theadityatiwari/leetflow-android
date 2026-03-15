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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.nativeknights.leetflow.R
import com.nativeknights.leetflow.ui.navigations.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    viewModel: DashboardViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "${state.greeting} 👋",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Let's crush some problems!",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(Screen.Settings.route)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF111827)
                )
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 1. HERO SECTION: Decision Fatigue Remover (Kept exactly as it was)
            HeroCard(
                onClick = { navController.navigate(Screen.ProblemSelector.route) },
                enabled = state.hasApiKey
            )

            // 2. NEW ROW: Complexity Blitz & Blind Problem Suggester (Replacing Stats/Curriculum)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Complexity Blitz Widget
                ComplexityBlitzWidget(
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.ComplexityBlitz.route) },
                    enabled = state.hasApiKey
                )

                // Blind Problem Suggester Widget
                BlindProblemWidget(
                    modifier = Modifier.weight(1f),
                    onClick = { navController.navigate(Screen.BlindProblem.route) },
                    enabled = state.hasApiKey
                )
            }

            // 4. TOOLS GRID
            Text(
                text = "Quick Tools",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF9CA3AF),
                modifier = Modifier.padding(top = 8.dp)
            )

            ToolCard(
                icon = Icons.Default.LocationOn,
                title = "Topic Roadmap",
                subtitle = "All Important Patterns",
                gradient = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF831843).copy(alpha = 0.3f), Color(0xFF111827))
                ),
                borderColor = Color(0xFF831843).copy(alpha = 0.3f),
                iconBgColor = Color(0xFF831843).copy(alpha = 0.3f),
                iconTint = Color(0xFFF9A8D4),
                onClick = { navController.navigate(Screen.RoadmapPlanner.route) },
                enabled = state.hasApiKey
            )

            ToolCard(
                icon = Icons.Default.Create,
                title = "Code Analyzer",
                subtitle = "Review Quality",
                gradient = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF1E3A8A).copy(alpha = 0.3f), Color(0xFF111827))
                ),
                borderColor = Color(0xFF374151),
                iconBgColor = Color(0xFF1E3A8A).copy(alpha = 0.3f),
                iconTint = Color(0xFF60A5FA),
                onClick = { navController.navigate(Screen.CodeAnalyzer.route) },
                enabled = state.hasApiKey
            )

            ToolCard(
                icon = Icons.Default.Star,
                title = "Recall Notes",
                subtitle = "${state.notesCount} Cards",
                gradient = Brush.horizontalGradient(
                    colors = listOf(Color(0xFF78350F).copy(alpha = 0.3f), Color(0xFF111827))
                ),
                borderColor = Color(0xFF374151),
                iconBgColor = Color(0xFF78350F).copy(alpha = 0.3f),
                iconTint = Color(0xFFFBBF24),
                onClick = { navController.navigate(Screen.FlashCard.route) },
                enabled = true
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ==================== NEW: COMPLEXITY BLITZ WIDGET ====================
@Composable
private fun ComplexityBlitzWidget(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
        shape = RoundedCornerShape(16.dp),
        enabled = enabled
    ) {
        Box(modifier = Modifier.fillMaxSize().border(1.dp, Color(0xFF0891B2).copy(alpha = 0.4f), RoundedCornerShape(16.dp)).padding(16.dp)) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "TRAINING", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF22D3EE), letterSpacing = 1.sp)
                    Text(text = "Complexity", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Text(text = "Blitz Big O", fontSize = 12.sp, color = Color(0xFF10B981))
            }
            Icon(imageVector = Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(48.dp).align(Alignment.TopEnd).offset(x = 10.dp, y = (-10).dp), tint = Color(0xFF22D3EE).copy(alpha = 0.2f))
        }
    }
}

// ==================== NEW: BLIND PROBLEM WIDGET ====================
@Composable
private fun BlindProblemWidget(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(120.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
        shape = RoundedCornerShape(16.dp),
        enabled = enabled
    ) {
        Box(modifier = Modifier.fillMaxSize().border(1.dp, Color(0xFF7C3AED).copy(alpha = 0.4f), RoundedCornerShape(16.dp)).padding(16.dp)) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(text = "CHALLENGE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA78BFA), letterSpacing = 1.sp)
                    Text(text = "Blind", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Text(text = "No Hints mode", fontSize = 12.sp, color = Color(0xFFA78BFA))
            }
            Icon(painterResource(
                id = R.drawable.ic_visibility_off_v2
            ), contentDescription = null, modifier = Modifier.size(48.dp).align(Alignment.TopEnd).offset(x = 10.dp, y = (-10).dp), tint = Color(0xFFA78BFA).copy(alpha = 0.2f))
        }
    }
}

// ==================== HERO CARD (ORIGINAL) ====================
@Composable
private fun HeroCard(
    onClick: () -> Unit,
    enabled: Boolean
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(160.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF111827),
            disabledContainerColor = Color(0xFF111827).copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(20.dp),
        enabled = enabled
    ) {
        Box(modifier = Modifier.fillMaxSize().border(1.dp, Color(0xFF374151), RoundedCornerShape(20.dp)).padding(20.dp)) {
            Text(text = "⚡", fontSize = 80.sp, modifier = Modifier.align(Alignment.TopEnd).offset(x = 10.dp, y = (-10).dp), color = Color.White.copy(alpha = if (enabled) 0.1f else 0.05f))
            Column(modifier = Modifier.fillMaxSize().padding(end = 60.dp), verticalArrangement = Arrangement.SpaceBetween) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(color = Color(0xFF3B82F6).copy(alpha = 0.2f), shape = CircleShape, modifier = Modifier.size(32.dp)) {
                            Box(contentAlignment = Alignment.Center) { Text(text = "⚡", fontSize = 18.sp) }
                        }
                        Text(text = "Decision Fatigue Remover", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = if (enabled) Color.White else Color.White.copy(alpha = 0.5f))
                    }
                    Text(text = "Don't know what to solve? Let AI decide based on your gaps.", fontSize = 13.sp, color = if (enabled) Color(0xFF9CA3AF) else Color(0xFF9CA3AF).copy(alpha = 0.5f), lineHeight = 18.sp)
                }
                if (enabled) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "Get Recommendation", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF3B82F6))
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(16.dp))
                    }
                } else {
                    Surface(color = Color(0xFF7F1D1D).copy(alpha = 0.3f), shape = RoundedCornerShape(6.dp)) {
                        Text(text = "API Key Required", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color(0xFFFCA5A5))
                    }
                }
            }
        }
    }
}

// ==================== AI COACH CARD ====================
@Composable
private fun AICoachCard(enabled: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFF374151), RoundedCornerShape(16.dp)).padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(color = Color(0xFF1E3A8A).copy(alpha = 0.3f), shape = CircleShape, modifier = Modifier.size(48.dp)) {
                    Box(contentAlignment = Alignment.Center) { Text(text = "🤖", fontSize = 24.sp) }
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(text = "AI Progress Coach", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = if (enabled) "Get personalized insights and recommendations" else "Enable API key to unlock AI insights", fontSize = 12.sp, color = Color(0xFF9CA3AF), lineHeight = 16.sp)
                }
                if (enabled) AnimatedStatusDot()
            }
        }
    }
}

@Composable
private fun AnimatedStatusDot() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(initialValue = 1f, targetValue = 1.3f, animationSpec = infiniteRepeatable(animation = tween(1000, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse), label = "scale")
    Box(modifier = Modifier.size(12.dp).background(Color(0xFF10B981), CircleShape).then(Modifier.size((12 * scale).dp).background(Color(0xFF10B981).copy(alpha = 0.3f), CircleShape)))
}

// ==================== TOOL CARD ====================
@Composable
private fun ToolCard(
    icon: ImageVector, title: String, subtitle: String, gradient: Brush, borderColor: Color,
    iconBgColor: Color, iconTint: Color, onClick: () -> Unit, enabled: Boolean
) {
    Card(
        onClick = onClick, modifier = Modifier.fillMaxWidth().height(80.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent),
        shape = RoundedCornerShape(16.dp), enabled = enabled
    ) {
        Box(modifier = Modifier.fillMaxSize().background(gradient).border(width = 1.dp, color = if (enabled) borderColor else borderColor.copy(alpha = 0.3f), shape = RoundedCornerShape(16.dp)).padding(16.dp)) {
            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = if (enabled) iconBgColor else iconBgColor.copy(alpha = 0.3f), shape = RoundedCornerShape(10.dp), modifier = Modifier.size(44.dp)) {
                        Box(contentAlignment = Alignment.Center) { Icon(imageVector = icon, contentDescription = null, tint = if (enabled) iconTint else iconTint.copy(alpha = 0.5f), modifier = Modifier.size(22.dp)) }
                    }
                    Column {
                        Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (enabled) Color.White else Color.White.copy(alpha = 0.5f))
                        Text(text = subtitle, fontSize = 12.sp, color = if (enabled) Color(0xFF9CA3AF) else Color(0xFF9CA3AF).copy(alpha = 0.5f))
                    }
                }
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = if (enabled) Color(0xFF4B5563) else Color(0xFF4B5563).copy(alpha = 0.3f), modifier = Modifier.size(18.dp))
            }
        }
    }
}