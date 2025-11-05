package com.familyhub.core.auth

import com.familyhub.core.common.result.Result
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

/**
 * Interface for authentication operations
 */
interface AuthManager {
    /**
     * Get the current authenticated user
     */
    fun getCurrentUser(): FirebaseUser?

    /**
     * Observe authentication state changes
     */
    fun observeAuthState(): Flow<FirebaseUser?>

    /**
     * Sign in with email and password
     */
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser>

    /**
     * Sign up with email and password
     */
    suspend fun signUpWithEmail(email: String, password: String): Result<FirebaseUser>

    /**
     * Sign out the current user
     */
    suspend fun signOut(): Result<Unit>

    /**
     * Check if user is authenticated
     */
    fun isAuthenticated(): Boolean
}
