package com.nativeknights.leetflow.ui.screens.flashcards.components

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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nativeknights.leetflow.data.models.RecallNote
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NoteCard(
    note: RecallNote,
    isPreview: Boolean,
    onSave: (() -> Unit)? = null,
    onDiscard: (() -> Unit)? = null,
    onClose: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF111827))
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isPreview) {
                    Surface(
                        color = Color(0xFFF59E0B).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "PREVIEW",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFBBF24)
                        )
                    }
                }
                Text(
                    text = note.problemTitle,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            if (!isPreview) {
                IconButton(onClick = { onClose?.invoke() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Close",
                        tint = Color(0xFF9CA3AF)
                    )
                }
            }
        }
        
        // Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .background(Color(0xFF111827), RoundedCornerShape(12.dp))
                .border(1.dp, Color(0xFF374151), RoundedCornerShape(12.dp))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DSA COMMAND CENTER • RECALL CARD",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF6B7280),
                    letterSpacing = 1.sp
                )
                Text(
                    text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        .format(Date(note.createdAt)),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA78BFA)
                )
            }
            
            Divider(color = Color(0xFF374151), thickness = 1.dp)
            
            // Intuition Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFEF3C7).copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFFFBBF24).copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "INTUITION (THE AHA!)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFBBF24),
                            letterSpacing = 0.5.sp
                        )
                    }
                    Text(
                        text = note.intuition,
                        fontSize = 16.sp,
                        color = Color(0xFFFDE68A),
                        lineHeight = 24.sp
                    )
                }
            }
            
            // Explanation Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1F2937), RoundedCornerShape(8.dp))
                    .border(1.dp, Color(0xFF374151), RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "SIMPLE EXPLANATION",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9CA3AF),
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = note.explanation,
                        fontSize = 14.sp,
                        color = Color(0xFFD1D5DB),
                        lineHeight = 20.sp
                    )
                }
            }
            
            // Mistakes and Facts Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Mistakes
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF7F1D1D).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFF7F1D1D).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "MISTAKES TO AVOID",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF87171),
                            letterSpacing = 0.5.sp
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            note.mistakesToAvoid.forEach { mistake ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "•",
                                        fontSize = 13.sp,
                                        color = Color(0xFFFCA5A5)
                                    )
                                    Text(
                                        text = mistake,
                                        fontSize = 12.sp,
                                        color = Color(0xFFFCA5A5),
                                        lineHeight = 18.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Facts
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFF14532D).copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .border(1.dp, Color(0xFF14532D).copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "FUTURE FACTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4ADE80),
                            letterSpacing = 0.5.sp
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            note.futureUseFacts.forEach { fact ->
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "•",
                                        fontSize = 13.sp,
                                        color = Color(0xFF86EFAC)
                                    )
                                    Text(
                                        text = fact,
                                        fontSize = 12.sp,
                                        color = Color(0xFF86EFAC),
                                        lineHeight = 18.sp,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Tags
            Divider(color = Color(0xFF374151), thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                note.tags.forEach { tag ->
                    Surface(
                        color = Color(0xFF374151),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = tag,
                                fontSize = 11.sp,
                                color = Color(0xFFD1D5DB)
                            )
                        }
                    }
                }
            }
        }
        
        // Action Buttons (Preview Only)
        if (isPreview && onSave != null && onDiscard != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onSave,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF059669)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save to Library",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                OutlinedButton(
                    onClick = onDiscard,
                    modifier = Modifier.height(54.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFF374151),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Discard", fontSize = 14.sp)
                }
            }
        }
    }
}