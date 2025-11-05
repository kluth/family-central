package com.familyhub.core.domain.repository

import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.Family
import com.familyhub.core.domain.model.UserRole
import kotlinx.coroutines.flow.Flow

/**
 * FamilyRepository interface
 * Defines family management operations
 * Implementation in core:data module
 */
interface FamilyRepository {

    /**
     * Create a new family
     * @return Result<Family> - Created family on success
     */
    suspend fun createFamily(name: String, description: String? = null): Result<Family>

    /**
     * Get family by ID
     * @return Flow<Result<Family>> - Family data
     */
    fun getFamilyById(familyId: String): Flow<Result<Family>>

    /**
     * Get all families for current user
     * @return Flow<Result<List<Family>>> - List of families
     */
    fun getUserFamilies(): Flow<Result<List<Family>>>

    /**
     * Join family with invite code
     * @return Result<Family> - Joined family on success
     */
    suspend fun joinFamily(inviteCode: String): Result<Family>

    /**
     * Generate invite code for family
     * @return Result<String> - Invite code on success
     */
    suspend fun generateInviteCode(familyId: String, expiryHours: Int = 168): Result<String>

    /**
     * Update family settings
     */
    suspend fun updateFamilySettings(familyId: String, settings: Map<String, Any>): Result<Family>

    /**
     * Add member to family
     */
    suspend fun addMember(familyId: String, userId: String, role: UserRole): Result<Unit>

    /**
     * Remove member from family
     */
    suspend fun removeMember(familyId: String, userId: String): Result<Unit>

    /**
     * Update member role
     */
    suspend fun updateMemberRole(familyId: String, userId: String, role: UserRole): Result<Unit>

    /**
     * Leave family
     */
    suspend fun leaveFamily(familyId: String): Result<Unit>
}
