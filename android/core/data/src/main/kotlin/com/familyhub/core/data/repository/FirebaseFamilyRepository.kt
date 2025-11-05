package com.familyhub.core.data.repository

import com.familyhub.core.common.exception.FamilyHubException
import com.familyhub.core.common.result.Result
import com.familyhub.core.data.mapper.FamilyMapper
import com.familyhub.core.data.model.FamilyDto
import com.familyhub.core.data.model.FamilyMemberDto
import com.familyhub.core.data.model.FamilySettingsDto
import com.familyhub.core.domain.model.Family
import com.familyhub.core.domain.model.UserRole
import com.familyhub.core.domain.repository.FamilyRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase implementation of FamilyRepository
 */
@Singleton
class FirebaseFamilyRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : FamilyRepository {

    override suspend fun createFamily(name: String, description: String?): Result<Family> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.Error(FamilyHubException.UnauthorizedException("No user signed in"))

            val familyId = firestore.collection("families").document().id

            val familyDto = FamilyDto(
                id = familyId,
                name = name,
                description = description,
                createdAt = Timestamp.now(),
                createdBy = currentUser.uid,
                members = listOf(
                    FamilyMemberDto(
                        uid = currentUser.uid,
                        email = currentUser.email ?: "",
                        displayName = currentUser.displayName ?: "",
                        photoURL = currentUser.photoUrl?.toString(),
                        role = "admin",
                        joinedAt = Timestamp.now(),
                        isActive = true
                    )
                ),
                settings = FamilySettingsDto(),
                inviteCode = null,
                inviteCodeExpiry = null
            )

            firestore.collection("families")
                .document(familyId)
                .set(familyDto)
                .await()

            // Update user's family memberships
            firestore.collection("users")
                .document(currentUser.uid)
                .update(
                    "familyMemberships", com.google.firebase.firestore.FieldValue.arrayUnion(
                        mapOf(
                            "familyId" to familyId,
                            "familyName" to name,
                            "role" to "admin",
                            "joinedAt" to Timestamp.now()
                        )
                    )
                )
                .await()

            Timber.d("Family created successfully: $familyId")
            Result.Success(FamilyMapper.toDomain(familyDto))

        } catch (e: Exception) {
            Timber.e(e, "Family creation failed")
            Result.Error(FamilyHubException.UnknownException("Failed to create family", e))
        }
    }

    override fun getFamilyById(familyId: String): Flow<Result<Family>> = callbackFlow {
        val listener = firestore.collection("families")
            .document(familyId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.Error(FamilyHubException.UnknownException("Error fetching family", error)))
                    return@addSnapshotListener
                }

                if (snapshot == null || !snapshot.exists()) {
                    trySend(Result.Error(FamilyHubException.FamilyNotFoundException()))
                    return@addSnapshotListener
                }

                val familyDto = snapshot.toObject(FamilyDto::class.java)
                if (familyDto != null) {
                    trySend(Result.Success(FamilyMapper.toDomain(familyDto)))
                } else {
                    trySend(Result.Error(FamilyHubException.DataCorruptedException()))
                }
            }

        awaitClose { listener.remove() }
    }

    override fun getUserFamilies(): Flow<Result<List<Family>>> = callbackFlow {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            trySend(Result.Error(FamilyHubException.UnauthorizedException()))
            close()
            return@callbackFlow
        }

        // Listen to user document to get family memberships
        val userListener = firestore.collection("users")
            .document(currentUser.uid)
            .addSnapshotListener { userSnapshot, userError ->
                if (userError != null) {
                    trySend(Result.Error(FamilyHubException.UnknownException("Error fetching user", userError)))
                    return@addSnapshotListener
                }

                if (userSnapshot == null || !userSnapshot.exists()) {
                    trySend(Result.Success(emptyList()))
                    return@addSnapshotListener
                }

                val familyMemberships = userSnapshot.get("familyMemberships") as? List<Map<String, Any>>
                if (familyMemberships.isNullOrEmpty()) {
                    trySend(Result.Success(emptyList()))
                    return@addSnapshotListener
                }

                val familyIds = familyMemberships.mapNotNull { it["familyId"] as? String }

                // Fetch all families
                firestore.collection("families")
                    .whereIn(com.google.firebase.firestore.FieldPath.documentId(), familyIds)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        val families = querySnapshot.documents.mapNotNull { doc ->
                            doc.toObject(FamilyDto::class.java)?.let { FamilyMapper.toDomain(it) }
                        }
                        trySend(Result.Success(families))
                    }
                    .addOnFailureListener { e ->
                        trySend(Result.Error(FamilyHubException.UnknownException("Error fetching families", e)))
                    }
            }

        awaitClose { userListener.remove() }
    }

    override suspend fun joinFamily(inviteCode: String): Result<Family> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.Error(FamilyHubException.UnauthorizedException())

            // Find family with invite code
            val familiesSnapshot = firestore.collection("families")
                .whereEqualTo("inviteCode", inviteCode)
                .get()
                .await()

            if (familiesSnapshot.isEmpty) {
                return Result.Error(FamilyHubException.InvalidInviteCodeException())
            }

            val familyDoc = familiesSnapshot.documents.first()
            val familyDto = familyDoc.toObject(FamilyDto::class.java)!!

            // Check if invite code is expired
            if (familyDto.inviteCodeExpiry != null && familyDto.inviteCodeExpiry!! < Timestamp.now()) {
                return Result.Error(FamilyHubException.InvalidInviteCodeException("Invite code has expired"))
            }

            // Check if user is already a member
            if (familyDto.members.any { it.uid == currentUser.uid }) {
                return Result.Error(FamilyHubException.AlreadyFamilyMemberException())
            }

            // Add user to family members
            val newMember = FamilyMemberDto(
                uid = currentUser.uid,
                email = currentUser.email ?: "",
                displayName = currentUser.displayName ?: "",
                photoURL = currentUser.photoUrl?.toString(),
                role = "member",
                joinedAt = Timestamp.now(),
                isActive = true
            )

            firestore.collection("families")
                .document(familyDoc.id)
                .update(
                    "members", com.google.firebase.firestore.FieldValue.arrayUnion(newMember)
                )
                .await()

            // Update user's family memberships
            firestore.collection("users")
                .document(currentUser.uid)
                .update(
                    "familyMemberships", com.google.firebase.firestore.FieldValue.arrayUnion(
                        mapOf(
                            "familyId" to familyDoc.id,
                            "familyName" to familyDto.name,
                            "role" to "member",
                            "joinedAt" to Timestamp.now()
                        )
                    )
                )
                .await()

            Timber.d("User joined family: ${familyDoc.id}")
            Result.Success(FamilyMapper.toDomain(familyDto.copy(members = familyDto.members + newMember)))

        } catch (e: Exception) {
            Timber.e(e, "Join family failed")
            Result.Error(FamilyHubException.UnknownException("Failed to join family", e))
        }
    }

    override suspend fun generateInviteCode(familyId: String, expiryHours: Int): Result<String> {
        return try {
            val inviteCode = UUID.randomUUID().toString().take(8).uppercase()
            val expiryDate = Timestamp(Date(System.currentTimeMillis() + expiryHours * 60 * 60 * 1000))

            firestore.collection("families")
                .document(familyId)
                .update(
                    mapOf(
                        "inviteCode" to inviteCode,
                        "inviteCodeExpiry" to expiryDate
                    )
                )
                .await()

            Timber.d("Invite code generated: $inviteCode")
            Result.Success(inviteCode)

        } catch (e: Exception) {
            Timber.e(e, "Generate invite code failed")
            Result.Error(FamilyHubException.UnknownException("Failed to generate invite code", e))
        }
    }

    override suspend fun updateFamilySettings(familyId: String, settings: Map<String, Any>): Result<Family> {
        return try {
            firestore.collection("families")
                .document(familyId)
                .update(settings)
                .await()

            val familyDoc = firestore.collection("families")
                .document(familyId)
                .get()
                .await()

            val familyDto = familyDoc.toObject(FamilyDto::class.java)!!
            Result.Success(FamilyMapper.toDomain(familyDto))

        } catch (e: Exception) {
            Timber.e(e, "Update settings failed")
            Result.Error(FamilyHubException.UnknownException("Failed to update settings", e))
        }
    }

    override suspend fun addMember(familyId: String, userId: String, role: UserRole): Result<Unit> {
        return try {
            // Implementation would add member to family
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(FamilyHubException.UnknownException("Failed to add member", e))
        }
    }

    override suspend fun removeMember(familyId: String, userId: String): Result<Unit> {
        return try {
            // Implementation would remove member from family
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(FamilyHubException.UnknownException("Failed to remove member", e))
        }
    }

    override suspend fun updateMemberRole(familyId: String, userId: String, role: UserRole): Result<Unit> {
        return try {
            // Implementation would update member role
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(FamilyHubException.UnknownException("Failed to update role", e))
        }
    }

    override suspend fun leaveFamily(familyId: String): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.Error(FamilyHubException.UnauthorizedException())

            // Remove from family members
            // Remove from user's family memberships
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(FamilyHubException.UnknownException("Failed to leave family", e))
        }
    }
}
