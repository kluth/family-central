package com.familyhub.core.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * User DTO (Data Transfer Object) for Firestore
 * Maps to users/{userId} collection
 */
data class UserDto(
    @DocumentId
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoURL: String? = null,
    val phoneNumber: String? = null,
    val familyMemberships: List<FamilyMembershipDto> = emptyList(),
    val notificationPreferences: NotificationPreferencesDto = NotificationPreferencesDto(),
    val isActive: Boolean = true,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)

data class FamilyMembershipDto(
    val familyId: String = "",
    val familyName: String = "",
    val role: String = "",
    val joinedAt: Timestamp = Timestamp.now()
)

data class NotificationPreferencesDto(
    val taskAssignments: Boolean = true,
    val taskReminders: Boolean = true,
    val chatMessages: Boolean = true,
    val calendarEvents: Boolean = true,
    val weeklyDigest: Boolean = true,
    val fcmToken: String? = null
)
