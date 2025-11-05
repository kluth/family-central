package com.familyhub.core.domain.usecase.calendar

import com.familyhub.core.common.exception.FamilyHubException
import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.CalendarEvent
import com.familyhub.core.domain.repository.CalendarRepository
import javax.inject.Inject

class CreateEventUseCase @Inject constructor(
    private val calendarRepository: CalendarRepository
) {
    suspend operator fun invoke(event: CalendarEvent): Result<CalendarEvent> {
        if (event.title.isBlank()) {
            return Result.Error(FamilyHubException.ValidationException("Event title cannot be empty"))
        }
        if (event.startTime.isAfter(event.endTime)) {
            return Result.Error(FamilyHubException.ValidationException("End time must be after start time"))
        }
        return calendarRepository.createEvent(event)
    }
}
