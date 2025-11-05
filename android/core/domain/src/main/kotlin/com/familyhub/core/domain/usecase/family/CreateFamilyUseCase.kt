package com.familyhub.core.domain.usecase.family

import com.familyhub.core.common.exception.FamilyHubException
import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.Family
import com.familyhub.core.domain.repository.FamilyRepository
import javax.inject.Inject

/**
 * CreateFamilyUseCase
 * Handles family creation with validation
 * TDD GREEN Phase - Implementation
 */
class CreateFamilyUseCase @Inject constructor(
    private val familyRepository: FamilyRepository
) {
    suspend operator fun invoke(
        name: String,
        description: String? = null
    ): Result<Family> {
        // Validate name
        if (name.isBlank()) {
            return Result.Error(
                FamilyHubException.ValidationException("Family name cannot be empty")
            )
        }

        if (name.length > 100) {
            return Result.Error(
                FamilyHubException.ValidationException("Family name must be 100 characters or less")
            )
        }

        // All validations passed, call repository
        return familyRepository.createFamily(name, description)
    }
}
