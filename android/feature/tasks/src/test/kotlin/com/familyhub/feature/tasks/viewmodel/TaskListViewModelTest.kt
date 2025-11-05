package com.familyhub.feature.tasks.viewmodel

import app.cash.turbine.test
import com.familyhub.core.common.exception.FamilyHubException
import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.Task
import com.familyhub.core.domain.model.TaskPriority
import com.familyhub.core.domain.model.TaskStatus
import com.familyhub.core.domain.usecase.task.CompleteTaskUseCase
import com.familyhub.core.domain.usecase.task.DeleteTaskUseCase
import com.familyhub.core.domain.usecase.task.GetFamilyTasksUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime

/**
 * TaskListViewModelTest
 * TDD tests for TaskListViewModel (RED phase)
 */
@OptIn(ExperimentalCoroutinesApi::class)
class TaskListViewModelTest {

    private lateinit var viewModel: TaskListViewModel
    private lateinit var getFamilyTasksUseCase: GetFamilyTasksUseCase
    private lateinit var completeTaskUseCase: CompleteTaskUseCase
    private lateinit var deleteTaskUseCase: DeleteTaskUseCase
    private val testDispatcher = StandardTestDispatcher()

    private val mockTasks = listOf(
        Task(
            id = "task-1",
            familyId = "family-123",
            title = "Buy groceries",
            description = "Milk, bread, eggs",
            status = TaskStatus.TODO,
            priority = TaskPriority.HIGH,
            createdBy = "user-123",
            dueDate = LocalDateTime.now().plusDays(1)
        ),
        Task(
            id = "task-2",
            familyId = "family-123",
            title = "Clean kitchen",
            description = "Deep clean",
            status = TaskStatus.IN_PROGRESS,
            priority = TaskPriority.MEDIUM,
            createdBy = "user-123"
        )
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getFamilyTasksUseCase = mockk()
        completeTaskUseCase = mockk()
        deleteTaskUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is loading`() = runTest {
        // Given
        every { getFamilyTasksUseCase(any()) } returns flowOf(emptyList())
        viewModel = TaskListViewModel(getFamilyTasksUseCase, completeTaskUseCase, deleteTaskUseCase)

        // Then
        assertThat(viewModel.uiState.value).isInstanceOf(TaskListUiState.Loading::class.java)
    }

    @Test
    fun `loadTasks with valid familyId emits success state`() = runTest {
        // Given
        every { getFamilyTasksUseCase("family-123") } returns flowOf(mockTasks)
        viewModel = TaskListViewModel(getFamilyTasksUseCase, completeTaskUseCase, deleteTaskUseCase)

        // When/Then
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(TaskListUiState.Loading::class.java)

            viewModel.loadTasks("family-123")
            testDispatcher.scheduler.advanceUntilIdle()

            val successState = awaitItem() as TaskListUiState.Success
            assertThat(successState.tasks).hasSize(2)
            assertThat(successState.tasks[0].title).isEqualTo("Buy groceries")
            assertThat(successState.tasks[1].title).isEqualTo("Clean kitchen")
        }
    }

    @Test
    fun `loadTasks with empty list emits empty state`() = runTest {
        // Given
        every { getFamilyTasksUseCase("family-123") } returns flowOf(emptyList())
        viewModel = TaskListViewModel(getFamilyTasksUseCase, completeTaskUseCase, deleteTaskUseCase)

        // When/Then
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(TaskListUiState.Loading::class.java)

            viewModel.loadTasks("family-123")
            testDispatcher.scheduler.advanceUntilIdle()

            assertThat(awaitItem()).isInstanceOf(TaskListUiState.Empty::class.java)
        }
    }

    @Test
    fun `completeTask with valid ID shows success message`() = runTest {
        // Given
        every { getFamilyTasksUseCase(any()) } returns flowOf(mockTasks)
        val completedTask = mockTasks[0].copy(status = TaskStatus.COMPLETED)
        coEvery { completeTaskUseCase("task-1", "user-123") } returns Result.Success(completedTask)

        viewModel = TaskListViewModel(getFamilyTasksUseCase, completeTaskUseCase, deleteTaskUseCase)
        viewModel.loadTasks("family-123")
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.completeTask("task-1", "user-123")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - verify success message is set
        assertThat(viewModel.successMessage.value).isNotEmpty()
        assertThat(viewModel.successMessage.value).contains("completed")
    }

    @Test
    fun `completeTask with error shows error message`() = runTest {
        // Given
        every { getFamilyTasksUseCase(any()) } returns flowOf(mockTasks)
        coEvery { completeTaskUseCase("task-1", "user-123") } returns Result.Error(
            FamilyHubException.DatabaseException("Failed to complete task")
        )

        viewModel = TaskListViewModel(getFamilyTasksUseCase, completeTaskUseCase, deleteTaskUseCase)
        viewModel.loadTasks("family-123")
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.completeTask("task-1", "user-123")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.errorMessage.value).isNotEmpty()
    }

    @Test
    fun `deleteTask with valid ID removes task and shows success`() = runTest {
        // Given
        every { getFamilyTasksUseCase(any()) } returns flowOf(mockTasks)
        coEvery { deleteTaskUseCase("task-1") } returns Result.Success(Unit)

        viewModel = TaskListViewModel(getFamilyTasksUseCase, completeTaskUseCase, deleteTaskUseCase)
        viewModel.loadTasks("family-123")
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.deleteTask("task-1")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        assertThat(viewModel.successMessage.value).isNotEmpty()
        assertThat(viewModel.successMessage.value).contains("deleted")
    }

    @Test
    fun `filterTasks by status filters the task list`() = runTest {
        // Given
        every { getFamilyTasksUseCase(any()) } returns flowOf(mockTasks)
        viewModel = TaskListViewModel(getFamilyTasksUseCase, completeTaskUseCase, deleteTaskUseCase)
        viewModel.loadTasks("family-123")
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.filterByStatus(TaskStatus.TODO)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value as TaskListUiState.Success
        assertThat(state.tasks).hasSize(1)
        assertThat(state.tasks[0].status).isEqualTo(TaskStatus.TODO)
    }

    @Test
    fun `clearMessages resets error and success messages`() = runTest {
        // Given
        every { getFamilyTasksUseCase(any()) } returns flowOf(mockTasks)
        coEvery { deleteTaskUseCase("task-1") } returns Result.Success(Unit)

        viewModel = TaskListViewModel(getFamilyTasksUseCase, completeTaskUseCase, deleteTaskUseCase)
        viewModel.loadTasks("family-123")
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.deleteTask("task-1")
        testDispatcher.scheduler.advanceUntilIdle()

        // When
        viewModel.clearMessages()

        // Then
        assertThat(viewModel.successMessage.value).isEmpty()
        assertThat(viewModel.errorMessage.value).isEmpty()
    }
}
