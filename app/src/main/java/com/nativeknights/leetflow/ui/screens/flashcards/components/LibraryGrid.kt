package com.nativeknights.leetflow.ui.screens.flashcards.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nativeknights.leetflow.data.models.RecallNote
import java.text.SimpleDateFormat
import java.util.*

/**
 * Reusable Library Grid Component
 * Displays flashcards in a grid layout with search and filter
 */
@Composable
fun LibraryGrid(
    notes: List<RecallNote>,
    onNoteClick: (RecallNote) -> Unit,
    onDeleteNote: (RecallNote) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf<String?>(null) }
    
    // Filter notes
    val filteredNotes = remember(notes, searchQuery, selectedTag) {
        notes.filter { note ->
            val matchesSearch = searchQuery.isBlank() || 
                note.problemTitle.contains(searchQuery, ignoreCase = true) ||
                note.intuition.contains(searchQuery, ignoreCase = true)
            
            val matchesTag = selectedTag == null || note.tags.contains(selectedTag)
            
            matchesSearch && matchesTag
        }
    }
    
    // Get all unique tags
    val allTags = remember(notes) {
        notes.flatMap { it.tags }.distinct().sorted()
    }
    
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search and Filter Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search flashcards...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = Color(0xFF9CA3AF)
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF7C3AED),
                    unfocusedBorderColor = Color(0xFF374151),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFF7C3AED)
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
            
            // Tag Filter
            if (allTags.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // All filter
                    FilterChip(
                        selected = selectedTag == null,
                        onClick = { selectedTag = null },
                        label = { Text("All (${notes.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF7C3AED),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0xFF374151),
                            labelColor = Color(0xFF9CA3AF)
                        )
                    )
                    
                    // Tag filters (show first 3)
                    allTags.take(3).forEach { tag ->
                        val count = notes.count { it.tags.contains(tag) }
                        FilterChip(
                            selected = selectedTag == tag,
                            onClick = { selectedTag = if (selectedTag == tag) null else tag },
                            label = { Text("$tag ($count)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF7C3AED),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF374151),
                                labelColor = Color(0xFF9CA3AF)
                            )
                        )
                    }
                }
            }
            
            // Results info
            if (searchQuery.isNotEmpty() || selectedTag != null) {
                Text(
                    text = "${filteredNotes.size} flashcard${if (filteredNotes.size != 1) "s" else ""} found",
                    fontSize = 13.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
        }
        
        // Grid or Empty State
        if (filteredNotes.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = if (searchQuery.isNotEmpty() || selectedTag != null) 
                            Icons.Default.Search else Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFF374151),
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = if (searchQuery.isNotEmpty() || selectedTag != null)
                            "No matching flashcards" else "No flashcards yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7280)
                    )
                    Text(
                        text = if (searchQuery.isNotEmpty() || selectedTag != null)
                            "Try a different search or filter" 
                        else 
                            "Create your first flashcard from the Generate tab",
                        fontSize = 14.sp,
                        color = Color(0xFF4B5563)
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 320.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredNotes, key = { it.id }) { note ->
                    LibraryNoteCard(
                        note = note,
                        onClick = { onNoteClick(note) },
                        onDelete = { onDeleteNote(note) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LibraryNoteCard(
    note: RecallNote,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
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
            // Header with delete button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = note.problemTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )
                    Text(
                        text = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                            .format(Date(note.createdAt)),
                        fontSize = 11.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }
                
                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            
            // Intuition preview
            Text(
                text = note.intuition,
                fontSize = 13.sp,
                color = Color(0xFFFDE68A),
                maxLines = 2,
                lineHeight = 18.sp
            )
            
            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatBadge(
                    icon = Icons.Default.Warning,
                    count = note.mistakesToAvoid.size,
                    label = "mistakes",
                    color = Color(0xFFF87171)
                )
                StatBadge(
                    icon = Icons.Default.Star,
                    count = note.futureUseFacts.size,
                    label = "facts",
                    color = Color(0xFF4ADE80)
                )
            }
            
            // Tags
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
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
    
    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Flashcard?") },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFF87171)
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
            containerColor = Color(0xFF1F2937),
            titleContentColor = Color.White,
            textContentColor = Color(0xFFD1D5DB)
        )
    }
}

@Composable
private fun StatBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    label: String,
    color: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = "$count $label",
            fontSize = 11.sp,
            color = color.copy(alpha = 0.8f)
        )
    }
}