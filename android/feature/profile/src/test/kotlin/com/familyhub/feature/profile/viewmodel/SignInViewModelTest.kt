package com.familyhub.feature.profile.viewmodel

import app.cash.turbine.test
import com.familyhub.core.common.exception.FamilyHubException
import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.User
import com.familyhub.core.domain.usecase.auth.SignInUseCase
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * SignInViewModelTest
 * TDD tests for SignInViewModel
 * Tests written BEFORE implementation (RED phase)
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SignInViewModelTest {

    private lateinit var viewModel: SignInViewModel
    private lateinit var signInUseCase: SignInUseCase
    private val testDispatcher = StandardTestDispatcher()

    private val mockUser = User(
        uid = "test-uid-123",
        email = "test@example.com",
        displayName = "Test User"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        signInUseCase = mockk()
        viewModel = SignInViewModel(signInUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is idle`() = runTest {
        // Then
        assertThat(viewModel.uiState.value).isInstanceOf(SignInUiState.Idle::class.java)
    }

    @Test
    fun `onSignIn with valid credentials shows loading then success`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        coEvery { signInUseCase(email, password) } returns Result.Success(mockUser)

        // When/Then
        viewModel.uiState.test {
            // Initial idle state
            assertThat(awaitItem()).isInstanceOf(SignInUiState.Idle::class.java)

            // Trigger sign in
            viewModel.onSignIn(email, password)
            testDispatcher.scheduler.advanceUntilIdle()

            // Should show loading
            assertThat(awaitItem()).isInstanceOf(SignInUiState.Loading::class.java)

            // Should show success
            val successState = awaitItem() as SignInUiState.Success
            assertThat(successState.user).isEqualTo(mockUser)
            assertThat(successState.user.email).isEqualTo(email)
        }
    }

    @Test
    fun `onSignIn with empty email shows error`() = runTest {
        // When/Then
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(SignInUiState.Idle::class.java)

            viewModel.onSignIn("", "password123")
            testDispatcher.scheduler.advanceUntilIdle()

            val errorState = awaitItem() as SignInUiState.Error
            assertThat(errorState.message).contains("email")
        }
    }

    @Test
    fun `onSignIn with empty password shows error`() = runTest {
        // When/Then
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(SignInUiState.Idle::class.java)

            viewModel.onSignIn("test@example.com", "")
            testDispatcher.scheduler.advanceUntilIdle()

            val errorState = awaitItem() as SignInUiState.Error
            assertThat(errorState.message).contains("password")
        }
    }

    @Test
    fun `onSignIn with invalid credentials shows error`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "wrongpassword"
        coEvery { signInUseCase(email, password) } returns Result.Error(
            FamilyHubException.InvalidCredentialsException()
        )

        // When/Then
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(SignInUiState.Idle::class.java)

            viewModel.onSignIn(email, password)
            testDispatcher.scheduler.advanceUntilIdle()

            assertThat(awaitItem()).isInstanceOf(SignInUiState.Loading::class.java)

            val errorState = awaitItem() as SignInUiState.Error
            assertThat(errorState.message).isNotEmpty()
        }
    }

    @Test
    fun `onSignIn with network error shows error`() = runTest {
        // Given
        val email = "test@example.com"
        val password = "password123"
        coEvery { signInUseCase(email, password) } returns Result.Error(
            FamilyHubException.NetworkException()
        )

        // When/Then
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(SignInUiState.Idle::class.java)

            viewModel.onSignIn(email, password)
            testDispatcher.scheduler.advanceUntilIdle()

            assertThat(awaitItem()).isInstanceOf(SignInUiState.Loading::class.java)

            val errorState = awaitItem() as SignInUiState.Error
            assertThat(errorState.message).isNotEmpty()
        }
    }

    @Test
    fun `resetState clears error and returns to idle`() = runTest {
        // Given - set error state first
        coEvery { signInUseCase("", "password") } returns Result.Error(
            FamilyHubException.ValidationException("Email cannot be empty")
        )

        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(SignInUiState.Idle::class.java)

            viewModel.onSignIn("", "password")
            testDispatcher.scheduler.advanceUntilIdle()

            assertThat(awaitItem()).isInstanceOf(SignInUiState.Error::class.java)

            // When
            viewModel.resetState()

            // Then
            assertThat(awaitItem()).isInstanceOf(SignInUiState.Idle::class.java)
        }
    }
}
