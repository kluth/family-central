package com.familyhub.feature.chat.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.familyhub.core.domain.model.Message
import com.familyhub.core.ui.components.LoadingIndicator
import com.familyhub.feature.chat.viewmodel.ChatUiState
import com.familyhub.feature.chat.viewmodel.ChatViewModel
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    familyId: String,
    currentUserId: String,
    currentUserName: String,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    var messageText by remember { mutableStateOf("") }

    LaunchedEffect(familyId) {
        viewModel.loadMessages(familyId)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(errorMessage) {
        if (errorMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(errorMessage)
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Family Chat") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 3.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message...") },
                        maxLines = 4
                    )
                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendMessage(familyId, currentUserId, currentUserName, messageText)
                                messageText = ""
                            }
                        },
                        enabled = messageText.isNotBlank()
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send")
                    }
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        when (val state = uiState) {
            is ChatUiState.Loading -> {
                LoadingIndicator(modifier = Modifier.fillMaxSize().padding(paddingValues))
            }
            is ChatUiState.Empty -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No messages yet. Start the conversation!", style = MaterialTheme.typography.bodyLarge)
                }
            }
            is ChatUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    reverseLayout = false
                ) {
                    items(state.messages, key = { it.id }) { message ->
                        MessageBubble(
                            message = message,
                            isCurrentUser = message.senderId == currentUserId
                        )
                    }
                }
            }
            is ChatUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadMessages(familyId) }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MessageBubble(
    message: Message,
    isCurrentUser: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (isCurrentUser) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                }
            ),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isCurrentUser) 16.dp else 4.dp,
                bottomEnd = if (isCurrentUser) 4.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                if (!isCurrentUser) {
                    Text(
                        text = message.senderName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = message.timestamp.format(DateTimeFormatter.ofPattern("HH:mm")),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
