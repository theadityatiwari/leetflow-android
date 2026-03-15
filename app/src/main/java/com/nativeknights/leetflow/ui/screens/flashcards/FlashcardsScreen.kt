package com.nativeknights.leetflow.ui.screens.flashcards

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.nativeknights.leetflow.data.models.RecallNote
import com.nativeknights.leetflow.ui.screens.flashcards.components.NoteCard
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardsScreen(
    onNavigateBack: () -> Unit,
    viewModel: FlashcardsViewModel = viewModel()
) {
    val activeTab by viewModel.activeTab.collectAsState()
    val problemTitle by viewModel.problemTitle.collectAsState()
    val solutionInput by viewModel.solutionInput.collectAsState()
    val generationState by viewModel.generationState.collectAsState()
    val allNotes by viewModel.allNotes.collectAsState()
    val viewingNote by viewModel.viewingNote.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "🧠", fontSize = 24.sp)
                        Text("Flashcards")
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
                actions = {
                    if (viewingNote == null && generationState !is GenerationState.Success) {
                        Surface(
                            color = Color(0xFF111827),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .border(1.dp, Color(0xFF374151), RoundedCornerShape(8.dp))
                                    .padding(4.dp)
                            ) {
                                TabButton(
                                    text = "New",
                                    icon = Icons.Default.Add,
                                    isSelected = activeTab == FlashcardTab.GENERATE,
                                    onClick = { viewModel.onTabChange(FlashcardTab.GENERATE) }
                                )
                                TabButton(
                                    text = "Library",
                                    icon = Icons.Default.List,
                                    isSelected = activeTab == FlashcardTab.LIBRARY,
                                    onClick = { viewModel.onTabChange(FlashcardTab.LIBRARY) }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF111827),
                    titleContentColor = Color(0xFFA78BFA),
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = Color(0xFF0F172A)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Show generated note preview
            if (generationState is GenerationState.Success) {
                NoteCard(
                    note = (generationState as GenerationState.Success).note,
                    isPreview = true,
                    onSave = { 
                        viewModel.saveFlashcard((generationState as GenerationState.Success).note)
                    },
                    onDiscard = { viewModel.discardGeneration() }
                )
            }
            // Show viewing note
            else if (viewingNote != null) {
                NoteCard(
                    note = viewingNote!!,
                    isPreview = false,
                    onClose = { viewModel.closeNoteView() }
                )
            }
            // Show main content
            else {
                when (activeTab) {
                    FlashcardTab.GENERATE -> {
                        GeneratorTab(
                            problemTitle = problemTitle,
                            solutionInput = solutionInput,
                            generationState = generationState,
                            onProblemTitleChange = viewModel::onProblemTitleChange,
                            onSolutionInputChange = viewModel::onSolutionInputChange,
                            onGenerate = viewModel::generateFlashcard
                        )
                    }
                    FlashcardTab.LIBRARY -> {
                        LibraryTab(
                            notes = allNotes,
                            onNoteClick = viewModel::viewNote,
                            onDeleteNote = viewModel::deleteNote
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabButton(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) Color(0xFF7C3AED) else Color.Transparent,
        shape = RoundedCornerShape(6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else Color(0xFF9CA3AF),
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = text,
                fontSize = 13.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) Color.White else Color(0xFF9CA3AF)
            )
        }
    }
}

@Composable
private fun GeneratorTab(
    problemTitle: String,
    solutionInput: String,
    generationState: GenerationState,
    onProblemTitleChange: (String) -> Unit,
    onSolutionInputChange: (String) -> Unit,
    onGenerate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
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
            placeholder = { Text("e.g., Two Sum") },
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
            shape = RoundedCornerShape(12.dp)
        )
        
        // Solution Input
        OutlinedTextField(
            value = solutionInput,
            onValueChange = onSolutionInputChange,
            label = { Text("Your Solution / Logic") },
            placeholder = { Text("Paste your code or explain your approach...") },
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
            enabled = problemTitle.isNotBlank() && solutionInput.isNotBlank() && 
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
                    Text(
                        text = generationState.message,
                        fontSize = 13.sp,
                        color = Color(0xFFFCA5A5)
                    )
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
                Text(
                    text = "AI will extract: Key intuition, simple explanation, common mistakes, and future-use facts",
                    fontSize = 12.sp,
                    color = Color(0xFF93C5FD),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun LibraryTab(
    notes: List<RecallNote>,
    onNoteClick: (RecallNote) -> Unit,
    onDeleteNote: (RecallNote) -> Unit
) {
    if (notes.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFF374151),
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = "No flashcards yet",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6B7280)
                )
                Text(
                    text = "Create your first flashcard from the Generate tab",
                    fontSize = 14.sp,
                    color = Color(0xFF4B5563)
                )
            }
        }
    } else {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 320.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(notes) { note ->
                LibraryNoteCard(
                    note = note,
                    onClick = { onNoteClick(note) }
                )
            }
        }
    }
}

@Composable
private fun LibraryNoteCard(
    note: RecallNote,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF111827),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .border(1.dp, Color(0xFF374151), RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = note.problemTitle,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = SimpleDateFormat("MMM dd", Locale.getDefault())
                        .format(Date(note.createdAt)),
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
            
            // Intuition preview
            Text(
                text = note.intuition,
                fontSize = 13.sp,
                color = Color(0xFFFDE68A),
                maxLines = 2,
                lineHeight = 18.sp
            )
            
            // Tags
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                note.tags.take(3).forEach { tag ->
                    Surface(
                        color = Color(0xFF374151),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = tag,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 10.sp,
                            color = Color(0xFF9CA3AF)
                        )
                    }
                }
                if (note.tags.size > 3) {
                    Text(
                        text = "+${note.tags.size - 3}",
                        fontSize = 10.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }
        }
    }
}