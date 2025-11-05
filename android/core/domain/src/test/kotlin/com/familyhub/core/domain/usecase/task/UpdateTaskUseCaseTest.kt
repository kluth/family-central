package com.familyhub.core.domain.usecase.task

import com.familyhub.core.common.exception.FamilyHubException
import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.Task
import com.familyhub.core.domain.model.TaskPriority
import com.familyhub.core.domain.model.TaskStatus
import com.familyhub.core.domain.repository.TaskRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

/**
 * UpdateTaskUseCaseTest
 * TDD tests for UpdateTaskUseCase (RED phase)
 */
class UpdateTaskUseCaseTest {

    private lateinit var updateTaskUseCase: UpdateTaskUseCase
    private lateinit var taskRepository: TaskRepository

    private val mockTask = Task(
        id = "task-123",
        familyId = "family-123",
        title = "Updated Task",
        description = "Updated Description",
        status = TaskStatus.IN_PROGRESS,
        priority = TaskPriority.HIGH,
        createdBy = "user-123",
        dueDate = LocalDateTime.now().plusDays(7)
    )

    @Before
    fun setup() {
        taskRepository = mockk()
        updateTaskUseCase = UpdateTaskUseCase(taskRepository)
    }

    @Test
    fun `invoke with valid task returns Success`() = runTest {
        // Given
        coEvery { taskRepository.updateTask(any()) } returns Result.Success(mockTask)

        // When
        val result = updateTaskUseCase(mockTask)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val successResult = result as Result.Success
        assertThat(successResult.data).isEqualTo(mockTask)
        coVerify(exactly = 1) { taskRepository.updateTask(mockTask) }
    }

    @Test
    fun `invoke with empty id returns Error`() = runTest {
        // Given
        val taskWithEmptyId = mockTask.copy(id = "")

        // When
        val result = updateTaskUseCase(taskWithEmptyId)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.ValidationException::class.java)
        assertThat(errorResult.exception.message).contains("id")
        coVerify(exactly = 0) { taskRepository.updateTask(any()) }
    }

    @Test
    fun `invoke with empty title returns Error`() = runTest {
        // Given
        val taskWithEmptyTitle = mockTask.copy(title = "")

        // When
        val result = updateTaskUseCase(taskWithEmptyTitle)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.ValidationException::class.java)
        assertThat(errorResult.exception.message).contains("title")
    }

    @Test
    fun `invoke when repository fails returns Error`() = runTest {
        // Given
        val exception = FamilyHubException.DatabaseException("Update failed")
        coEvery { taskRepository.updateTask(any()) } returns Result.Error(exception)

        // When
        val result = updateTaskUseCase(mockTask)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isEqualTo(exception)
    }
}
