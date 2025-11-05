package com.familyhub.feature.profile.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.familyhub.core.common.exception.FamilyHubException
import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.User
import com.familyhub.core.domain.usecase.auth.SignUpUseCase
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
import org.junit.Rule
import org.junit.Test

/**
 * SignUpViewModel Tests - TDD RED Phase
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var signUpUseCase: SignUpUseCase
    private lateinit var viewModel: SignUpViewModel

    private val mockUser = User(
        uid = "user123",
        email = "test@example.com",
        displayName = "Test User"
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        signUpUseCase = mockk()
        viewModel = SignUpViewModel(signUpUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is idle`() {
        assertThat(viewModel.uiState.value).isInstanceOf(SignUpUiState.Idle::class.java)
    }

    @Test
    fun `onSignUp with valid data shows loading then success`() = runTest {
        // Given
        coEvery {
            signUpUseCase("test@example.com", "password123", "Test User")
        } returns Result.Success(mockUser)

        viewModel.uiState.test {
            // Initial state
            assertThat(awaitItem()).isInstanceOf(SignUpUiState.Idle::class.java)

            // When
            viewModel.onSignUp("test@example.com", "password123", "password123", "Test User")
            testDispatcher.scheduler.advanceUntilIdle()

            // Then - Loading
            assertThat(awaitItem()).isInstanceOf(SignUpUiState.Loading::class.java)

            // Then - Success
            val successState = awaitItem() as SignUpUiState.Success
            assertThat(successState.user).isEqualTo(mockUser)
        }
    }

    @Test
    fun `onSignUp with mismatched passwords shows error`() = runTest {
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(SignUpUiState.Idle::class.java)

            viewModel.onSignUp("test@example.com", "password123", "differentpassword", "Test User")
            testDispatcher.scheduler.advanceUntilIdle()

            val errorState = awaitItem() as SignUpUiState.Error
            assertThat(errorState.message).contains("Passwords do not match")
        }
    }

    @Test
    fun `onSignUp with empty email shows error`() = runTest {
        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(SignUpUiState.Idle::class.java)

            viewModel.onSignUp("", "password123", "password123", "Test User")
            testDispatcher.scheduler.advanceUntilIdle()

            val errorState = awaitItem() as SignUpUiState.Error
            assertThat(errorState.message).contains("cannot be empty")
        }
    }

    @Test
    fun `onSignUp with invalid email shows error`() = runTest {
        coEvery {
            signUpUseCase("invalid-email", "password123", "Test User")
        } returns Result.Error(FamilyHubException.ValidationException("Invalid email format"))

        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(SignUpUiState.Idle::class.java)

            viewModel.onSignUp("invalid-email", "password123", "password123", "Test User")
            testDispatcher.scheduler.advanceUntilIdle()

            assertThat(awaitItem()).isInstanceOf(SignUpUiState.Loading::class.java)

            val errorState = awaitItem() as SignUpUiState.Error
            assertThat(errorState.message).contains("Invalid email")
        }
    }

    @Test
    fun `onSignUp with existing email shows error`() = runTest {
        coEvery {
            signUpUseCase("existing@example.com", "password123", "Test User")
        } returns Result.Error(FamilyHubException.EmailAlreadyExistsException())

        viewModel.uiState.test {
            assertThat(awaitItem()).isInstanceOf(SignUpUiState.Idle::class.java)

            viewModel.onSignUp("existing@example.com", "password123", "password123", "Test User")
            testDispatcher.scheduler.advanceUntilIdle()

            assertThat(awaitItem()).isInstanceOf(SignUpUiState.Loading::class.java)

            val errorState = awaitItem() as SignUpUiState.Error
            assertThat(errorState.message).contains("already registered")
        }
    }
}
