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
 * CreateTaskUseCaseTest
 * TDD tests for CreateTaskUseCase (RED phase)
 */
class CreateTaskUseCaseTest {

    private lateinit var createTaskUseCase: CreateTaskUseCase
    private lateinit var taskRepository: TaskRepository

    private val mockTask = Task(
        id = "task-123",
        familyId = "family-123",
        title = "Test Task",
        description = "Test Description",
        status = TaskStatus.TODO,
        priority = TaskPriority.MEDIUM,
        createdBy = "user-123",
        dueDate = LocalDateTime.now().plusDays(7)
    )

    @Before
    fun setup() {
        taskRepository = mockk()
        createTaskUseCase = CreateTaskUseCase(taskRepository)
    }

    @Test
    fun `invoke with valid task returns Success`() = runTest {
        // Given
        coEvery { taskRepository.createTask(any()) } returns Result.Success(mockTask)

        // When
        val result = createTaskUseCase(mockTask)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val successResult = result as Result.Success
        assertThat(successResult.data).isEqualTo(mockTask)
        coVerify(exactly = 1) { taskRepository.createTask(mockTask) }
    }

    @Test
    fun `invoke with empty title returns Error`() = runTest {
        // Given
        val taskWithEmptyTitle = mockTask.copy(title = "")

        // When
        val result = createTaskUseCase(taskWithEmptyTitle)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.ValidationException::class.java)
        assertThat(errorResult.exception.message).contains("title")
        coVerify(exactly = 0) { taskRepository.createTask(any()) }
    }

    @Test
    fun `invoke with blank title returns Error`() = runTest {
        // Given
        val taskWithBlankTitle = mockTask.copy(title = "   ")

        // When
        val result = createTaskUseCase(taskWithBlankTitle)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.ValidationException::class.java)
    }

    @Test
    fun `invoke with empty familyId returns Error`() = runTest {
        // Given
        val taskWithEmptyFamilyId = mockTask.copy(familyId = "")

        // When
        val result = createTaskUseCase(taskWithEmptyFamilyId)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.ValidationException::class.java)
        assertThat(errorResult.exception.message).contains("familyId")
    }

    @Test
    fun `invoke with empty createdBy returns Error`() = runTest {
        // Given
        val taskWithEmptyCreatedBy = mockTask.copy(createdBy = "")

        // When
        val result = createTaskUseCase(taskWithEmptyCreatedBy)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.ValidationException::class.java)
        assertThat(errorResult.exception.message).contains("createdBy")
    }

    @Test
    fun `invoke with past due date returns Error`() = runTest {
        // Given
        val taskWithPastDueDate = mockTask.copy(dueDate = LocalDateTime.now().minusDays(1))

        // When
        val result = createTaskUseCase(taskWithPastDueDate)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.ValidationException::class.java)
        assertThat(errorResult.exception.message).contains("due date")
    }

    @Test
    fun `invoke when repository throws exception returns Error`() = runTest {
        // Given
        val exception = FamilyHubException.DatabaseException("Database error")
        coEvery { taskRepository.createTask(any()) } returns Result.Error(exception)

        // When
        val result = createTaskUseCase(mockTask)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isEqualTo(exception)
    }

    @Test
    fun `invoke with null due date is valid`() = runTest {
        // Given
        val taskWithNullDueDate = mockTask.copy(dueDate = null)
        coEvery { taskRepository.createTask(any()) } returns Result.Success(taskWithNullDueDate)

        // When
        val result = createTaskUseCase(taskWithNullDueDate)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val successResult = result as Result.Success
        assertThat(successResult.data.dueDate).isNull()
    }
}
