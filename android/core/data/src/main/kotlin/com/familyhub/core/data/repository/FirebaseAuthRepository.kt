package com.familyhub.core.data.repository

import com.familyhub.core.common.exception.FamilyHubException
import com.familyhub.core.common.result.Result
import com.familyhub.core.data.mapper.UserMapper
import com.familyhub.core.data.model.FamilyMembershipDto
import com.familyhub.core.data.model.NotificationPreferencesDto
import com.familyhub.core.data.model.UserDto
import com.familyhub.core.domain.model.User
import com.familyhub.core.domain.repository.AuthRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase implementation of AuthRepository
 * Handles authentication via Firebase Auth and user data via Firestore
 */
@Singleton
class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String
    ): Result<User> {
        return try {
            // Create Firebase Auth user
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return Result.Error(FamilyHubException.AuthenticationException("User creation failed"))

            // Update profile with display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()

            // Create Firestore user document
            val userDto = UserDto(
                uid = firebaseUser.uid,
                email = email,
                displayName = displayName,
                photoURL = firebaseUser.photoUrl?.toString(),
                familyMemberships = emptyList(),
                notificationPreferences = NotificationPreferencesDto(),
                isActive = true,
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )

            firestore.collection("users")
                .document(firebaseUser.uid)
                .set(userDto)
                .await()

            Timber.d("User created successfully: ${firebaseUser.uid}")
            Result.Success(UserMapper.toDomain(userDto))

        } catch (e: Exception) {
            Timber.e(e, "Sign up failed")
            Result.Error(mapFirebaseException(e))
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
                ?: return Result.Error(FamilyHubException.AuthenticationException("Sign in failed"))

            // Fetch user document
            val userDoc = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            if (!userDoc.exists()) {
                return Result.Error(FamilyHubException.UserNotFoundException())
            }

            val userDto = userDoc.toObject(UserDto::class.java)
                ?: return Result.Error(FamilyHubException.DataCorruptedException("User data is invalid"))

            Timber.d("User signed in successfully: ${firebaseUser.uid}")
            Result.Success(UserMapper.toDomain(userDto))

        } catch (e: Exception) {
            Timber.e(e, "Sign in failed")
            Result.Error(mapFirebaseException(e))
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user
                ?: return Result.Error(FamilyHubException.AuthenticationException("Google sign in failed"))

            // Check if user document exists
            val userDocRef = firestore.collection("users").document(firebaseUser.uid)
            val userDoc = userDocRef.get().await()

            val userDto = if (userDoc.exists()) {
                // Existing user
                userDoc.toObject(UserDto::class.java)!!
            } else {
                // New user - create document
                val newUser = UserDto(
                    uid = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName ?: "",
                    photoURL = firebaseUser.photoUrl?.toString(),
                    familyMemberships = emptyList(),
                    notificationPreferences = NotificationPreferencesDto(),
                    isActive = true,
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now()
                )
                userDocRef.set(newUser).await()
                newUser
            }

            Timber.d("Google sign in successful: ${firebaseUser.uid}")
            Result.Success(UserMapper.toDomain(userDto))

        } catch (e: Exception) {
            Timber.e(e, "Google sign in failed")
            Result.Error(mapFirebaseException(e))
        }
    }

    override suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Timber.d("User signed out successfully")
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Sign out failed")
            Result.Error(FamilyHubException.UnknownException("Sign out failed", e))
        }
    }

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val authListener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                // Fetch user document
                firestore.collection("users")
                    .document(firebaseUser.uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        if (doc.exists()) {
                            val userDto = doc.toObject(UserDto::class.java)
                            trySend(userDto?.let { UserMapper.toDomain(it) })
                        } else {
                            trySend(null)
                        }
                    }
                    .addOnFailureListener {
                        trySend(null)
                    }
            } else {
                trySend(null)
            }
        }

        auth.addAuthStateListener(authListener)

        awaitClose {
            auth.removeAuthStateListener(authListener)
        }
    }

    override fun isAuthenticated(): Flow<Boolean> = callbackFlow {
        val authListener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser != null)
        }

        auth.addAuthStateListener(authListener)

        awaitClose {
            auth.removeAuthStateListener(authListener)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Timber.d("Password reset email sent to: $email")
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Password reset failed")
            Result.Error(mapFirebaseException(e))
        }
    }

    override suspend fun updateProfile(displayName: String?, photoUrl: String?): Result<User> {
        return try {
            val firebaseUser = auth.currentUser
                ?: return Result.Error(FamilyHubException.UnauthorizedException("No user signed in"))

            // Update Firebase Auth profile
            val profileUpdates = UserProfileChangeRequest.Builder()
                .apply {
                    displayName?.let { setDisplayName(it) }
                    photoUrl?.let { setPhotoUri(android.net.Uri.parse(it)) }
                }
                .build()

            firebaseUser.updateProfile(profileUpdates).await()

            // Update Firestore document
            val updates = mutableMapOf<String, Any>()
            displayName?.let { updates["displayName"] = it }
            photoUrl?.let { updates["photoURL"] = it }
            updates["updatedAt"] = Timestamp.now()

            firestore.collection("users")
                .document(firebaseUser.uid)
                .update(updates)
                .await()

            // Fetch updated user
            val userDoc = firestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            val userDto = userDoc.toObject(UserDto::class.java)!!
            Timber.d("Profile updated successfully")
            Result.Success(UserMapper.toDomain(userDto))

        } catch (e: Exception) {
            Timber.e(e, "Profile update failed")
            Result.Error(mapFirebaseException(e))
        }
    }

    private fun mapFirebaseException(exception: Exception): FamilyHubException {
        return when {
            exception.message?.contains("email address is already") == true ->
                FamilyHubException.EmailAlreadyExistsException()
            exception.message?.contains("password is invalid") == true ->
                FamilyHubException.InvalidCredentialsException()
            exception.message?.contains("no user record") == true ->
                FamilyHubException.UserNotFoundException()
            exception.message?.contains("network") == true ->
                FamilyHubException.NetworkException(cause = exception)
            else ->
                FamilyHubException.UnknownException("Authentication failed", exception)
        }
    }
}
