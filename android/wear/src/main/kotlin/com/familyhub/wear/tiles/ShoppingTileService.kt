package com.familyhub.wear.tiles

import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters
import androidx.wear.protolayout.DimensionBuilders.dp
import androidx.wear.protolayout.LayoutElementBuilders.*
import androidx.wear.protolayout.ModifiersBuilders.*
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.protolayout.TimelineBuilders.TimelineEntry
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.RequestBuilders.TileRequest
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tiles.TileService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

/**
 * ShoppingTileService
 * Provides a glanceable view of the shopping list on the watch face
 * Shows items with completion status and overall progress
 */
class ShoppingTileService : TileService() {

    companion object {
        private const val RESOURCES_VERSION = "1"
        private const val PRIMARY_COLOR = 0xFF2196F3.toInt()
        private const val SECONDARY_COLOR = 0xFF4CAF50.toInt()
        private const val BACKGROUND_COLOR = 0xFF000000.toInt()
        private const val SURFACE_COLOR = 0xFF1E1E1E.toInt()
        private const val COMPLETED_COLOR = 0xFF66BB6A.toInt()
        private const val UNCOMPLETED_COLOR = 0xFF9E9E9E.toInt()
    }

    override fun onTileRequest(requestParams: TileRequest): ListenableFuture<Tile> {
        // In a real implementation, this would fetch from repository
        val items = getMockShoppingItems()
        val completedCount = items.count { it.checked }
        val totalCount = items.size

        return Futures.immediateFuture(
            Tile.Builder()
                .setResourcesVersion(RESOURCES_VERSION)
                .setTileTimeline(
                    Timeline.Builder()
                        .addTimelineEntry(
                            TimelineEntry.Builder()
                                .setLayout(
                                    Layout.Builder()
                                        .setRoot(createTileLayout(items, completedCount, totalCount))
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .build()
        )
    }

    override fun onTileResourcesRequest(requestParams: ResourcesRequest): ListenableFuture<Resources> {
        return Futures.immediateFuture(
            Resources.Builder()
                .setVersion(RESOURCES_VERSION)
                .build()
        )
    }

    private fun createTileLayout(
        items: List<ShoppingItemData>,
        completedCount: Int,
        totalCount: Int
    ): LayoutElement {
        return Box.Builder()
            .setWidth(expand())
            .setHeight(expand())
            .setModifiers(
                Modifiers.Builder()
                    .setBackground(
                        Background.Builder()
                            .setColor(argb(BACKGROUND_COLOR))
                            .build()
                    )
                    .setPadding(
                        Padding.Builder()
                            .setAll(dp(8f))
                            .build()
                    )
                    .build()
            )
            .addContent(
                Column.Builder()
                    .setWidth(expand())
                    .setHeight(expand())
                    .addContent(createHeader())
                    .addContent(createSpacer(4f))
                    .addContent(createProgressIndicator(completedCount, totalCount))
                    .addContent(createSpacer(6f))
                    .apply {
                        items.take(3).forEach { item ->
                            addContent(createShoppingItemRow(item))
                            addContent(createSpacer(4f))
                        }
                    }
                    .build()
            )
            .build()
    }

    private fun createHeader(): LayoutElement {
        return Row.Builder()
            .setWidth(expand())
            .setVerticalAlignment(VERTICAL_ALIGN_CENTER)
            .addContent(
                Text.Builder()
                    .setText("Shopping List")
                    .setFontStyle(
                        androidx.wear.protolayout.LayoutElementBuilders.FontStyle.Builder()
                            .setSize(dp(16f))
                            .setWeight(androidx.wear.protolayout.LayoutElementBuilders.FONT_WEIGHT_BOLD)
                            .setColor(argb(PRIMARY_COLOR))
                            .build()
                    )
                    .build()
            )
            .build()
    }

    private fun createProgressIndicator(completed: Int, total: Int): LayoutElement {
        return Box.Builder()
            .setWidth(expand())
            .setModifiers(
                Modifiers.Builder()
                    .setBackground(
                        Background.Builder()
                            .setColor(argb(SURFACE_COLOR))
                            .setCorner(
                                Corner.Builder()
                                    .setRadius(dp(8f))
                                    .build()
                            )
                            .build()
                    )
                    .setPadding(
                        Padding.Builder()
                            .setAll(dp(8f))
                            .build()
                    )
                    .build()
            )
            .addContent(
                Row.Builder()
                    .setWidth(expand())
                    .setVerticalAlignment(VERTICAL_ALIGN_CENTER)
                    .addContent(
                        Text.Builder()
                            .setText("$completed / $total")
                            .setFontStyle(
                                androidx.wear.protolayout.LayoutElementBuilders.FontStyle.Builder()
                                    .setSize(dp(14f))
                                    .setWeight(androidx.wear.protolayout.LayoutElementBuilders.FONT_WEIGHT_BOLD)
                                    .setColor(argb(COMPLETED_COLOR))
                                    .build()
                            )
                            .build()
                    )
                    .addContent(createSpacer(4f))
                    .addContent(
                        Text.Builder()
                            .setText("completed")
                            .setFontStyle(
                                androidx.wear.protolayout.LayoutElementBuilders.FontStyle.Builder()
                                    .setSize(dp(12f))
                                    .setColor(argb(0xFFBBBBBB.toInt()))
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .build()
    }

    private fun createShoppingItemRow(item: ShoppingItemData): LayoutElement {
        return Box.Builder()
            .setWidth(expand())
            .setModifiers(
                Modifiers.Builder()
                    .setBackground(
                        Background.Builder()
                            .setColor(argb(SURFACE_COLOR))
                            .setCorner(
                                Corner.Builder()
                                    .setRadius(dp(8f))
                                    .build()
                            )
                            .build()
                    )
                    .setPadding(
                        Padding.Builder()
                            .setStart(dp(8f))
                            .setEnd(dp(8f))
                            .setTop(dp(6f))
                            .setBottom(dp(6f))
                            .build()
                    )
                    .setClickable(
                        Clickable.Builder()
                            .setId("item_${item.id}")
                            .setOnClick(
                                ActionBuilders.LaunchAction.Builder()
                                    .setAndroidActivity(
                                        ActionBuilders.AndroidActivity.Builder()
                                            .setPackageName(this.packageName)
                                            .setClassName("com.familyhub.wear.presentation.MainActivity")
                                            .build()
                                    )
                                    .build()
                            )
                            .build()
                    )
                    .build()
            )
            .addContent(
                Row.Builder()
                    .setWidth(expand())
                    .setVerticalAlignment(VERTICAL_ALIGN_CENTER)
                    .addContent(
                        Box.Builder()
                            .setWidth(dp(16f))
                            .setHeight(dp(16f))
                            .setModifiers(
                                Modifiers.Builder()
                                    .setBackground(
                                        Background.Builder()
                                            .setColor(
                                                argb(
                                                    if (item.checked) COMPLETED_COLOR
                                                    else UNCOMPLETED_COLOR
                                                )
                                            )
                                            .setCorner(
                                                Corner.Builder()
                                                    .setRadius(dp(8f))
                                                    .build()
                                            )
                                            .build()
                                    )
                                    .build()
                            )
                            .build()
                    )
                    .addContent(createSpacer(8f))
                    .addContent(
                        Column.Builder()
                            .setWidth(expand())
                            .addContent(
                                Text.Builder()
                                    .setText(item.name)
                                    .setMaxLines(1)
                                    .setFontStyle(
                                        androidx.wear.protolayout.LayoutElementBuilders.FontStyle.Builder()
                                            .setSize(dp(12f))
                                            .setColor(
                                                argb(
                                                    if (item.checked) 0xFF888888.toInt()
                                                    else 0xFFFFFFFF.toInt()
                                                )
                                            )
                                            .build()
                                    )
                                    .build()
                            )
                            .apply {
                                if (item.quantity.isNotEmpty()) {
                                    addContent(
                                        Text.Builder()
                                            .setText(item.quantity)
                                            .setFontStyle(
                                                androidx.wear.protolayout.LayoutElementBuilders.FontStyle.Builder()
                                                    .setSize(dp(10f))
                                                    .setColor(argb(0xFF888888.toInt()))
                                                    .build()
                                            )
                                            .build()
                                    )
                                }
                            }
                            .build()
                    )
                    .build()
            )
            .build()
    }

    private fun createSpacer(heightDp: Float): LayoutElement {
        return Spacer.Builder()
            .setHeight(dp(heightDp))
            .build()
    }

    private fun getMockShoppingItems(): List<ShoppingItemData> {
        return listOf(
            ShoppingItemData("1", "Milk", "2 liters", false),
            ShoppingItemData("2", "Bread", "1 loaf", true),
            ShoppingItemData("3", "Eggs", "12 count", false),
            ShoppingItemData("4", "Apples", "6 pieces", false),
            ShoppingItemData("5", "Cheese", "200g", true)
        )
    }

    data class ShoppingItemData(
        val id: String,
        val name: String,
        val quantity: String,
        val checked: Boolean
    )
}
