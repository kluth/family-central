package com.familyhub.feature.tasks.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.Task
import com.familyhub.core.domain.model.TaskStatus
import com.familyhub.core.domain.usecase.task.CompleteTaskUseCase
import com.familyhub.core.domain.usecase.task.DeleteTaskUseCase
import com.familyhub.core.domain.usecase.task.GetFamilyTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * TaskListViewModel
 * Handles task list UI state and user actions
 */
@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val getFamilyTasksUseCase: GetFamilyTasksUseCase,
    private val completeTaskUseCase: CompleteTaskUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<TaskListUiState>(TaskListUiState.Loading)
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    private val _successMessage = MutableStateFlow("")
    val successMessage: StateFlow<String> = _successMessage.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    private val _filterStatus = MutableStateFlow<TaskStatus?>(null)
    private var allTasks: List<Task> = emptyList()

    fun loadTasks(familyId: String) {
        viewModelScope.launch {
            getFamilyTasksUseCase(familyId)
                .catch { exception ->
                    Timber.e(exception, "Error loading tasks")
                    _uiState.value = TaskListUiState.Error(
                        exception.message ?: "Failed to load tasks"
                    )
                }
                .collect { tasks ->
                    allTasks = tasks
                    updateUiState()
                }
        }
    }

    fun filterByStatus(status: TaskStatus?) {
        _filterStatus.value = status
        updateUiState()
    }

    fun completeTask(taskId: String, userId: String) {
        viewModelScope.launch {
            when (val result = completeTaskUseCase(taskId, userId)) {
                is Result.Success -> {
                    _successMessage.value = "Task completed successfully"
                    Timber.d("Task completed: $taskId")
                }
                is Result.Error -> {
                    _errorMessage.value = result.exception.message ?: "Failed to complete task"
                    Timber.e(result.exception, "Error completing task")
                }
                is Result.Loading -> {
                    // No action needed
                }
            }
        }
    }

    fun deleteTask(taskId: String) {
        viewModelScope.launch {
            when (val result = deleteTaskUseCase(taskId)) {
                is Result.Success -> {
                    _successMessage.value = "Task deleted successfully"
                    Timber.d("Task deleted: $taskId")
                }
                is Result.Error -> {
                    _errorMessage.value = result.exception.message ?: "Failed to delete task"
                    Timber.e(result.exception, "Error deleting task")
                }
                is Result.Loading -> {
                    // No action needed
                }
            }
        }
    }

    fun clearMessages() {
        _successMessage.value = ""
        _errorMessage.value = ""
    }

    private fun updateUiState() {
        val filteredTasks = if (_filterStatus.value != null) {
            allTasks.filter { it.status == _filterStatus.value }
        } else {
            allTasks
        }

        _uiState.value = if (filteredTasks.isEmpty()) {
            TaskListUiState.Empty
        } else {
            TaskListUiState.Success(filteredTasks)
        }
    }
}

/**
 * UI State for Task List screen
 */
sealed class TaskListUiState {
    object Loading : TaskListUiState()
    object Empty : TaskListUiState()
    data class Success(val tasks: List<Task>) : TaskListUiState()
    data class Error(val message: String) : TaskListUiState()
}
