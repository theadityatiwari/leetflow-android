package com.nativeknights.leetflow.ui.screens.problemselector.components

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
import com.nativeknights.leetflow.ui.theme.*

@Composable
fun RecommendationCard(
    recommendation: ProblemRecommendation,
    modifier: Modifier = Modifier
) {
    val diffAccent = when (recommendation.difficulty) {
        "Easy"   -> DifficultyEasyText
        "Medium" -> DifficultyMediumText
        "Hard"   -> DifficultyHardText
        else     -> TextTertiary
    }
    val diffBg = when (recommendation.difficulty) {
        "Easy"   -> DifficultyEasyBg.copy(alpha = 0.15f)
        "Medium" -> DifficultyMediumBg.copy(alpha = 0.15f)
        "Hard"   -> DifficultyHardBg.copy(alpha = 0.15f)
        else     -> CardBorder.copy(alpha = 0.15f)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
        ) {
            // Colored accent bar — difficulty color
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(diffAccent)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Difficulty badge + topic
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = diffBg,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = recommendation.difficulty,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = diffAccent
                        )
                    }

                    Text(
                        text = recommendation.topic.uppercase(),
                        fontSize = 11.sp,
                        color = TextTertiary,
                        letterSpacing = 1.sp
                    )
                }

                // Problem title
                Text(
                    text = recommendation.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    lineHeight = 28.sp
                )

                // Short description
                Text(
                    text = recommendation.description,
                    fontSize = 14.sp,
                    color = TextSecondary,
                    lineHeight = 21.sp
                )

                Divider(color = CardBorder)

                InfoBox(
                    icon = "🎯",
                    title = "Why this problem?",
                    content = recommendation.reason,
                    accentColor = InfoBlueText
                )

                InfoBox(
                    icon = "🧩",
                    title = "What you'll learn",
                    content = recommendation.skillBuilder,
                    accentColor = PurpleText
                )
            }
        }
    }
}

@Composable
private fun InfoBox(
    icon: String,
    title: String,
    content: String,
    accentColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = accentColor.copy(alpha = 0.08f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, accentColor.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(icon, fontSize = 16.sp, modifier = Modifier.padding(top = 1.dp))
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                Text(
                    text = content,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    lineHeight = 19.sp
                )
            }
        }
    }
}
