package com.familyhub.wear.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*

/**
 * TasksScreen
 * Quick task viewing and completion for Wear OS
 */
@Composable
fun TasksScreen(
    modifier: Modifier = Modifier
) {
    // Mock data for demonstration
    val tasks = remember {
        listOf(
            WearTask("1", "Buy groceries", false, "HIGH"),
            WearTask("2", "Clean kitchen", true, "MEDIUM"),
            WearTask("3", "Call mom", false, "HIGH"),
            WearTask("4", "Pay bills", false, "URGENT"),
            WearTask("5", "Exercise", false, "MEDIUM")
        )
    }

    var taskStates by remember { mutableStateOf(tasks.associate { it.id to it.isCompleted }) }

    ScalingLazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 32.dp,
            start = 10.dp,
            end = 10.dp,
            bottom = 32.dp
        ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            ListHeader {
                Text("My Tasks")
            }
        }

        items(tasks.size) { index ->
            val task = tasks[index]
            val isCompleted = taskStates[task.id] ?: task.isCompleted

            ToggleChip(
                checked = isCompleted,
                onCheckedChange = { checked ->
                    taskStates = taskStates + (task.id to checked)
                },
                label = {
                    Text(
                        text = task.title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                toggleControl = {
                    Icon(
                        imageVector = if (isCompleted) {
                            androidx.wear.compose.material.icons.Icons.Rounded.Done
                        } else {
                            androidx.wear.compose.material.icons.Icons.Rounded.RadioButtonUnchecked
                        },
                        contentDescription = if (isCompleted) "Completed" else "Not completed"
                    )
                },
                colors = ToggleChipDefaults.toggleChipColors(
                    checkedStartBackgroundColor = MaterialTheme.colors.surface
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Chip(
                onClick = { /* TODO: Voice input to add task */ },
                label = {
                    Text("Add Task", style = MaterialTheme.typography.caption1)
                },
                icon = {
                    Icon(
                        imageVector = androidx.wear.compose.material.icons.Icons.Rounded.Mic,
                        contentDescription = "Voice input"
                    )
                },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }
    }
}

data class WearTask(
    val id: String,
    val title: String,
    val isCompleted: Boolean,
    val priority: String
)
