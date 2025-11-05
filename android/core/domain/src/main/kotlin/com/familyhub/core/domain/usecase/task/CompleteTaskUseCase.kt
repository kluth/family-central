package com.familyhub.core.domain.usecase.task

import com.familyhub.core.common.exception.FamilyHubException
import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.Task
import com.familyhub.core.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * CompleteTaskUseCase
 * Marks a task as completed with validation
 */
class CompleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: String, completedBy: String): Result<Task> {
        if (taskId.isBlank()) {
            return Result.Error(
                FamilyHubException.ValidationException("Task ID cannot be empty")
            )
        }

        if (completedBy.isBlank()) {
            return Result.Error(
                FamilyHubException.ValidationException("Completed by user ID cannot be empty")
            )
        }

        return taskRepository.completeTask(taskId, completedBy)
    }
}
