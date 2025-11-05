package com.familyhub.feature.tasks.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.familyhub.core.domain.model.Task
import com.familyhub.core.domain.model.TaskPriority
import com.familyhub.core.domain.model.TaskStatus
import com.familyhub.core.ui.components.LoadingIndicator
import com.familyhub.feature.tasks.viewmodel.TaskListUiState
import com.familyhub.feature.tasks.viewmodel.TaskListViewModel
import java.time.format.DateTimeFormatter

/**
 * TaskListScreen
 * Displays list of tasks for a family
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    familyId: String,
    onNavigateToTaskDetail: (String) -> Unit,
    onNavigateToCreateTask: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: TaskListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val successMessage by viewModel.successMessage.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf<TaskStatus?>(null) }

    // Load tasks when screen is first displayed
    LaunchedEffect(familyId) {
        viewModel.loadTasks(familyId)
    }

    // Show snackbar for messages
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
        topBar = {
            TopAppBar(
                title = { Text("Tasks") },
                actions = {
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter tasks")
                    }
                    DropdownMenu(
                        expanded = showFilterMenu,
                        onDismissRequest = { showFilterMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Tasks") },
                            onClick = {
                                selectedFilter = null
                                viewModel.filterByStatus(null)
                                showFilterMenu = false
                            }
                        )
                        TaskStatus.values().forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.name.replace("_", " ")) },
                                onClick = {
                                    selectedFilter = status
                                    viewModel.filterByStatus(status)
                                    showFilterMenu = false
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateTask
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create task")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { paddingValues ->
        when (val state = uiState) {
            is TaskListUiState.Loading -> {
                LoadingIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            is TaskListUiState.Empty -> {
                EmptyTasksView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            is TaskListUiState.Success -> {
                TaskList(
                    tasks = state.tasks,
                    onTaskClick = onNavigateToTaskDetail,
                    onCompleteTask = { taskId ->
                        viewModel.completeTask(taskId, "current-user-id") // TODO: Get real user ID
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
            is TaskListUiState.Error -> {
                ErrorView(
                    message = state.message,
                    onRetry = { viewModel.loadTasks(familyId) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun TaskList(
    tasks: List<Task>,
    onTaskClick: (String) -> Unit,
    onCompleteTask: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tasks, key = { it.id }) { task ->
            TaskItem(
                task = task,
                onClick = { onTaskClick(task.id) },
                onComplete = { onCompleteTask(task.id) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskItem(
    task: Task,
    onClick: () -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.status == TaskStatus.COMPLETED) {
                        TextDecoration.LineThrough
                    } else {
                        null
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (task.description.isNotBlank()) {
                    Text(
                        text = task.description,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Priority badge
                    PriorityBadge(priority = task.priority)

                    // Status badge
                    StatusBadge(status = task.status)

                    // Due date
                    task.dueDate?.let { dueDate ->
                        AssistChip(
                            onClick = { },
                            label = {
                                Text(
                                    text = dueDate.format(DateTimeFormatter.ofPattern("MMM dd")),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
            }

            if (task.status != TaskStatus.COMPLETED) {
                IconButton(onClick = onComplete) {
                    Icon(
                        Icons.Default.CheckCircleOutline,
                        contentDescription = "Complete task",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun PriorityBadge(priority: TaskPriority) {
    val color = when (priority) {
        TaskPriority.URGENT -> MaterialTheme.colorScheme.error
        TaskPriority.HIGH -> MaterialTheme.colorScheme.tertiary
        TaskPriority.MEDIUM -> MaterialTheme.colorScheme.secondary
        TaskPriority.LOW -> MaterialTheme.colorScheme.surfaceVariant
    }

    AssistChip(
        onClick = { },
        label = {
            Text(
                text = priority.name,
                style = MaterialTheme.typography.labelSmall
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = color.copy(alpha = 0.2f)
        )
    )
}

@Composable
private fun StatusBadge(status: TaskStatus) {
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = status.name.replace("_", " "),
                style = MaterialTheme.typography.labelSmall
            )
        }
    )
}

@Composable
private fun EmptyTasksView(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.TaskAlt,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "No tasks yet",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Tap + to create your first task",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorView(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.error
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error
            )
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}
