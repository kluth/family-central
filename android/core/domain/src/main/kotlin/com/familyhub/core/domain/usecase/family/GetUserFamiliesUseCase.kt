package com.familyhub.core.domain.usecase.family

import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.Family
import com.familyhub.core.domain.repository.FamilyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * GetUserFamiliesUseCase
 * Retrieves all families for the current user
 */
class GetUserFamiliesUseCase @Inject constructor(
    private val familyRepository: FamilyRepository
) {
    operator fun invoke(): Flow<Result<List<Family>>> {
        return familyRepository.getUserFamilies()
    }
}
