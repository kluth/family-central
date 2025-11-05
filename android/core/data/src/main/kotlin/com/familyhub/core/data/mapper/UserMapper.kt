package com.familyhub.core.data.mapper

import com.familyhub.core.data.model.FamilyMembershipDto
import com.familyhub.core.data.model.NotificationPreferencesDto
import com.familyhub.core.data.model.UserDto
import com.familyhub.core.domain.model.FamilyMembership
import com.familyhub.core.domain.model.NotificationPreferences
import com.familyhub.core.domain.model.User
import com.familyhub.core.domain.model.UserRole

/**
 * Mapper for User domain model <-> UserDto
 */
object UserMapper {

    fun toDomain(dto: UserDto): User {
        return User(
            uid = dto.uid,
            email = dto.email,
            displayName = dto.displayName,
            photoUrl = dto.photoURL,
            phoneNumber = dto.phoneNumber,
            familyMemberships = dto.familyMemberships.map { it.toDomain() },
            notificationPreferences = dto.notificationPreferences.toDomain(),
            isActive = dto.isActive
        )
    }

    fun toDto(domain: User): UserDto {
        return UserDto(
            uid = domain.uid,
            email = domain.email,
            displayName = domain.displayName,
            photoURL = domain.photoUrl,
            phoneNumber = domain.phoneNumber,
            familyMemberships = domain.familyMemberships.map { it.toDto() },
            notificationPreferences = domain.notificationPreferences.toDto(),
            isActive = domain.isActive
        )
    }

    private fun FamilyMembershipDto.toDomain(): FamilyMembership {
        return FamilyMembership(
            familyId = familyId,
            familyName = familyName,
            role = UserRole.valueOf(role.uppercase()),
            joinedAt = joinedAt.toDate().time
        )
    }

    private fun FamilyMembership.toDto(): FamilyMembershipDto {
        return FamilyMembershipDto(
            familyId = familyId,
            familyName = familyName,
            role = role.name.lowercase(),
            joinedAt = com.google.firebase.Timestamp(java.util.Date(joinedAt))
        )
    }

    private fun NotificationPreferencesDto.toDomain(): NotificationPreferences {
        return NotificationPreferences(
            taskAssignments = taskAssignments,
            taskReminders = taskReminders,
            chatMessages = chatMessages,
            calendarEvents = calendarEvents,
            weeklyDigest = weeklyDigest,
            fcmToken = fcmToken
        )
    }

    private fun NotificationPreferences.toDto(): NotificationPreferencesDto {
        return NotificationPreferencesDto(
            taskAssignments = taskAssignments,
            taskReminders = taskReminders,
            chatMessages = chatMessages,
            calendarEvents = calendarEvents,
            weeklyDigest = weeklyDigest,
            fcmToken = fcmToken
        )
    }
}
