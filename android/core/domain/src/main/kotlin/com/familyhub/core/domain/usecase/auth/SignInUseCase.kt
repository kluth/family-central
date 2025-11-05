package com.familyhub.core.domain.usecase.auth

import com.familyhub.core.common.exception.FamilyHubException
import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.User
import com.familyhub.core.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * SignInUseCase
 * Handles user sign in with validation
 * TDD GREEN Phase - Implementation
 */
class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<User> {
        // Validate email
        if (email.isBlank()) {
            return Result.Error(
                FamilyHubException.ValidationException("Email cannot be empty")
            )
        }

        // Validate password
        if (password.isBlank()) {
            return Result.Error(
                FamilyHubException.ValidationException("Password cannot be empty")
            )
        }

        // All validations passed, call repository
        return authRepository.signInWithEmail(email, password)
    }
}
