package com.familyhub.core.domain.usecase.auth

import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * SignOutUseCase
 * Signs out the current user
 */
class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return authRepository.signOut()
    }
}
