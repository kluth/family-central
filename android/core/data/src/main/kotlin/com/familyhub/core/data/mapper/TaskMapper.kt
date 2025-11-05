package com.familyhub.core.data.mapper

import com.familyhub.core.data.model.AssigneeDto
import com.familyhub.core.data.model.RecurrenceConfigDto
import com.familyhub.core.data.model.SubtaskDto
import com.familyhub.core.data.model.TaskDto
import com.familyhub.core.domain.model.*
import com.google.firebase.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * TaskMapper
 * Maps between Task domain model and TaskDto
 */
object TaskMapper {

    fun toDomain(dto: TaskDto): Task {
        return Task(
            id = dto.id,
            familyId = dto.familyId,
            title = dto.title,
            description = dto.description,
            status = TaskStatus.valueOf(dto.status),
            priority = TaskPriority.valueOf(dto.priority),
            assignedTo = dto.assignedTo.map { it.toDomain() },
            createdBy = dto.createdBy,
            dueDate = dto.dueDate?.toLocalDateTime(),
            completedAt = dto.completedAt?.toLocalDateTime(),
            createdAt = dto.createdAt.toLocalDateTime(),
            updatedAt = dto.updatedAt.toLocalDateTime(),
            subtasks = dto.subtasks.map { it.toDomain() },
            tags = dto.tags,
            recurrence = dto.recurrence?.toDomain(),
            reminderMinutesBefore = dto.reminderMinutesBefore
        )
    }

    fun toDto(domain: Task): TaskDto {
        return TaskDto(
            id = domain.id,
            familyId = domain.familyId,
            title = domain.title,
            description = domain.description,
            status = domain.status.name,
            priority = domain.priority.name,
            assignedTo = domain.assignedTo.map { it.toDto() },
            createdBy = domain.createdBy,
            dueDate = domain.dueDate?.toTimestamp(),
            completedAt = domain.completedAt?.toTimestamp(),
            createdAt = domain.createdAt.toTimestamp(),
            updatedAt = domain.updatedAt.toTimestamp(),
            subtasks = domain.subtasks.map { it.toDto() },
            tags = domain.tags,
            recurrence = domain.recurrence?.toDto(),
            reminderMinutesBefore = domain.reminderMinutesBefore
        )
    }

    // Extension functions for nested models
    private fun AssigneeDto.toDomain(): Assignee {
        return Assignee(
            userId = userId,
            displayName = displayName,
            photoUrl = photoUrl
        )
    }

    private fun Assignee.toDto(): AssigneeDto {
        return AssigneeDto(
            userId = userId,
            displayName = displayName,
            photoUrl = photoUrl
        )
    }

    private fun SubtaskDto.toDomain(): Subtask {
        return Subtask(
            id = id,
            title = title,
            isCompleted = isCompleted,
            completedBy = completedBy,
            completedAt = completedAt?.toLocalDateTime()
        )
    }

    private fun Subtask.toDto(): SubtaskDto {
        return SubtaskDto(
            id = id,
            title = title,
            isCompleted = isCompleted,
            completedBy = completedBy,
            completedAt = completedAt?.toTimestamp()
        )
    }

    private fun RecurrenceConfigDto.toDomain(): RecurrenceConfig {
        return RecurrenceConfig(
            frequency = RecurrenceFrequency.valueOf(frequency),
            interval = interval,
            endDate = endDate?.toLocalDateTime(),
            daysOfWeek = daysOfWeek
        )
    }

    private fun RecurrenceConfig.toDto(): RecurrenceConfigDto {
        return RecurrenceConfigDto(
            frequency = frequency.name,
            interval = interval,
            endDate = endDate?.toTimestamp(),
            daysOfWeek = daysOfWeek
        )
    }

    // Timestamp conversion helpers
    private fun Timestamp.toLocalDateTime(): LocalDateTime {
        return LocalDateTime.ofInstant(
            Instant.ofEpochSecond(seconds, nanoseconds.toLong()),
            ZoneId.systemDefault()
        )
    }

    private fun LocalDateTime.toTimestamp(): Timestamp {
        val instant = atZone(ZoneId.systemDefault()).toInstant()
        return Timestamp(instant.epochSecond, instant.nano)
    }
}
