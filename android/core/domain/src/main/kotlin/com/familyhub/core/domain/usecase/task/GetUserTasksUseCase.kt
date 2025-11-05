package com.familyhub.core.domain.usecase.task

import com.familyhub.core.domain.model.Task
import com.familyhub.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * GetUserTasksUseCase
 * Retrieves tasks assigned to a specific user
 */
class GetUserTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(userId: String, familyId: String): Flow<List<Task>> {
        return taskRepository.getUserTasks(userId, familyId)
    }
}
