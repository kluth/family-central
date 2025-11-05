package com.familyhub.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * User domain model
 * Represents a user in the FamilyHub platform
 */
@Parcelize
data class User(
    val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String? = null,
    val phoneNumber: String? = null,
    val familyMemberships: List<FamilyMembership> = emptyList(),
    val notificationPreferences: NotificationPreferences = NotificationPreferences(),
    val isActive: Boolean = true
) : Parcelable

@Parcelize
data class FamilyMembership(
    val familyId: String,
    val familyName: String,
    val role: UserRole,
    val joinedAt: Long
) : Parcelable

@Parcelize
data class NotificationPreferences(
    val taskAssignments: Boolean = true,
    val taskReminders: Boolean = true,
    val chatMessages: Boolean = true,
    val calendarEvents: Boolean = true,
    val weeklyDigest: Boolean = true,
    val fcmToken: String? = null
) : Parcelable

enum class UserRole {
    ADMIN,
    MEMBER
}
