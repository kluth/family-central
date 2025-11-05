package com.familyhub.core.domain.usecase.auth

import com.familyhub.core.common.exception.FamilyHubException
import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.User
import com.familyhub.core.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * SignUpUseCase
 * Handles user sign up with validation
 * TDD GREEN Phase - Implementation
 */
class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        displayName: String
    ): Result<User> {
        // Validate email
        if (email.isBlank()) {
            return Result.Error(
                FamilyHubException.ValidationException("Email cannot be empty")
            )
        }

        if (!isValidEmail(email)) {
            return Result.Error(
                FamilyHubException.ValidationException("Invalid email format")
            )
        }

        // Validate password
        if (password.length < 6) {
            return Result.Error(
                FamilyHubException.ValidationException("Password must be at least 6 characters")
            )
        }

        // Validate display name
        if (displayName.isBlank()) {
            return Result.Error(
                FamilyHubException.ValidationException("Display name cannot be empty")
            )
        }

        // All validations passed, call repository
        return authRepository.signUpWithEmail(email, password, displayName)
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }
}
