package com.familyhub.core.domain.usecase.task

import com.familyhub.core.common.exception.FamilyHubException
import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * DeleteTaskUseCase
 * Deletes a task with validation
 */
class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(taskId: String): Result<Unit> {
        if (taskId.isBlank()) {
            return Result.Error(
                FamilyHubException.ValidationException("Task ID cannot be empty")
            )
        }
        return taskRepository.deleteTask(taskId)
    }
}
