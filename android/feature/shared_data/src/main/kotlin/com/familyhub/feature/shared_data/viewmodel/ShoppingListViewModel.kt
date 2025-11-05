package com.familyhub.feature.shared_data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familyhub.core.domain.model.ShoppingItem
import com.familyhub.core.domain.model.ShoppingList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import timber.log.Timber
import javax.inject.Inject

/**
 * ShoppingListViewModel
 * Manages shopping lists for the family
 */
@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    // TODO: Inject repository when ready
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShoppingListUiState>(ShoppingListUiState.Success(emptyList()))
    val uiState: StateFlow<ShoppingListUiState> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    fun loadShoppingLists(familyId: String) {
        // Mock data for demonstration
        val mockLists = listOf(
            ShoppingList(
                id = "1",
                familyId = familyId,
                name = "Weekly Groceries",
                items = listOf(
                    ShoppingItem(id = "1", name = "Milk", quantity = "2 liters", isCompleted = false),
                    ShoppingItem(id = "2", name = "Bread", quantity = "1 loaf", isCompleted = true),
                    ShoppingItem(id = "3", name = "Eggs", quantity = "12", isCompleted = false)
                ),
                createdBy = "user-123"
            ),
            ShoppingList(
                id = "2",
                familyId = familyId,
                name = "Birthday Party",
                items = listOf(
                    ShoppingItem(id = "4", name = "Cake", quantity = "1", isCompleted = false),
                    ShoppingItem(id = "5", name = "Balloons", quantity = "20", isCompleted = false),
                    ShoppingItem(id = "6", name = "Candles", quantity = "1 pack", isCompleted = true)
                ),
                createdBy = "user-123"
            )
        )
        _uiState.value = ShoppingListUiState.Success(mockLists)
    }

    fun toggleItemComplete(listId: String, itemId: String) {
        Timber.d("Toggling item $itemId in list $listId")
        // TODO: Implement with repository
    }

    fun clearError() {
        _errorMessage.value = ""
    }
}

sealed class ShoppingListUiState {
    object Loading : ShoppingListUiState()
    data class Success(val lists: List<ShoppingList>) : ShoppingListUiState()
    data class Error(val message: String) : ShoppingListUiState()
}
