package com.familyhub.core.domain.usecase.task

import com.familyhub.core.domain.model.Task
import com.familyhub.core.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * GetFamilyTasksUseCase
 * Retrieves all tasks for a family
 */
class GetFamilyTasksUseCase @Inject constructor(
    private val taskRepository: TaskRepository
) {
    operator fun invoke(familyId: String): Flow<List<Task>> {
        return taskRepository.getFamilyTasks(familyId)
    }
}
