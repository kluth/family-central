package com.familyhub.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

/**
 * Task domain model
 * Represents a task in the family management system
 */
@Parcelize
data class Task(
    val id: String = "",
    val familyId: String,
    val title: String,
    val description: String = "",
    val status: TaskStatus = TaskStatus.TODO,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val assignedTo: List<Assignee> = emptyList(),
    val createdBy: String,
    val dueDate: LocalDateTime? = null,
    val completedAt: LocalDateTime? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    val subtasks: List<Subtask> = emptyList(),
    val tags: List<String> = emptyList(),
    val recurrence: RecurrenceConfig? = null,
    val reminderMinutesBefore: Int? = null
) : Parcelable {
    val isOverdue: Boolean
        get() = dueDate?.let { it.isBefore(LocalDateTime.now()) && status != TaskStatus.COMPLETED } ?: false

    val completionPercentage: Int
        get() = if (subtasks.isEmpty()) {
            if (status == TaskStatus.COMPLETED) 100 else 0
        } else {
            val completed = subtasks.count { it.isCompleted }
            (completed * 100) / subtasks.size
        }
}

/**
 * Task status enum
 */
enum class TaskStatus {
    TODO,
    IN_PROGRESS,
    BLOCKED,
    COMPLETED,
    CANCELLED
}

/**
 * Task priority enum
 */
enum class TaskPriority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT
}

/**
 * Task assignee
 */
@Parcelize
data class Assignee(
    val userId: String,
    val displayName: String,
    val photoUrl: String? = null
) : Parcelable

/**
 * Subtask model
 */
@Parcelize
data class Subtask(
    val id: String = "",
    val title: String,
    val isCompleted: Boolean = false,
    val completedBy: String? = null,
    val completedAt: LocalDateTime? = null
) : Parcelable

/**
 * Recurrence configuration
 */
@Parcelize
data class RecurrenceConfig(
    val frequency: RecurrenceFrequency,
    val interval: Int = 1,
    val endDate: LocalDateTime? = null,
    val daysOfWeek: List<Int> = emptyList() // 1=Monday, 7=Sunday
) : Parcelable

/**
 * Recurrence frequency
 */
enum class RecurrenceFrequency {
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY
}
