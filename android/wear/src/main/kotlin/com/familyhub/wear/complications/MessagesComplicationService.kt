package com.familyhub.wear.complications

import android.graphics.drawable.Icon
import androidx.wear.watchface.complications.data.*
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingComplicationDataSourceService

/**
 * MessagesComplicationService
 * Provides unread message count for watch face complications
 * Supports SHORT_TEXT, LONG_TEXT, and RANGED_VALUE types
 */
class MessagesComplicationService : SuspendingComplicationDataSourceService() {

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return when (type) {
            ComplicationType.SHORT_TEXT -> createShortTextComplication(3)
            ComplicationType.LONG_TEXT -> createLongTextComplication(3)
            ComplicationType.RANGED_VALUE -> createRangedValueComplication(3)
            else -> null
        }
    }

    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationData? {
        // In a real implementation, this would fetch from repository
        val unreadCount = getMockUnreadMessages()

        return when (request.complicationType) {
            ComplicationType.SHORT_TEXT -> createShortTextComplication(unreadCount)
            ComplicationType.LONG_TEXT -> createLongTextComplication(unreadCount)
            ComplicationType.RANGED_VALUE -> createRangedValueComplication(unreadCount)
            else -> null
        }
    }

    private fun createShortTextComplication(unreadCount: Int): ComplicationData {
        return ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(unreadCount.toString()).build(),
            contentDescription = PlainComplicationText.Builder(
                "$unreadCount unread messages"
            ).build()
        )
            .setTitle(PlainComplicationText.Builder("Msgs").build())
            .setMonochromaticImage(
                MonochromaticImage.Builder(
                    Icon.createWithResource(this, android.R.drawable.ic_dialog_email)
                ).build()
            )
            .setTapAction(createTapAction())
            .build()
    }

    private fun createLongTextComplication(unreadCount: Int): ComplicationData {
        return LongTextComplicationData.Builder(
            text = PlainComplicationText.Builder(
                if (unreadCount == 0) "No new messages"
                else "$unreadCount new messages"
            ).build(),
            contentDescription = PlainComplicationText.Builder(
                "$unreadCount unread messages"
            ).build()
        )
            .setTitle(PlainComplicationText.Builder("Family Chat").build())
            .setMonochromaticImage(
                MonochromaticImage.Builder(
                    Icon.createWithResource(this, android.R.drawable.ic_dialog_email)
                ).build()
            )
            .setTapAction(createTapAction())
            .build()
    }

    private fun createRangedValueComplication(unreadCount: Int): ComplicationData {
        // Cap the max at 10 for better visualization
        val maxValue = 10f
        val displayValue = minOf(unreadCount.toFloat(), maxValue)

        return RangedValueComplicationData.Builder(
            value = displayValue,
            min = 0f,
            max = maxValue,
            contentDescription = PlainComplicationText.Builder(
                "$unreadCount unread messages"
            ).build()
        )
            .setText(
                PlainComplicationText.Builder(
                    if (unreadCount > 10) "10+" else unreadCount.toString()
                ).build()
            )
            .setTitle(PlainComplicationText.Builder("Msgs").build())
            .setMonochromaticImage(
                MonochromaticImage.Builder(
                    Icon.createWithResource(this, android.R.drawable.ic_dialog_email)
                ).build()
            )
            .setTapAction(createTapAction())
            .build()
    }

    private fun createTapAction(): android.app.PendingIntent {
        val intent = packageManager.getLaunchIntentForPackage(packageName)?.apply {
            putExtra("navigate_to", "messages")
        }
        return android.app.PendingIntent.getActivity(
            this,
            1, // Different request code from TasksComplicationService
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getMockUnreadMessages(): Int {
        // In a real implementation, this would query the repository
        return 3
    }
}
