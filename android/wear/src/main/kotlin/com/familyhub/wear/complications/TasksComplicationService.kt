package com.familyhub.wear.complications

import android.graphics.drawable.Icon
import androidx.wear.watchface.complications.data.*
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService
import com.familyhub.wear.R

/**
 * TasksComplicationService
 * Provides task count and status information for watch face complications
 * Supports SHORT_TEXT, LONG_TEXT, and RANGED_VALUE types
 */
class TasksComplicationService : SuspendingComplicationDataSourceService() {

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return when (type) {
            ComplicationType.SHORT_TEXT -> createShortTextComplication(5)
            ComplicationType.LONG_TEXT -> createLongTextComplication(5, 12)
            ComplicationType.RANGED_VALUE -> createRangedValueComplication(5, 12)
            else -> null
        }
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {
        // In a real implementation, this would fetch from repository
        val activeTasks = getMockActiveTasks()
        val totalTasks = getMockTotalTasks()

        return when (request.complicationType) {
            ComplicationType.SHORT_TEXT -> createShortTextComplication(activeTasks)
            ComplicationType.LONG_TEXT -> createLongTextComplication(activeTasks, totalTasks)
            ComplicationType.RANGED_VALUE -> createRangedValueComplication(activeTasks, totalTasks)
            else -> null
        }
    }

    private fun createShortTextComplication(activeTasks: Int): ComplicationData {
        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(activeTasks.toString()).build(),
            contentDescription = PlainComplicationText.Builder(
                "$activeTasks active tasks"
            ).build()
        )
            .setTitle(PlainComplicationText.Builder("Tasks").build())
            .setMonochromaticImage(
                MonochromaticImage.Builder(
                    Icon.createWithResource(this, android.R.drawable.ic_menu_agenda)
                ).build()
            )
            .setTapAction(createTapAction())
            .build()
    }

    private fun createLongTextComplication(activeTasks: Int, totalTasks: Int): ComplicationData {
        return LongTextComplicationData.Builder(
            text = PlainComplicationText.Builder("$activeTasks of $totalTasks tasks").build(),
            contentDescription = PlainComplicationText.Builder(
                "$activeTasks of $totalTasks tasks active"
            ).build()
        )
            .setTitle(PlainComplicationText.Builder("Active Tasks").build())
            .setMonochromaticImage(
                MonochromaticImage.Builder(
                    Icon.createWithResource(this, android.R.drawable.ic_menu_agenda)
                ).build()
            )
            .setTapAction(createTapAction())
            .build()
    }

    private fun createRangedValueComplication(activeTasks: Int, totalTasks: Int): ComplicationData {
        return RangedValueComplicationData.Builder(
            value = activeTasks.toFloat(),
            min = 0f,
            max = totalTasks.toFloat(),
            contentDescription = PlainComplicationText.Builder(
                "$activeTasks of $totalTasks tasks"
            ).build()
        )
            .setText(PlainComplicationText.Builder(activeTasks.toString()).build())
            .setTitle(PlainComplicationText.Builder("Tasks").build())
            .setMonochromaticImage(
                MonochromaticImage.Builder(
                    Icon.createWithResource(this, android.R.drawable.ic_menu_agenda)
                ).build()
            )
            .setTapAction(createTapAction())
            .build()
    }

    private fun createTapAction(): android.app.PendingIntent {
        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            putExtra("navigate_to", "tasks")
        }
        return android.app.PendingIntent.getActivity(
            this,
            0,
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getMockActiveTasks(): Int {
        // In a real implementation, this would query the repository
        return 5
    }

    private fun getMockTotalTasks(): Int {
        // In a real implementation, this would query the repository
        return 12
    }
}
