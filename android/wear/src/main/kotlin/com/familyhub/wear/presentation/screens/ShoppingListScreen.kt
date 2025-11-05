package com.familyhub.wear.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*

/**
 * ShoppingListScreen
 * Quick shopping list viewer for Wear OS
 */
@Composable
fun ShoppingListScreen(
    modifier: Modifier = Modifier
) {
    // Mock shopping items
    val items = remember {
        listOf(
            ShoppingItem("1", "Milk", "2 liters", false),
            ShoppingItem("2", "Bread", "1 loaf", true),
            ShoppingItem("3", "Eggs", "12", false),
            ShoppingItem("4", "Apples", "6", false),
            ShoppingItem("5", "Coffee", "1 bag", false)
        )
    }

    var itemStates by remember { mutableStateOf(items.associate { it.id to it.checked }) }

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
                Text("Shopping List")
            }
        }

        item {
            val checkedCount = itemStates.values.count { it }
            val totalCount = items.size
            Text(
                text = "$checkedCount / $totalCount completed",
                style = MaterialTheme.typography.caption1,
                color = MaterialTheme.colors.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(items.size) { index ->
            val item = items[index]
            val isChecked = itemStates[item.id] ?: item.checked

            ToggleChip(
                checked = isChecked,
                onCheckedChange = { checked ->
                    itemStates = itemStates + (item.id to checked)
                },
                label = {
                    Text(
                        text = item.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                secondaryLabel = {
                    Text(
                        text = item.quantity,
                        style = MaterialTheme.typography.caption2
                    )
                },
                toggleControl = {
                    Icon(
                        imageVector = if (isChecked) {
                            androidx.wear.compose.material.icons.Icons.Rounded.Done
                        } else {
                            androidx.wear.compose.material.icons.Icons.Rounded.RadioButtonUnchecked
                        },
                        contentDescription = if (isChecked) "Checked" else "Unchecked"
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
                onClick = { /* TODO: Voice input to add item */ },
                label = {
                    Text("Add Item", style = MaterialTheme.typography.caption1)
                },
                icon = {
                    Icon(
                        imageVector = androidx.wear.compose.material.icons.Icons.Rounded.Add,
                        contentDescription = "Add item"
                    )
                },
                colors = ChipDefaults.secondaryChipColors(),
                modifier = Modifier.fillMaxWidth(0.8f)
            )
        }
    }
}

data class ShoppingItem(
    val id: String,
    val name: String,
    val quantity: String,
    val checked: Boolean
)
