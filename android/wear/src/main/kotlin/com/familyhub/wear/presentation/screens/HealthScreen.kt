package com.familyhub.wear.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*

/**
 * HealthScreen
 * Health and fitness tracking for Wear OS
 * Shows steps, heart rate, and activity tracking
 */
@Composable
fun HealthScreen(
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
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            ListHeader {
                Text("Health & Fitness")
            }
        }

        // Steps Card
        item {
            Card(
                onClick = { /* TODO: Show detailed steps */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = androidx.wear.compose.material.icons.Icons.Rounded.DirectionsWalk,
                        contentDescription = "Steps",
                        tint = MaterialTheme.colors.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "7,542",
                        style = MaterialTheme.typography.display3
                    )
                    Text(
                        text = "steps today",
                        style = MaterialTheme.typography.caption1,
                        color = MaterialTheme.colors.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    CompactChip(
                        onClick = { },
                        label = {
                            Text("75% to goal", style = MaterialTheme.typography.caption2)
                        },
                        colors = ChipDefaults.secondaryChipColors()
                    )
                }
            }
        }

        // Heart Rate Card
        item {
            Card(
                onClick = { /* TODO: Show heart rate history */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = androidx.wear.compose.material.icons.Icons.Rounded.Favorite,
                        contentDescription = "Heart rate",
                        tint = MaterialTheme.colors.secondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "72",
                        style = MaterialTheme.typography.display3
                    )
                    Text(
                        text = "bpm • Resting",
                        style = MaterialTheme.typography.caption1,
                        color = MaterialTheme.colors.onSurfaceVariant
                    )
                }
            }
        }

        // Activity Chips
        item {
            Text(
                text = "Quick Activities",
                style = MaterialTheme.typography.title3,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        item {
            Chip(
                onClick = { /* TODO: Start walk tracking */ },
                label = {
                    Text("Start Walk", style = MaterialTheme.typography.title3)
                },
                icon = {
                    Icon(
                        imageVector = androidx.wear.compose.material.icons.Icons.Rounded.DirectionsWalk,
                        contentDescription = "Walk"
                    )
                },
                colors = ChipDefaults.primaryChipColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Chip(
                onClick = { /* TODO: Start run tracking */ },
                label = {
                    Text("Start Run", style = MaterialTheme.typography.title3)
                },
                icon = {
                    Icon(
                        imageVector = androidx.wear.compose.material.icons.Icons.Rounded.DirectionsRun,
                        contentDescription = "Run"
                    )
                },
                colors = ChipDefaults.primaryChipColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Chip(
                onClick = { /* TODO: Start workout */ },
                label = {
                    Text("Workout", style = MaterialTheme.typography.title3)
                },
                icon = {
                    Icon(
                        imageVector = androidx.wear.compose.material.icons.Icons.Rounded.FitnessCenter,
                        contentDescription = "Workout"
                    )
                },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
