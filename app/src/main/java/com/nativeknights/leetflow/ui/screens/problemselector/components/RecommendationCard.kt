package com.nativeknights.leetflow.ui.screens.problemselector.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nativeknights.leetflow.data.models.ProblemRecommendation

@Composable
fun RecommendationCard(
    recommendation: ProblemRecommendation,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937) // gray-800/50
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Row: Difficulty + Topic
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Difficulty Badge
                val (bgColor, textColor) = when (recommendation.difficulty) {
                    "Easy" -> Color(0xFF10B981).copy(alpha = 0.2f) to Color(0xFF34D399)
                    "Medium" -> Color(0xFFF59E0B).copy(alpha = 0.2f) to Color(0xFFFBBF24)
                    "Hard" -> Color(0xFFEF4444).copy(alpha = 0.2f) to Color(0xFFF87171)
                    else -> Color(0xFF6B7280).copy(alpha = 0.2f) to Color(0xFF9CA3AF)
                }
                
                Surface(
                    color = bgColor,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = recommendation.difficulty,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }
                
                // Topic Tag
                Text(
                    text = recommendation.topic.uppercase(),
                    fontSize = 12.sp,
                    color = Color(0xFF6B7280),
                    letterSpacing = 1.2.sp
                )
            }
            
            // Problem Title
            Text(
                text = recommendation.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            // Description
            Text(
                text = recommendation.description,
                fontSize = 15.sp,
                color = Color(0xFFD1D5DB),
                lineHeight = 22.sp
            )
            
            // Reason Box
            InfoBox(
                title = "Why this problem?",
                content = recommendation.reason,
                backgroundColor = Color(0xFF1E3A8A).copy(alpha = 0.2f),
                borderColor = Color(0xFF1E3A8A).copy(alpha = 0.5f),
                titleColor = Color(0xFF60A5FA)
            )
            
            // Skill Builder Box
            InfoBox(
                title = "Skill Builder",
                content = recommendation.skillBuilder,
                backgroundColor = Color(0xFF581C87).copy(alpha = 0.2f),
                borderColor = Color(0xFF581C87).copy(alpha = 0.5f),
                titleColor = Color(0xFFA78BFA)
            )
        }
    }
}

@Composable
private fun InfoBox(
    title: String,
    content: String,
    backgroundColor: Color,
    borderColor: Color,
    titleColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = titleColor
            )
            Text(
                text = content,
                fontSize = 13.sp,
                color = Color(0xFFD1D5DB),
                lineHeight = 18.sp
            )
        }
    }
}