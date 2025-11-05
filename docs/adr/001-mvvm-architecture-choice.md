# ADR 001: MVVM Architecture Over MVI

## Status
**Accepted**

## Context
We need to choose an architectural pattern for the Android application. The two primary candidates are:
- **MVVM (Model-View-ViewModel)** with Unidirectional Data Flow
- **MVI (Model-View-Intent)**

## Decision
We will use **MVVM with Unidirectional Data Flow (UDF)**.

## Rationale

### Advantages of MVVM
1. **Native Android Support**: First-class support via Architecture Components (ViewModel, LiveData/StateFlow)
2. **Simpler Learning Curve**: Easier for new team members to understand
3. **Less Boilerplate**: Fewer classes and interfaces compared to MVI for standard CRUD operations
4. **Clear Separation of Concerns**: Well-defined boundaries between View, ViewModel, and Domain layers
5. **Better Testability**: ViewModels are easy to test in isolation with MockK and Turbine

### Why Not MVI?
While MVI offers excellent state predictability, it comes with:
- More boilerplate code (Intent, Action, State, Reducer)
- Steeper learning curve
- Overkill for simple CRUD operations
- More complex state management for basic flows

### Our Implementation
We implement **MVVM + UDF** combining the best of both:
- **ViewModel** as single source of truth (MVVM)
- **Immutable state** (MVI concept)
- **Unidirectional data flow** (MVI concept)
- **Event-based user actions** (MVI concept)

```kotlin
// Example ViewModel with UDF
class TaskListViewModel(
    private val getTasks: GetTasksUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<TaskListUiState>(TaskListUiState.Loading)
    val uiState: StateFlow<TaskListUiState> = _uiState.asStateFlow()

    // User events
    fun onRefresh() { loadTasks() }
    fun onFilterChanged(filter: TaskFilter) { applyFilter(filter) }

    // State updates
    private fun loadTasks() {
        viewModelScope.launch {
            getTasks().collect { result ->
                _uiState.value = when (result) {
                    is Result.Success -> TaskListUiState.Success(result.data)
                    is Result.Error -> TaskListUiState.Error(result.message)
                }
            }
        }
    }
}
```

## Consequences

### Positive
- Faster development velocity
- Easier onboarding for new developers
- Better tooling support (Android Studio)
- Cleaner codebase for standard flows

### Negative
- Slightly less predictable than pure MVI for complex state
- Requires discipline to maintain UDF patterns

### Mitigation
- Enforce UDF patterns in code reviews
- Use sealed classes for UI state
- Write comprehensive unit tests
- Document patterns in wiki

## Future Considerations
If state management becomes too complex (e.g., collaborative editing), we may introduce MVI for specific features while keeping MVVM for others.

## Date
2024-01-15

## Authors
Principal Architect Team
