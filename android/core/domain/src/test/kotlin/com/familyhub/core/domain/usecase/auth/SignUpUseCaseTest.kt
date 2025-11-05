package com.familyhub.core.domain.usecase.auth

import com.familyhub.core.common.exception.FamilyHubException
import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.User
import com.familyhub.core.domain.repository.AuthRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * SignUpUseCase Tests - TDD RED Phase
 * These tests will fail until we implement SignUpUseCase
 */
class SignUpUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var signUpUseCase: SignUpUseCase

    private val mockUser = User(
        uid = "user123",
        email = "test@example.com",
        displayName = "Test User",
        photoUrl = null,
        phoneNumber = null
    )

    @Before
    fun setup() {
        authRepository = mockk()
        signUpUseCase = SignUpUseCase(authRepository)
    }

    @Test
    fun `invoke with valid data returns Success`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val displayName = "Test User"

        coEvery {
            authRepository.signUpWithEmail(email, password, displayName)
        } returns Result.Success(mockUser)

        // When
        val result = signUpUseCase(email, password, displayName)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val successResult = result as Result.Success
        assertThat(successResult.data).isEqualTo(mockUser)

        coVerify(exactly = 1) {
            authRepository.signUpWithEmail(email, password, displayName)
        }
    }

    @Test
    fun `invoke with empty email returns Error`() = runTest {
        // Given
        val email = ""
        val password = "password123"
        val displayName = "Test User"

        // When
        val result = signUpUseCase(email, password, displayName)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.ValidationException::class.java)
        assertThat(errorResult.exception.message).contains("Email cannot be empty")

        coVerify(exactly = 0) {
            authRepository.signUpWithEmail(any(), any(), any())
        }
    }

    @Test
    fun `invoke with invalid email format returns Error`() = runTest {
        // Given
        val email = "invalid-email"
        val password = "password123"
        val displayName = "Test User"

        // When
        val result = signUpUseCase(email, password, displayName)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.ValidationException::class.java)
        assertThat(errorResult.exception.message).contains("Invalid email format")

        coVerify(exactly = 0) {
            authRepository.signUpWithEmail(any(), any(), any())
        }
    }

    @Test
    fun `invoke with weak password returns Error`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "123" // Too short
        val displayName = "Test User"

        // When
        val result = signUpUseCase(email, password, displayName)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.ValidationException::class.java)
        assertThat(errorResult.exception.message).contains("Password must be at least 6 characters")

        coVerify(exactly = 0) {
            authRepository.signUpWithEmail(any(), any(), any())
        }
    }

    @Test
    fun `invoke with empty display name returns Error`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val displayName = ""

        // When
        val result = signUpUseCase(email, password, displayName)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.ValidationException::class.java)
        assertThat(errorResult.exception.message).contains("Display name cannot be empty")

        coVerify(exactly = 0) {
            authRepository.signUpWithEmail(any(), any(), any())
        }
    }

    @Test
    fun `invoke when email already exists returns Error`() = runTest {
        // Given
        val email = "existing@example.com"
        val password = "password123"
        val displayName = "Test User"

        coEvery {
            authRepository.signUpWithEmail(email, password, displayName)
        } returns Result.Error(FamilyHubException.EmailAlreadyExistsException())

        // When
        val result = signUpUseCase(email, password, displayName)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.EmailAlreadyExistsException::class.java)

        coVerify(exactly = 1) {
            authRepository.signUpWithEmail(email, password, displayName)
        }
    }

    @Test
    fun `invoke with network error returns Error`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        val displayName = "Test User"

        coEvery {
            authRepository.signUpWithEmail(email, password, displayName)
        } returns Result.Error(FamilyHubException.NetworkException())

        // When
        val result = signUpUseCase(email, password, displayName)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.NetworkException::class.java)

        coVerify(exactly = 1) {
            authRepository.signUpWithEmail(email, password, displayName)
        }
    }
}
