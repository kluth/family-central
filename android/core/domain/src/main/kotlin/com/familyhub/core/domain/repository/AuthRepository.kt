package com.familyhub.core.domain.repository

import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * AuthRepository interface
 * Defines authentication operations
 * Implementation in core:data module
 */
interface AuthRepository {

    /**
     * Sign up with email and password
     * @return Flow<Result<User>> - User data on success
     */
    suspend fun signUpWithEmail(email: String, password: String, displayName: String): Result<User>

    /**
     * Sign in with email and password
     * @return Flow<Result<User>> - User data on success
     */
    suspend fun signInWithEmail(email: String, password: String): Result<User>

    /**
     * Sign in with Google
     * @return Flow<Result<User>> - User data on success
     */
    suspend fun signInWithGoogle(idToken: String): Result<User>

    /**
     * Sign out current user
     */
    suspend fun signOut(): Result<Unit>

    /**
     * Get currently authenticated user
     * @return Flow<User?> - Current user or null if not authenticated
     */
    fun getCurrentUser(): Flow<User?>

    /**
     * Check if user is authenticated
     * @return Flow<Boolean> - True if authenticated
     */
    fun isAuthenticated(): Flow<Boolean>

    /**
     * Send password reset email
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    /**
     * Update user profile
     */
    suspend fun updateProfile(displayName: String?, photoUrl: String?): Result<User>
}
