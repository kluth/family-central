package com.familyhub.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class ShoppingList(
    val id: String = "",
    val familyId: String,
    val name: String,
    val items: List<ShoppingItem> = emptyList(),
    val createdBy: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) : Parcelable {
    val completedCount: Int get() = items.count { it.isCompleted }
    val totalCount: Int get() = items.size
    val isFullyCompleted: Boolean get() = items.isNotEmpty() && items.all { it.isCompleted }
}

@Parcelize
data class ShoppingItem(
    val id: String = "",
    val name: String,
    val quantity: String = "1",
    val category: String? = null,
    val isCompleted: Boolean = false,
    val completedBy: String? = null,
    val notes: String? = null
) : Parcelable
