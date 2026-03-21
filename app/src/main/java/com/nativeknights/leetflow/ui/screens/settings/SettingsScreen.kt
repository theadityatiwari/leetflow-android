package com.nativeknights.leetflow.ui.screens.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nativeknights.leetflow.R
import com.nativeknights.leetflow.ui.screens.dashboard.SectionHeader
import com.nativeknights.leetflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToDeveloper: () -> Unit = {},
    viewModel: SettingsViewModel = viewModel()
) {
    val updateState by viewModel.updateState.collectAsState()
    val apiKeyInput by viewModel.apiKeyInput.collectAsState()
    val currentApiKey by viewModel.currentApiKey.collectAsState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val privacyPolicyUrl =
        "https://continuous-marquis-bbf.notion.site/Privacy-Policy-for-LeetFlow-3266854374568005bdd9e70d97039ef0"

    var isKeyVisible by remember { mutableStateOf(false) }

    // Navigate back on success after delay
    LaunchedEffect(updateState) {
        if (updateState is ApiKeyUpdateState.Success) {
            kotlinx.coroutines.delay(2000)
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "⚙️", fontSize = 24.sp)
                        Text("Settings")
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            // ── Current API Key Section ──────────────────────────────────────
            Card(
                colors = CardDefaults.cardColors(containerColor = BackgroundCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = PrimaryBlue.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Current API Key",
                                fontSize = 13.sp,
                                color = TextTertiary,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = currentApiKey,
                                fontSize = 14.sp,
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Divider(color = CardBorder)

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = InfoBlueText,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Update your Google AI Studio API key below",
                            fontSize = 12.sp,
                            color = TextTertiary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            // ── Update API Key Section ───────────────────────────────────────
            Card(
                colors = CardDefaults.cardColors(containerColor = BackgroundCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Update API Key",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = viewModel::onApiKeyChange,
                        label = { Text("New API Key") },
                        placeholder = { Text("AIza...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (isKeyVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isKeyVisible = !isKeyVisible }) {
                                Icon(
                                    painter = painterResource(
                                        id = if (isKeyVisible)
                                            R.drawable.ic_visibility_on_v2
                                        else
                                            R.drawable.ic_visibility_off_v2
                                    ),
                                    contentDescription = if (isKeyVisible) "Hide key" else "Show key"
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                viewModel.validateAndUpdateApiKey()
                            }
                        ),
                        isError = updateState is ApiKeyUpdateState.Error,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CardElevated,
                            unfocusedContainerColor = CardElevated,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = CardBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = PrimaryBlue,
                            errorBorderColor = ErrorRed,
                            errorContainerColor = CardElevated
                        ),
                        shape = RoundedCornerShape(12.dp),
                        supportingText = {
                            when (updateState) {
                                is ApiKeyUpdateState.Error -> {
                                    Text(
                                        text = (updateState as ApiKeyUpdateState.Error).message,
                                        color = ErrorRedText
                                    )
                                }
                                else -> {
                                    Text(
                                        text = "Enter a new key to update. Old key remains if validation fails.",
                                        color = TextTertiary,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { viewModel.validateAndUpdateApiKey() },
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            enabled = updateState !is ApiKeyUpdateState.Validating && apiKeyInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue,
                                disabledContainerColor = PrimaryBlue.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            when (updateState) {
                                is ApiKeyUpdateState.Validating -> {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = TextPrimary,
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Validating...", fontSize = 14.sp)
                                }
                                else -> {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Validate & Update", fontSize = 14.sp)
                                }
                            }
                        }

                        OutlinedButton(
                            onClick = { viewModel.resetUpdateState() },
                            modifier = Modifier.height(54.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = TextPrimary
                            ),
                            border = BorderStroke(1.dp, CardBorder),
                            shape = RoundedCornerShape(12.dp),
                            enabled = updateState !is ApiKeyUpdateState.Validating
                        ) {
                            Text("Cancel", fontSize = 14.sp)
                        }
                    }
                }
            }

            // ── Success Message ──────────────────────────────────────────────
            AnimatedVisibility(
                visible = updateState is ApiKeyUpdateState.Success,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = SuccessGreen.copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, SuccessGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = (updateState as? ApiKeyUpdateState.Success)?.message
                                    ?: "Success!",
                                color = SuccessGreenText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Redirecting to dashboard...",
                                color = TextTertiary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // ── Help Section ─────────────────────────────────────────────────
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = InfoBlueBg.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, InfoBlueBg.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = InfoBlueText,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Need Help?",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = InfoBlueText
                        )
                    }

                    Text(
                        text = "Your API key is stored securely and encrypted. If validation fails, your old key will remain active.",
                        fontSize = 13.sp,
                        color = TextTertiary,
                        lineHeight = 18.sp
                    )

                    TextButton(
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://aistudio.google.com/app/apikey")
                            )
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = InfoBlueText
                        )
                    ) {
                        Text(
                            "Get a new API key →",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // ── Legal Section ────────────────────────────────────────────────
            // Section label
            SectionHeader(title = "Legal", accentColor = InfoBlueText)

            Card(
                colors = CardDefaults.cardColors(containerColor = BackgroundCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                ) {

                    // Privacy Policy Row
                    LegalLinkRow(
                        icon = Icons.Default.Info,
                        title = "Privacy Policy",
                        subtitle = "How LeetFlow handles your data",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
                            context.startActivity(intent)
                        }
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = CardBorder
                    )

                    // App Version Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                color = PrimaryBlue.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "App Version",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "LeetFlow - DSA Command Center",
                                    color = Color(0xFF9CA3AF),
                                    fontSize = 12.sp
                                )
                            }
                        }
                        Text(
                            text = "1.0.0",
                            color = Color(0xFF6B7280),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // ── Developer Section ─────────────────────────────────────────────
            SectionHeader(title = "About", accentColor = PurpleText)

            Card(
                colors = CardDefaults.cardColors(containerColor = BackgroundCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PurpleBorder.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .clickable { onNavigateToDeveloper() }
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            color = PurpleBg.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(text = "👨‍💻", fontSize = 18.sp)
                            }
                        }
                        Column {
                            Text(
                                text = "Meet the Developer",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Aditya Tiwari · Android Engineer",
                                color = PurpleText,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Navigate",
                        tint = PurpleText.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── Reusable Legal Link Row ──────────────────────────────────────────────────
@Composable
private fun LegalLinkRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                color = PrimaryBlue.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = Color(0xFF9CA3AF),
                    fontSize = 12.sp
                )
            }
        }
        Icon(
            imageVector = Icons.Default.Build,
            contentDescription = "Opens in browser",
            tint = Color(0xFF6B7280),
            modifier = Modifier.size(16.dp)
        )
    }
}