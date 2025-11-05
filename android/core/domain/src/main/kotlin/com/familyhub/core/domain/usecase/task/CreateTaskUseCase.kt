package com.familyhub.core.domain.usecase.task

import com.familyhub.core.common.exception.FamilyHubException
import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.Task
import com.familyhub.core.domain.repository.TaskRepository
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * CreateTaskUseCase
 * Creates a new task with validation
 */
class CreateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Result<Task> {
        // Validate title
        if (task.title.isBlank()) {
            return Result.Error(
                FamilyHubException.ValidationException("Task title cannot be empty")
            )
        }

        // Validate familyId
        if (task.familyId.isBlank()) {
            return Result.Error(
                FamilyHubException.ValidationException("Task familyId cannot be empty")
            )
        }

        // Validate createdBy
        if (task.createdBy.isBlank()) {
            return Result.Error(
                FamilyHubException.ValidationException("Task createdBy cannot be empty")
            )
        }

        // Validate due date is not in the past
        task.dueDate?.let { dueDate ->
            if (dueDate.isBefore(LocalDateTime.now())) {
                return Result.Error(
                    FamilyHubException.ValidationException("Task due date cannot be in the past")
                )
            }
        }

        return taskRepository.createTask(task)
    }
}
