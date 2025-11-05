package com.familyhub.wear.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*

/**
 * MessagesScreen
 * Quick view of family messages on Wear OS
 */
@Composable
fun MessagesScreen(
    modifier: Modifier = Modifier
) {
    // Mock messages
    val messages = remember {
        listOf(
            WearMessage("1", "Mom", "Don't forget to buy milk!", "5m ago"),
            WearMessage("2", "Dad", "Running late, start dinner without me", "15m ago"),
            WearMessage("3", "Sister", "Can you pick me up at 5?", "1h ago"),
            WearMessage("4", "Brother", "Game night tonight?", "2h ago")
        )
    }

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
                Text("Messages")
            }
        }

        items(messages.size) { index ->
            val message = messages[index]

            TitleCard(
                onClick = { /* TODO: Show full message or quick replies */ },
                title = { Text(message.sender, style = MaterialTheme.typography.title3) },
                time = { Text(message.time, style = MaterialTheme.typography.caption2) }
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.body2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Chip(
                onClick = { /* TODO: Voice reply */ },
                label = {
                    Text("Reply", style = MaterialTheme.typography.caption1)
                },
                icon = {
                    Icon(
                        imageVector = androidx.wear.compose.material.icons.Icons.Rounded.Mic,
                        contentDescription = "Voice reply"
                    )
                },
                colors = ChipDefaults.primaryChipColors(),
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }
    }
}

data class WearMessage(
    val id: String,
    val sender: String,
    val content: String,
    val time: String
)
