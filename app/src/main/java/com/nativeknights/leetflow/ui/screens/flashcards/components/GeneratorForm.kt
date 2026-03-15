package com.nativeknights.leetflow.ui.screens.flashcards.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nativeknights.leetflow.ui.screens.flashcards.GenerationState

/**
 * Reusable Generator Form Component
 * Can be used independently or within GeneratorTab
 */
@Composable
fun GeneratorForm(
    problemTitle: String,
    solutionInput: String,
    generationState: GenerationState,
    onProblemTitleChange: (String) -> Unit,
    onSolutionInputChange: (String) -> Unit,
    onGenerate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Text(
            text = "Generate AI Flashcard",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        Text(
            text = "Paste your solution and let AI create a recall card for you",
            fontSize = 14.sp,
            color = Color(0xFF9CA3AF)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Problem Title Input
        OutlinedTextField(
            value = problemTitle,
            onValueChange = onProblemTitleChange,
            label = { Text("Problem Title") },
            placeholder = { Text("e.g., Two Sum, Longest Substring") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7C3AED),
                unfocusedBorderColor = Color(0xFF374151),
                focusedLabelColor = Color(0xFF7C3AED),
                unfocusedLabelColor = Color(0xFF9CA3AF),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFF7C3AED)
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
        
        // Solution Input
        OutlinedTextField(
            value = solutionInput,
            onValueChange = onSolutionInputChange,
            label = { Text("Your Solution / Logic") },
            placeholder = { 
                Text(
                    """Paste your code or explain your approach...
                    
Example:
def twoSum(nums, target):
    seen = {}
    for i, num in enumerate(nums):
        diff = target - num
        if diff in seen:
            return [seen[diff], i]
        seen[num] = i
                    """.trimIndent()
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF7C3AED),
                unfocusedBorderColor = Color(0xFF374151),
                focusedLabelColor = Color(0xFF7C3AED),
                unfocusedLabelColor = Color(0xFF9CA3AF),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color(0xFF7C3AED)
            ),
            shape = RoundedCornerShape(12.dp),
            maxLines = Int.MAX_VALUE
        )
        
        // Generate Button
        Button(
            onClick = onGenerate,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            enabled = problemTitle.isNotBlank() && 
                     solutionInput.isNotBlank() && 
                     generationState !is GenerationState.Loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7C3AED),
                disabledContainerColor = Color(0xFF374151)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (generationState is GenerationState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Generating...", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            } else {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate Flashcard", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        // Error Display
        if (generationState is GenerationState.Error) {
            Surface(
                color = Color(0xFF7F1D1D).copy(alpha = 0.2f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color(0xFFF87171)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Generation Failed",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF87171)
                        )
                        Text(
                            text = generationState.message,
                            fontSize = 12.sp,
                            color = Color(0xFFFCA5A5)
                        )
                    }
                }
            }
        }
        
        // Info Box
        Surface(
            color = Color(0xFF1E40AF).copy(alpha = 0.1f),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFF60A5FA),
                    modifier = Modifier.size(18.dp)
                )
                Column {
                    Text(
                        text = "What AI will extract:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF93C5FD),
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "• Key intuition (the \"Aha!\" moment)\n• Simple explanation\n• Common mistakes to avoid\n• Future-use facts\n• Relevant tags",
                        fontSize = 11.sp,
                        color = Color(0xFF93C5FD),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}