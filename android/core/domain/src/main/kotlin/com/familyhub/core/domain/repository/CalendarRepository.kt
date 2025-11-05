package com.familyhub.core.domain.repository

import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.CalendarEvent
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface CalendarRepository {
    fun getFamilyEvents(familyId: String, startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<CalendarEvent>>
    suspend fun createEvent(event: CalendarEvent): Result<CalendarEvent>
    suspend fun updateEvent(event: CalendarEvent): Result<CalendarEvent>
    suspend fun deleteEvent(eventId: String): Result<Unit>
}
