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
 * SignInUseCase Tests - TDD RED Phase
 */
class SignInUseCaseTest {

    private lateinit var authRepository: AuthRepository
    private lateinit var signInUseCase: SignInUseCase

    private val mockUser = User(
        uid = "user123",
        email = "test@example.com",
        displayName = "Test User"
    )

    @Before
    fun setup() {
        authRepository = mockk()
        signInUseCase = SignInUseCase(authRepository)
    }

    @Test
    fun `invoke with valid credentials returns Success`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"

        coEvery {
            authRepository.signInWithEmail(email, password)
        } returns Result.Success(mockUser)

        // When
        val result = signInUseCase(email, password)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val successResult = result as Result.Success
        assertThat(successResult.data).isEqualTo(mockUser)

        coVerify(exactly = 1) {
            authRepository.signInWithEmail(email, password)
        }
    }

    @Test
    fun `invoke with empty email returns Error`() = runTest {
        // Given
        val email = ""
        val password = "password123"

        // When
        val result = signInUseCase(email, password)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.ValidationException::class.java)

        coVerify(exactly = 0) {
            authRepository.signInWithEmail(any(), any())
        }
    }

    @Test
    fun `invoke with empty password returns Error`() = runTest {
        // Given
        val email = "test@example.com"
        val password = ""

        // When
        val result = signInUseCase(email, password)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.ValidationException::class.java)

        coVerify(exactly = 0) {
            authRepository.signInWithEmail(any(), any())
        }
    }

    @Test
    fun `invoke with invalid credentials returns Error`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "wrongpassword"

        coEvery {
            authRepository.signInWithEmail(email, password)
        } returns Result.Error(FamilyHubException.InvalidCredentialsException())

        // When
        val result = signInUseCase(email, password)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.InvalidCredentialsException::class.java)

        coVerify(exactly = 1) {
            authRepository.signInWithEmail(email, password)
        }
    }

    @Test
    fun `invoke with network error returns Error`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"

        coEvery {
            authRepository.signInWithEmail(email, password)
        } returns Result.Error(FamilyHubException.NetworkException())

        // When
        val result = signInUseCase(email, password)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.NetworkException::class.java)
    }
}
