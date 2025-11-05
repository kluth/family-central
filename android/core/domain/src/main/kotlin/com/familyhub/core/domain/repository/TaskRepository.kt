package com.familyhub.core.domain.repository

import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.Task
import com.familyhub.core.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow

/**
 * TaskRepository
 * Repository interface for task data operations
 */
interface TaskRepository {
    /**
     * Get all tasks for a family
     * @param familyId The family ID
     * @return Flow of tasks list
     */
    fun getFamilyTasks(familyId: String): Flow<List<Task>>

    /**
     * Get tasks assigned to a specific user
     * @param userId The user ID
     * @param familyId The family ID
     * @return Flow of tasks list
     */
    fun getUserTasks(userId: String, familyId: String): Flow<List<Task>>

    /**
     * Get tasks by status
     * @param familyId The family ID
     * @param status The task status
     * @return Flow of tasks list
     */
    fun getTasksByStatus(familyId: String, status: TaskStatus): Flow<List<Task>>

    /**
     * Get a single task by ID
     * @param taskId The task ID
     * @return Result with task or error
     */
    suspend fun getTask(taskId: String): Result<Task>

    /**
     * Create a new task
     * @param task The task to create
     * @return Result with created task or error
     */
    suspend fun createTask(task: Task): Result<Task>

    /**
     * Update an existing task
     * @param task The task to update
     * @return Result with updated task or error
     */
    suspend fun updateTask(task: Task): Result<Task>

    /**
     * Delete a task
     * @param taskId The task ID to delete
     * @return Result with success or error
     */
    suspend fun deleteTask(taskId: String): Result<Unit>

    /**
     * Assign task to users
     * @param taskId The task ID
     * @param userIds List of user IDs to assign
     * @return Result with updated task or error
     */
    suspend fun assignTask(taskId: String, userIds: List<String>): Result<Task>

    /**
     * Complete a task
     * @param taskId The task ID
     * @param completedBy User ID who completed the task
     * @return Result with updated task or error
     */
    suspend fun completeTask(taskId: String, completedBy: String): Result<Task>

    /**
     * Update task status
     * @param taskId The task ID
     * @param status New status
     * @return Result with updated task or error
     */
    suspend fun updateTaskStatus(taskId: String, status: TaskStatus): Result<Task>
}
