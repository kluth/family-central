package com.familyhub.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Family domain model
 * Represents a family group in the FamilyHub platform
 */
@Parcelize
data class Family(
    val id: String,
    val name: String,
    val description: String? = null,
    val avatarUrl: String? = null,
    val createdAt: Long,
    val createdBy: String,
    val members: List<FamilyMember> = emptyList(),
    val settings: FamilySettings = FamilySettings(),
    val inviteCode: String? = null,
    val inviteCodeExpiry: Long? = null
) : Parcelable

@Parcelize
data class FamilyMember(
    val uid: String,
    val email: String,
    val displayName: String,
    val photoUrl: String? = null,
    val role: UserRole,
    val joinedAt: Long,
    val isActive: Boolean = true
) : Parcelable

@Parcelize
data class FamilySettings(
    val allowMemberInvites: Boolean = false,
    val requireApprovalForNewMembers: Boolean = true,
    val defaultTaskVisibility: TaskVisibility = TaskVisibility.ALL,
    val enableAI: Boolean = true,
    val enableWeeklySummary: Boolean = true
) : Parcelable

enum class TaskVisibility {
    ALL,
    ASSIGNED_ONLY
}
