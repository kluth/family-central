package com.familyhub.core.domain.repository

import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.ShoppingList
import kotlinx.coroutines.flow.Flow

interface ShoppingListRepository {
    fun getFamilyShoppingLists(familyId: String): Flow<List<ShoppingList>>
    suspend fun createShoppingList(shoppingList: ShoppingList): Result<ShoppingList>
    suspend fun updateShoppingList(shoppingList: ShoppingList): Result<ShoppingList>
    suspend fun deleteShoppingList(listId: String): Result<Unit>
}
