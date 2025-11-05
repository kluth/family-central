package com.familyhub.core.data.mapper

import com.familyhub.core.data.model.FamilyDto
import com.familyhub.core.data.model.FamilyMemberDto
import com.familyhub.core.data.model.FamilySettingsDto
import com.familyhub.core.domain.model.Family
import com.familyhub.core.domain.model.FamilyMember
import com.familyhub.core.domain.model.FamilySettings
import com.familyhub.core.domain.model.TaskVisibility
import com.familyhub.core.domain.model.UserRole

/**
 * Mapper for Family domain model <-> FamilyDto
 */
object FamilyMapper {

    fun toDomain(dto: FamilyDto): Family {
        return Family(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            avatarUrl = dto.avatarURL,
            createdAt = dto.createdAt.toDate().time,
            createdBy = dto.createdBy,
            members = dto.members.map { it.toDomain() },
            settings = dto.settings.toDomain(),
            inviteCode = dto.inviteCode,
            inviteCodeExpiry = dto.inviteCodeExpiry?.toDate()?.time
        )
    }

    fun toDto(domain: Family): FamilyDto {
        return FamilyDto(
            id = domain.id,
            name = domain.name,
            description = domain.description,
            avatarURL = domain.avatarUrl,
            createdAt = com.google.firebase.Timestamp(java.util.Date(domain.createdAt)),
            createdBy = domain.createdBy,
            members = domain.members.map { it.toDto() },
            settings = domain.settings.toDto(),
            inviteCode = domain.inviteCode,
            inviteCodeExpiry = domain.inviteCodeExpiry?.let { com.google.firebase.Timestamp(java.util.Date(it)) }
        )
    }

    private fun FamilyMemberDto.toDomain(): FamilyMember {
        return FamilyMember(
            uid = uid,
            email = email,
            displayName = displayName,
            photoUrl = photoURL,
            role = UserRole.valueOf(role.uppercase()),
            joinedAt = joinedAt.toDate().time,
            isActive = isActive
        )
    }

    private fun FamilyMember.toDto(): FamilyMemberDto {
        return FamilyMemberDto(
            uid = uid,
            email = email,
            displayName = displayName,
            photoURL = photoUrl,
            role = role.name.lowercase(),
            joinedAt = com.google.firebase.Timestamp(java.util.Date(joinedAt)),
            isActive = isActive
        )
    }

    private fun FamilySettingsDto.toDomain(): FamilySettings {
        return FamilySettings(
            allowMemberInvites = allowMemberInvites,
            requireApprovalForNewMembers = requireApprovalForNewMembers,
            defaultTaskVisibility = TaskVisibility.valueOf(defaultTaskVisibility.uppercase().replace("-", "_")),
            enableAI = enableAI,
            enableWeeklySummary = enableWeeklySummary
        )
    }

    private fun FamilySettings.toDto(): FamilySettingsDto {
        return FamilySettingsDto(
            allowMemberInvites = allowMemberInvites,
            requireApprovalForNewMembers = requireApprovalForNewMembers,
            defaultTaskVisibility = defaultTaskVisibility.name.lowercase().replace("_", "-"),
            enableAI = enableAI,
            enableWeeklySummary = enableWeeklySummary
        )
    }
}
