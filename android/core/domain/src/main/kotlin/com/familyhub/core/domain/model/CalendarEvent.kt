package com.familyhub.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class CalendarEvent(
    val id: String = "",
    val familyId: String,
    val title: String,
    val description: String = "",
    val startTime: LocalDateTime,
    val endTime: LocalDateTime,
    val location: String? = null,
    val createdBy: String,
    val attendees: List<String> = emptyList(),
    val reminders: List<Int> = emptyList(), // Minutes before event
    val isAllDay: Boolean = false,
    val color: String = "#2196F3",
    val recurrence: RecurrenceConfig? = null
) : Parcelable
