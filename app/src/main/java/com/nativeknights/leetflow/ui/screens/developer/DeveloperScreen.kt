package com.nativeknights.leetflow.ui.screens.developer

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nativeknights.leetflow.R
import com.nativeknights.leetflow.ui.theme.*

// ── Update these URLs with your actual profile links ────────────────────────
private const val URL_LINKEDIN  = "https://www.linkedin.com/in/theadityatiwari/"
private const val URL_LEETCODE  = "https://leetcode.com/u/theadityatiwari"
private const val URL_GITHUB    = "https://github.com/theadityatiwari"
private const val URL_INSTAGRAM = "https://instagram.com/theadityatiwari__"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperScreen(onNavigateBack: () -> Unit) {

    val context = LocalContext.current

    // Entrance animation
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "dev_alpha"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "👨‍💻", fontSize = 22.sp)
                        Text(
                            text = "Developer",
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
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
                .alpha(alpha)
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Profile Card ─────────────────────────────────────────────────
            Card(
                colors = CardDefaults.cardColors(containerColor = BackgroundCard),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(20.dp)),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // ── Square Photo (Instagram post ratio 1:1) ───────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF1E3A8A),
                                        Color(0xFF111827)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Subtle glow orbs (matching dashboard style)
                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .offset(x = (-60).dp, y = (-60).dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            PrimaryBlue.copy(alpha = 0.18f),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = CircleShape
                                )
                        )
                        Box(
                            modifier = Modifier
                                .size(160.dp)
                                .offset(x = 70.dp, y = 60.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF7C3AED).copy(alpha = 0.14f),
                                            Color.Transparent
                                        )
                                    ),
                                    shape = CircleShape
                                )
                        )

                        Image(
                            painter = painterResource(id = R.drawable.ic_aditya_dev_img),
                            contentDescription = "Aditya Tiwari",
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            alignment = Alignment.TopCenter,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // ── Name & Role ───────────────────────────────────────────
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Aditya Tiwari",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )

                        // Role pill
                        Surface(
                            color = PrimaryBlue.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(50.dp)
                        ) {
                            Text(
                                text = "⚡  Android Developer · 4+ Yrs",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = InfoBlueText,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // ── AI Tools Chips ────────────────────────────────────
                        Text(
                            text = "AI-POWERED STACK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDisabled,
                            letterSpacing = 1.5.sp
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AiChip(label = "Claude Code", color = Color(0xFF7C3AED))
                            AiChip(label = "Agentic AI", color = Color(0xFF059669))
                            AiChip(label = "MCP", color = Color(0xFFF59E0B))
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // ── Social Links ──────────────────────────────────────
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                        ) {
                            SocialButton(
                                iconRes = R.drawable.ic_linkedin,
                                label = "LinkedIn",
                                brandColor = Color(0xFF0A66C2),
                                url = URL_LINKEDIN,
                                context = context
                            )
                            SocialButton(
                                iconRes = R.drawable.ic_leetcode,
                                label = "LeetCode",
                                brandColor = Color(0xFFFFA116),
                                url = URL_LEETCODE,
                                context = context
                            )
                            SocialButton(
                                iconRes = R.drawable.ic_github,
                                label = "GitHub",
                                brandColor = Color(0xFFE6EDF3),
                                url = URL_GITHUB,
                                context = context
                            )
                            SocialButton(
                                iconRes = R.drawable.ic_instagram,
                                label = "Instagram",
                                brandColor = Color(0xFFE1306C),
                                url = URL_INSTAGRAM,
                                context = context
                            )
                        }
                    }
                }
            }

            // ── About Card ───────────────────────────────────────────────────
            Card(
                colors = CardDefaults.cardColors(containerColor = BackgroundCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = PrimaryBlue.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "🧠", fontSize = 18.sp)
                            }
                        }
                        Text(
                            text = "About the Developer",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    HorizontalDivider(color = CardBorder)

                    Text(
                        text = "Sole Android developer behind Mamily — a 100K+ download maternal healthcare super-app. Ships production features end to end: real-time video calling (Agora SDK), lab booking, 20+ custom Canvas views, and CleverTap instrumentation across 80+ screens.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )

                    Text(
                        text = "Passionate about the future of AI-augmented engineering — using Claude Code, agentic workflows, and Model Context Protocol (MCP) to ship faster and smarter. LeetCode 1500+.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )
                }
            }

            // ── Tech Stack Card ──────────────────────────────────────────────
            Card(
                colors = CardDefaults.cardColors(containerColor = BackgroundCard),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = Color(0xFF059669).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "🛠️", fontSize = 18.sp)
                            }
                        }
                        Text(
                            text = "Tech Stack",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    HorizontalDivider(color = CardBorder)

                    TechRow(label = "Languages", value = "Kotlin · Java")
                    TechRow(label = "UI", value = "Jetpack Compose · Custom Canvas API")
                    TechRow(label = "Architecture", value = "MVVM · Clean Architecture")
                    TechRow(label = "Libraries", value = "Retrofit · Room · Hilt · Agora SDK")
                    TechRow(label = "AI Tools", value = "Gemini SDK · Claude Code · MCP")
                    TechRow(label = "Tools", value = "Android Studio · Firebase · Figma")
                }
            }

            // ── Built With Section ───────────────────────────────────────────
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF7C3AED).copy(alpha = 0.08f)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            Color(0xFF7C3AED).copy(alpha = 0.3f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = "✨", fontSize = 22.sp)
                    Column {
                        Text(
                            text = "LeetFlow is built by Aditya",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = PurpleText
                        )
                        Text(
                            text = "Solo-shipped · AI-powered · Open to feedback",
                            fontSize = 12.sp,
                            color = TextTertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── Helper Composables ───────────────────────────────────────────────────────

@Composable
private fun AiChip(label: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(50.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

@Composable
private fun SocialButton(
    iconRes: Int,
    label: String,
    brandColor: Color,
    url: String,
    context: android.content.Context
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    ) {
        Surface(
            color = brandColor.copy(alpha = 0.15f),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .size(52.dp)
                .border(1.dp, brandColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = label,
                    tint = brandColor,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Text(
            text = label,
            fontSize = 10.sp,
            color = TextTertiary,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TechRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextTertiary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(90.dp)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End
        )
    }
}
