package com.familyhub.core.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * TaskDto
 * Firestore Data Transfer Object for Task
 */
data class TaskDto(
    @DocumentId val id: String = "",
    val familyId: String = "",
    val title: String = "",
    val description: String = "",
    val status: String = "TODO",
    val priority: String = "MEDIUM",
    val assignedTo: List<AssigneeDto> = emptyList(),
    val createdBy: String = "",
    val dueDate: Timestamp? = null,
    val completedAt: Timestamp? = null,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now(),
    val subtasks: List<SubtaskDto> = emptyList(),
    val tags: List<String> = emptyList(),
    val recurrence: RecurrenceConfigDto? = null,
    val reminderMinutesBefore: Int? = null
)

/**
 * AssigneeDto
 * DTO for task assignee
 */
data class AssigneeDto(
    val userId: String = "",
    val displayName: String = "",
    val photoUrl: String? = null
)

/**
 * SubtaskDto
 * DTO for subtask
 */
data class SubtaskDto(
    val id: String = "",
    val title: String = "",
    val isCompleted: Boolean = false,
    val completedBy: String? = null,
    val completedAt: Timestamp? = null
)

/**
 * RecurrenceConfigDto
 * DTO for recurrence configuration
 */
data class RecurrenceConfigDto(
    val frequency: String = "DAILY",
    val interval: Int = 1,
    val endDate: Timestamp? = null,
    val daysOfWeek: List<Int> = emptyList()
)
