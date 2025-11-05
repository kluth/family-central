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
 * TasksTileService
 * Provides a glanceable view of family tasks on the watch face
 * Shows top 3 urgent tasks with priority indicators
 */
class TasksTileService : TileService() {

    companion object {
        private const val RESOURCES_VERSION = "1"
        private const val PRIMARY_COLOR = 0xFF2196F3.toInt()
        private const val BACKGROUND_COLOR = 0xFF000000.toInt()
        private const val SURFACE_COLOR = 0xFF1E1E1E.toInt()
        private const val HIGH_PRIORITY_COLOR = 0xFFE53935.toInt()
        private const val MEDIUM_PRIORITY_COLOR = 0xFFFFA726.toInt()
        private const val LOW_PRIORITY_COLOR = 0xFF66BB6A.toInt()
    }

    override fun onTileRequest(requestParams: TileRequest): ListenableFuture<Tile> {
        // In a real implementation, this would fetch from repository
        val tasks = getMockTasks()

        return Futures.immediateFuture(
            Tile.Builder()
                .setResourcesVersion(RESOURCES_VERSION)
                .setTileTimeline(
                    Timeline.Builder()
                        .addTimelineEntry(
                            TimelineEntry.Builder()
                                .setLayout(
                                    Layout.Builder()
                                        .setRoot(createTileLayout(tasks))
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

    private fun createTileLayout(tasks: List<TaskData>): LayoutElement {
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
                    .apply {
                        tasks.take(3).forEach { task ->
                            addContent(createTaskRow(task))
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
                    .setText("My Tasks")
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

    private fun createTaskRow(task: TaskData): LayoutElement {
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
                            .setId("task_${task.id}")
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
                            .setWidth(dp(4f))
                            .setHeight(dp(24f))
                            .setModifiers(
                                Modifiers.Builder()
                                    .setBackground(
                                        Background.Builder()
                                            .setColor(argb(getPriorityColor(task.priority)))
                                            .setCorner(
                                                Corner.Builder()
                                                    .setRadius(dp(2f))
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
                                    .setText(task.title)
                                    .setMaxLines(2)
                                    .setFontStyle(
                                        androidx.wear.protolayout.LayoutElementBuilders.FontStyle.Builder()
                                            .setSize(dp(12f))
                                            .setColor(argb(0xFFFFFFFF.toInt()))
                                            .build()
                                    )
                                    .build()
                            )
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

    private fun getPriorityColor(priority: String): Int {
        return when (priority) {
            "HIGH" -> HIGH_PRIORITY_COLOR
            "MEDIUM" -> MEDIUM_PRIORITY_COLOR
            "LOW" -> LOW_PRIORITY_COLOR
            else -> LOW_PRIORITY_COLOR
        }
    }

    private fun getMockTasks(): List<TaskData> {
        return listOf(
            TaskData("1", "Buy groceries", "HIGH", false),
            TaskData("2", "Clean kitchen", "MEDIUM", false),
            TaskData("3", "Review documents", "HIGH", false),
            TaskData("4", "Call dentist", "LOW", false)
        )
    }

    data class TaskData(
        val id: String,
        val title: String,
        val priority: String,
        val isCompleted: Boolean
    )
}
