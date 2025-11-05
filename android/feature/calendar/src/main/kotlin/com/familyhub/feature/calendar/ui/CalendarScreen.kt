package com.familyhub.feature.calendar.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.familyhub.core.domain.model.CalendarEvent
import com.familyhub.feature.calendar.viewmodel.CalendarUiState
import com.familyhub.feature.calendar.viewmodel.CalendarViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    familyId: String,
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val successMessage by viewModel.successMessage.collectAsStateWithLifecycle()

    LaunchedEffect(familyId) {
        val startDate = LocalDateTime.now().minusMonths(1)
        val endDate = LocalDateTime.now().plusMonths(1)
        viewModel.loadEvents(familyId, startDate, endDate)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(successMessage, errorMessage) {
        when {
            successMessage.isNotEmpty() -> {
                snackbarHostState.showSnackbar(successMessage)
                viewModel.clearMessages()
            }
            errorMessage.isNotEmpty() -> {
                snackbarHostState.showSnackbar(errorMessage)
                viewModel.clearMessages()
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Family Calendar") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Show create event dialog */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add Event")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { padding ->
        when (val state = uiState) {
            is CalendarUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is CalendarUiState.Success -> {
                if (state.events.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Icon(
                                Icons.Default.Event,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text("No events yet", style = MaterialTheme.typography.headlineMedium)
                            Text("Tap + to create your first event", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.events, key = { it.id }) { event ->
                            EventCard(event = event)
                        }
                    }
                }
            }
            is CalendarUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Button(onClick = { viewModel.loadEvents(familyId, LocalDateTime.now().minusMonths(1), LocalDateTime.now().plusMonths(1)) }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventCard(event: CalendarEvent, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = event.title, style = MaterialTheme.typography.titleMedium)
            if (event.description.isNotBlank()) {
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AssistChip(
                    onClick = { },
                    label = {
                        Text(event.startTime.format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")))
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                )
            }
        }
    }
}
