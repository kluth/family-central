package com.familyhub.wear.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*

/**
 * HomeScreen
 * Main hub for Wear OS app
 */
@Composable
fun HomeScreen(
    onNavigateToTasks: () -> Unit,
    onNavigateToMessages: () -> Unit,
    onNavigateToShopping: () -> Unit,
    onNavigateToHealth: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            Text(
                text = "FamilyHub",
                style = MaterialTheme.typography.display3,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        item {
            Chip(
                onClick = onNavigateToTasks,
                label = {
                    Text("Tasks", style = MaterialTheme.typography.title3)
                },
                icon = {
                    Icon(
                        imageVector = androidx.wear.compose.material.icons.Icons.Rounded.Done,
                        contentDescription = "Tasks"
                    )
                },
                colors = ChipDefaults.primaryChipColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Chip(
                onClick = onNavigateToMessages,
                label = {
                    Text("Messages", style = MaterialTheme.typography.title3)
                },
                icon = {
                    Icon(
                        imageVector = androidx.wear.compose.material.icons.Icons.Rounded.Message,
                        contentDescription = "Messages"
                    )
                },
                colors = ChipDefaults.primaryChipColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Chip(
                onClick = onNavigateToShopping,
                label = {
                    Text("Shopping", style = MaterialTheme.typography.title3)
                },
                icon = {
                    Icon(
                        imageVector = androidx.wear.compose.material.icons.Icons.Rounded.ShoppingBag,
                        contentDescription = "Shopping"
                    )
                },
                colors = ChipDefaults.primaryChipColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Chip(
                onClick = onNavigateToHealth,
                label = {
                    Text("Health", style = MaterialTheme.typography.title3)
                },
                icon = {
                    Icon(
                        imageVector = androidx.wear.compose.material.icons.Icons.Rounded.Favorite,
                        contentDescription = "Health"
                    )
                },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
