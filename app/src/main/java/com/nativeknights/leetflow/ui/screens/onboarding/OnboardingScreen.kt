package com.nativeknights.leetflow.ui.screens.onboarding

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nativeknights.leetflow.R
import com.nativeknights.leetflow.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onNavigateToDashboard: () -> Unit,
    viewModel: OnboardingViewModel = viewModel()
) {
    val apiKeyInput by viewModel.apiKeyInput.collectAsState()
    val validationState by viewModel.validationState.collectAsState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var isKeyVisible by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    // Navigate on success
    LaunchedEffect(validationState) {
        if (validationState is ApiKeyValidationState.Success) {
            kotlinx.coroutines.delay(1500)
            onNavigateToDashboard()
        }
    }

    // Help Dialog
    if (showHelpDialog) {
        HelpDialog(
            onDismiss = { showHelpDialog = false },
            onOpenBrowser = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/app/apikey"))
                context.startActivity(intent)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Welcome to LeetFlow") },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Hero Icon
            Text(
                text = "🔑",
                fontSize = 80.sp
            )

            // Title
            Text(
                text = "Setup Your API Key",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = TextPrimary
            )

            // Description
            Text(
                text = "Enter your Google AI Studio API key to unlock AI-powered features",
                fontSize = 16.sp,
                color = TextTertiary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // API Key Input
            OutlinedTextField(
                value = apiKeyInput,
                onValueChange = viewModel::onApiKeyChange,
                label = { Text("API Key") },
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
                        viewModel.validateAndSaveApiKey()
                    }
                ),
                isError = validationState is ApiKeyValidationState.Error,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BackgroundCard,
                    unfocusedContainerColor = BackgroundCard,
                    focusedBorderColor = PrimaryBlue,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    cursorColor = PrimaryBlue,
                    errorBorderColor = ErrorRed,
                    errorContainerColor = BackgroundCard
                ),
                supportingText = {
                    when (validationState) {
                        is ApiKeyValidationState.Error -> {
                            Text(
                                text = (validationState as ApiKeyValidationState.Error).message,
                                color = ErrorRedText
                            )
                        }
                        else -> {
                            Text(
                                text = "Get your free key from ai.google.dev",
                                color = TextTertiary
                            )
                        }
                    }
                }
            )

            // Validate Button
            Button(
                onClick = { viewModel.validateAndSaveApiKey() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = validationState !is ApiKeyValidationState.Validating,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    disabledContainerColor = PrimaryBlue.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                when (validationState) {
                    is ApiKeyValidationState.Validating -> {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Validating...", color = TextPrimary)
                    }
                    else -> {
                        Text(
                            text = "Validate & Continue",
                            fontSize = 16.sp,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Success Message
            AnimatedVisibility(
                visible = validationState is ApiKeyValidationState.Success,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = SuccessGreenBg.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier.fillMaxWidth(),
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
                        Text(text = "✅", fontSize = 24.sp)
                        Text(
                            text = (validationState as? ApiKeyValidationState.Success)?.message
                                ?: "Success!",
                            color = SuccessGreenText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Help Button
            OutlinedButton(
                onClick = { showHelpDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryBlue
                ),
                border = BorderStroke(1.dp, CardBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("How to get an API key?", fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun HelpDialog(
    onDismiss: () -> Unit,
    onOpenBrowser: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = BackgroundCard
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardElevated)
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🔑", fontSize = 28.sp)
                            Text(
                                text = "Get Your API Key",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextTertiary
                            )
                        }
                    }
                }

                // Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Introduction
                    Text(
                        text = "To get an API key for Google's Gemini models, use Google AI Studio. The platform lets you generate a free key for testing.",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )

                    Divider(color = CardBorder)

                    // Steps Title
                    Text(
                        text = "Steps to Get Your Gemini API Key",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    // Step 1
                    StepCard(
                        number = "1",
                        title = "Go to Google AI Studio",
                        description = "Sign in to the Google AI Studio website with your Google account.",
                        numberColor = PrimaryBlue
                    )

                    // Step 2
                    StepCard(
                        number = "2",
                        title = "Find the API Keys Page",
                        description = "Look for the \"Get API key\" option. It is usually in the left-hand sidebar or the dashboard menu.",
                        numberColor = SuccessGreen
                    )

                    // Step 3
                    StepCard(
                        number = "3",
                        title = "Create a New Key",
                        description = "Click the \"Create API key\" button. Choose an existing Google Cloud project or click \"Create API key in a new project\" to set one up automatically.",
                        numberColor = PurpleText
                    )

                    // Step 4
                    StepCard(
                        number = "4",
                        title = "Copy and Secure Your Key",
                        description = "After the system generates the key, click the copy icon to save it. Keep it secure, as it gives access to your API quota.",
                        numberColor = WarningYellowText
                    )

                    // Open Browser Button
                    Button(
                        onClick = {
                            onOpenBrowser()
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Open Google AI Studio",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Info Note
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = InfoBlueBg.copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, InfoBlueBg.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "ℹ️", fontSize = 16.sp)
                            Text(
                                text = "The API key is free for testing. Make sure to keep it private and never share it publicly.",
                                fontSize = 12.sp,
                                color = InfoBlueText,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepCard(
    number: String,
    title: String,
    description: String,
    numberColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Step Number
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(numberColor.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                .border(1.dp, numberColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = numberColor
            )
        }

        // Step Content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = description,
                fontSize = 13.sp,
                color = TextTertiary,
                lineHeight = 18.sp
            )
        }
    }
}