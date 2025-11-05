package com.familyhub.core.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * Family DTO for Firestore
 * Maps to families/{familyId} collection
 */
data class FamilyDto(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val avatarURL: String? = null,
    val createdAt: Timestamp = Timestamp.now(),
    val createdBy: String = "",
    val members: List<FamilyMemberDto> = emptyList(),
    val settings: FamilySettingsDto = FamilySettingsDto(),
    val inviteCode: String? = null,
    val inviteCodeExpiry: Timestamp? = null
)

data class FamilyMemberDto(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoURL: String? = null,
    val role: String = "",
    val joinedAt: Timestamp = Timestamp.now(),
    val isActive: Boolean = true
)

data class FamilySettingsDto(
    val allowMemberInvites: Boolean = false,
    val requireApprovalForNewMembers: Boolean = true,
    val defaultTaskVisibility: String = "all",
    val enableAI: Boolean = true,
    val enableWeeklySummary: Boolean = true
)
