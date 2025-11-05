package com.familyhub.core.domain.usecase.task

import com.familyhub.core.common.exception.FamilyHubException
import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.Task
import com.familyhub.core.domain.repository.TaskRepository
import javax.inject.Inject

/**
 * UpdateTaskUseCase
 * Updates an existing task with validation
 */
class UpdateTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    suspend operator fun invoke(task: Task): Result<Task> {
        // Validate task has ID
        if (task.id.isBlank()) {
            return Result.Error(
                FamilyHubException.ValidationException("Task id cannot be empty")
            )
        }

        // Validate title
        if (task.title.isBlank()) {
            return Result.Error(
                FamilyHubException.ValidationException("Task title cannot be empty")
            )
        }

        return taskRepository.updateTask(task)
    }
}
