package com.familyhub.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.User
import com.familyhub.core.domain.usecase.auth.SignUpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * SignUpViewModel
 * Handles sign up UI state and user actions
 */
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onSignUp(
        email: String,
        password: String,
        confirmPassword: String,
        displayName: String
    ) {
        // Validate passwords match
        if (password != confirmPassword) {
            _uiState.value = SignUpUiState.Error("Passwords do not match")
            return
        }

        // Validate not empty
        if (email.isBlank() || password.isBlank() || displayName.isBlank()) {
            _uiState.value = SignUpUiState.Error("All fields are required and cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = SignUpUiState.Loading

            when (val result = signUpUseCase(email, password, displayName)) {
                is Result.Success -> {
                    Timber.d("Sign up successful: ${result.data.uid}")
                    _uiState.value = SignUpUiState.Success(result.data)
                }
                is Result.Error -> {
                    Timber.e(result.exception, "Sign up failed")
                    _uiState.value = SignUpUiState.Error(
                        result.exception.message ?: "Sign up failed"
                    )
                }
                is Result.Loading -> {
                    // Already in loading state
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = SignUpUiState.Idle
    }
}

/**
 * UI State for SignUp screen
 */
sealed class SignUpUiState {
    object Idle : SignUpUiState()
    object Loading : SignUpUiState()
    data class Success(val user: User) : SignUpUiState()
    data class Error(val message: String) : SignUpUiState()
}
