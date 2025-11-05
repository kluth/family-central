package com.familyhub.feature.calendar.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.CalendarEvent
import com.familyhub.core.domain.usecase.calendar.CreateEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * CalendarViewModel
 * Handles calendar UI state and event management
 */
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val createEventUseCase: CreateEventUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CalendarUiState>(CalendarUiState.Success(emptyList()))
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow("")
    val successMessage: StateFlow<String> = _successMessage.asStateFlow()

    fun loadEvents(familyId: String, startDate: LocalDateTime, endDate: LocalDateTime) {
        // TODO: Implement with repository when ready
        _uiState.value = CalendarUiState.Success(emptyList())
    }

    fun createEvent(
        familyId: String,
        title: String,
        description: String,
        startTime: LocalDateTime,
        endTime: LocalDateTime,
        createdBy: String
    ) {
        viewModelScope.launch {
            val event = CalendarEvent(
                familyId = familyId,
                title = title,
                description = description,
                startTime = startTime,
                endTime = endTime,
                createdBy = createdBy
            )

            when (val result = createEventUseCase(event)) {
                is Result.Success -> {
                    _successMessage.value = "Event created successfully"
                    Timber.d("Event created: ${result.data.id}")
                }
                is Result.Error -> {
                    _errorMessage.value = result.exception.message ?: "Failed to create event"
                    Timber.e(result.exception, "Error creating event")
                }
                is Result.Loading -> {}
            }
        }
    }

    fun clearMessages() {
        _errorMessage.value = ""
        _successMessage.value = ""
    }
}

sealed class CalendarUiState {
    object Loading : CalendarUiState()
    data class Success(val events: List<CalendarEvent>) : CalendarUiState()
    data class Error(val message: String) : CalendarUiState()
}
