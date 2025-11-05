package com.familyhub.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.User
import com.familyhub.core.domain.usecase.auth.SignInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * SignInViewModel
 * Handles sign in UI state and user actions
 */
@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignInUiState>(SignInUiState.Idle)
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    fun onSignIn(email: String, password: String) {
        // Validate not empty
        if (email.isBlank()) {
            _uiState.value = SignInUiState.Error("Email cannot be empty")
            return
        }

        if (password.isBlank()) {
            _uiState.value = SignInUiState.Error("Password cannot be empty")
            return
        }

        viewModelScope.launch {
            _uiState.value = SignInUiState.Loading

            when (val result = signInUseCase(email, password)) {
                is Result.Success -> {
                    Timber.d("Sign in successful: ${result.data.uid}")
                    _uiState.value = SignInUiState.Success(result.data)
                }
                is Result.Error -> {
                    Timber.e(result.exception, "Sign in failed")
                    _uiState.value = SignInUiState.Error(
                        result.exception.message ?: "Sign in failed"
                    )
                }
                is Result.Loading -> {
                    // Already in loading state
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = SignInUiState.Idle
    }
}

/**
 * UI State for SignIn screen
 */
sealed class SignInUiState {
    object Idle : SignInUiState()
    object Loading : SignInUiState()
    data class Success(val user: User) : SignInUiState()
    data class Error(val message: String) : SignInUiState()
}
