package com.familyhub.core.domain.usecase.family

import com.familyhub.core.common.exception.FamilyHubException
import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.Family
import com.familyhub.core.domain.repository.FamilyRepository
import javax.inject.Inject

/**
 * JoinFamilyUseCase
 * Joins a family using invite code
 */
class JoinFamilyUseCase @Inject constructor(
    private val familyRepository: FamilyRepository
) {
    suspend operator fun invoke(inviteCode: String): Result<Family> {
        // Validate invite code
        if (inviteCode.isBlank()) {
            return Result.Error(
                FamilyHubException.ValidationException("Invite code cannot be empty")
            )
        }

        if (inviteCode.length < 6) {
            return Result.Error(
                FamilyHubException.ValidationException("Invalid invite code format")
            )
        }

        return familyRepository.joinFamily(inviteCode.uppercase())
    }
}
