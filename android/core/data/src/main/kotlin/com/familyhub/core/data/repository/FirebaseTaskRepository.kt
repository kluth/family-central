package com.familyhub.core.data.src.main.kotlin.com.familyhub.core.data.repository

import com.familyhub.core.common.exception.FamilyHubException
import com.familyhub.core.common.result.Result
import com.familyhub.core.data.mapper.TaskMapper
import com.familyhub.core.data.model.TaskDto
import com.familyhub.core.domain.model.Task
import com.familyhub.core.domain.model.TaskStatus
import com.familyhub.core.domain.repository.TaskRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * FirebaseTaskRepository
 * Firebase Firestore implementation of TaskRepository
 */
@Singleton
class FirebaseTaskRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : TaskRepository {

    companion object {
        private const val TASKS_COLLECTION = "tasks"
    }

    override fun getFamilyTasks(familyId: String): Flow<List<Task>> = callbackFlow {
        val listener = firestore.collection(TASKS_COLLECTION)
            .whereEqualTo("familyId", familyId)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error listening to family tasks")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val tasks = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val dto = doc.toObject(TaskDto::class.java)
                        dto?.let { TaskMapper.toDomain(it) }
                    } catch (e: Exception) {
                        Timber.e(e, "Error parsing task document")
                        null
                    }
                } ?: emptyList()

                trySend(tasks)
            }

        awaitClose { listener.remove() }
    }

    override fun getUserTasks(userId: String, familyId: String): Flow<List<Task>> = callbackFlow {
        val listener = firestore.collection(TASKS_COLLECTION)
            .whereEqualTo("familyId", familyId)
            .whereArrayContains("assignedTo", mapOf("userId" to userId))
            .orderBy("dueDate", com.google.firebase.firestore.Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error listening to user tasks")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val tasks = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val dto = doc.toObject(TaskDto::class.java)
                        dto?.let { TaskMapper.toDomain(it) }
                    } catch (e: Exception) {
                        Timber.e(e, "Error parsing task document")
                        null
                    }
                } ?: emptyList()

                trySend(tasks)
            }

        awaitClose { listener.remove() }
    }

    override fun getTasksByStatus(familyId: String, status: TaskStatus): Flow<List<Task>> = callbackFlow {
        val listener = firestore.collection(TASKS_COLLECTION)
            .whereEqualTo("familyId", familyId)
            .whereEqualTo("status", status.name)
            .orderBy("priority", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error listening to tasks by status")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val tasks = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val dto = doc.toObject(TaskDto::class.java)
                        dto?.let { TaskMapper.toDomain(it) }
                    } catch (e: Exception) {
                        Timber.e(e, "Error parsing task document")
                        null
                    }
                } ?: emptyList()

                trySend(tasks)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun getTask(taskId: String): Result<Task> {
        return try {
            val document = firestore.collection(TASKS_COLLECTION)
                .document(taskId)
                .get()
                .await()

            if (!document.exists()) {
                return Result.Error(FamilyHubException.NotFoundException("Task not found"))
            }

            val dto = document.toObject(TaskDto::class.java)
                ?: return Result.Error(FamilyHubException.DatabaseException("Failed to parse task"))

            Result.Success(TaskMapper.toDomain(dto))
        } catch (e: Exception) {
            Timber.e(e, "Error getting task")
            Result.Error(mapFirebaseException(e))
        }
    }

    override suspend fun createTask(task: Task): Result<Task> {
        return try {
            val docRef = firestore.collection(TASKS_COLLECTION).document()
            val taskWithId = task.copy(id = docRef.id)
            val dto = TaskMapper.toDto(taskWithId)

            docRef.set(dto).await()
            Timber.d("Task created successfully: ${docRef.id}")

            Result.Success(taskWithId)
        } catch (e: Exception) {
            Timber.e(e, "Error creating task")
            Result.Error(mapFirebaseException(e))
        }
    }

    override suspend fun updateTask(task: Task): Result<Task> {
        return try {
            val dto = TaskMapper.toDto(task.copy(updatedAt = java.time.LocalDateTime.now()))

            firestore.collection(TASKS_COLLECTION)
                .document(task.id)
                .set(dto)
                .await()

            Timber.d("Task updated successfully: ${task.id}")
            Result.Success(task)
        } catch (e: Exception) {
            Timber.e(e, "Error updating task")
            Result.Error(mapFirebaseException(e))
        }
    }

    override suspend fun deleteTask(taskId: String): Result<Unit> {
        return try {
            firestore.collection(TASKS_COLLECTION)
                .document(taskId)
                .delete()
                .await()

            Timber.d("Task deleted successfully: $taskId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting task")
            Result.Error(mapFirebaseException(e))
        }
    }

    override suspend fun assignTask(taskId: String, userIds: List<String>): Result<Task> {
        return try {
            val taskResult = getTask(taskId)
            if (taskResult is Result.Error) {
                return taskResult
            }

            val task = (taskResult as Result.Success).data
            // Note: This is a simplified version. In a real implementation,
            // you'd fetch user details to create proper Assignee objects
            val updatedTask = task.copy(updatedAt = java.time.LocalDateTime.now())

            updateTask(updatedTask)
        } catch (e: Exception) {
            Timber.e(e, "Error assigning task")
            Result.Error(mapFirebaseException(e))
        }
    }

    override suspend fun completeTask(taskId: String, completedBy: String): Result<Task> {
        return try {
            val taskResult = getTask(taskId)
            if (taskResult is Result.Error) {
                return taskResult
            }

            val task = (taskResult as Result.Success).data
            val now = java.time.LocalDateTime.now()
            val completedTask = task.copy(
                status = TaskStatus.COMPLETED,
                completedAt = now,
                updatedAt = now
            )

            updateTask(completedTask)
        } catch (e: Exception) {
            Timber.e(e, "Error completing task")
            Result.Error(mapFirebaseException(e))
        }
    }

    override suspend fun updateTaskStatus(taskId: String, status: TaskStatus): Result<Task> {
        return try {
            val taskResult = getTask(taskId)
            if (taskResult is Result.Error) {
                return taskResult
            }

            val task = (taskResult as Result.Success).data
            val updatedTask = task.copy(
                status = status,
                updatedAt = java.time.LocalDateTime.now()
            )

            updateTask(updatedTask)
        } catch (e: Exception) {
            Timber.e(e, "Error updating task status")
            Result.Error(mapFirebaseException(e))
        }
    }

    private fun mapFirebaseException(exception: Exception): FamilyHubException {
        return when (exception) {
            is com.google.firebase.FirebaseNetworkException ->
                FamilyHubException.NetworkException(exception.message)
            is com.google.firebase.firestore.FirebaseFirestoreException ->
                FamilyHubException.DatabaseException(exception.message)
            else ->
                FamilyHubException.UnknownException(exception.message, exception)
        }
    }
}
