package com.familyhub.core.data.repository

import com.familyhub.core.common.exception.FamilyHubException
import com.familyhub.core.common.result.Result
import com.familyhub.core.data.mapper.MessageMapper
import com.familyhub.core.data.model.MessageDto
import com.familyhub.core.data.model.MessageReactionDto
import com.familyhub.core.domain.model.Message
import com.familyhub.core.domain.repository.ChatRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * FirebaseChatRepository
 * Firebase Firestore implementation of ChatRepository
 */
@Singleton
class FirebaseChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChatRepository {

    companion object {
        private const val MESSAGES_COLLECTION = "messages"
    }

    override fun getFamilyMessages(familyId: String, limit: Int): Flow<List<Message>> = callbackFlow {
        val listener = firestore.collection(MESSAGES_COLLECTION)
            .whereEqualTo("familyId", familyId)
            .whereEqualTo("isDeleted", false)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Timber.e(error, "Error listening to messages")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val dto = doc.toObject(MessageDto::class.java)
                        dto?.let { MessageMapper.toDomain(it) }
                    } catch (e: Exception) {
                        Timber.e(e, "Error parsing message document")
                        null
                    }
                }?.reversed() ?: emptyList() // Reverse to show oldest first

                trySend(messages)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun sendMessage(message: Message): Result<Message> {
        return try {
            val docRef = firestore.collection(MESSAGES_COLLECTION).document()
            val messageWithId = message.copy(id = docRef.id)
            val dto = MessageMapper.toDto(messageWithId)

            docRef.set(dto).await()
            Timber.d("Message sent successfully: ${docRef.id}")

            Result.Success(messageWithId)
        } catch (e: Exception) {
            Timber.e(e, "Error sending message")
            Result.Error(mapFirebaseException(e))
        }
    }

    override suspend fun updateMessage(message: Message): Result<Message> {
        return try {
            val updatedMessage = message.copy(
                isEdited = true,
                editedAt = java.time.LocalDateTime.now()
            )
            val dto = MessageMapper.toDto(updatedMessage)

            firestore.collection(MESSAGES_COLLECTION)
                .document(message.id)
                .set(dto)
                .await()

            Timber.d("Message updated successfully: ${message.id}")
            Result.Success(updatedMessage)
        } catch (e: Exception) {
            Timber.e(e, "Error updating message")
            Result.Error(mapFirebaseException(e))
        }
    }

    override suspend fun deleteMessage(messageId: String): Result<Unit> {
        return try {
            // Soft delete - mark as deleted instead of removing
            firestore.collection(MESSAGES_COLLECTION)
                .document(messageId)
                .update("isDeleted", true)
                .await()

            Timber.d("Message deleted successfully: $messageId")
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting message")
            Result.Error(mapFirebaseException(e))
        }
    }

    override suspend fun addReaction(messageId: String, userId: String, emoji: String): Result<Message> {
        return try {
            val docRef = firestore.collection(MESSAGES_COLLECTION).document(messageId)
            val document = docRef.get().await()

            if (!document.exists()) {
                return Result.Error(FamilyHubException.NotFoundException("Message not found"))
            }

            val dto = document.toObject(MessageDto::class.java)
                ?: return Result.Error(FamilyHubException.DatabaseException("Failed to parse message"))

            // Add or update reaction
            val reactions = dto.reactions.toMutableList()
            reactions.removeAll { it.userId == userId } // Remove existing reaction from this user
            reactions.add(MessageReactionDto(userId, emoji, Timestamp.now()))

            docRef.update("reactions", reactions).await()

            val updatedDto = dto.copy(reactions = reactions)
            Result.Success(MessageMapper.toDomain(updatedDto))
        } catch (e: Exception) {
            Timber.e(e, "Error adding reaction")
            Result.Error(mapFirebaseException(e))
        }
    }

    override suspend fun removeReaction(messageId: String, userId: String): Result<Message> {
        return try {
            val docRef = firestore.collection(MESSAGES_COLLECTION).document(messageId)
            val document = docRef.get().await()

            if (!document.exists()) {
                return Result.Error(FamilyHubException.NotFoundException("Message not found"))
            }

            val dto = document.toObject(MessageDto::class.java)
                ?: return Result.Error(FamilyHubException.DatabaseException("Failed to parse message"))

            // Remove reaction
            val reactions = dto.reactions.filterNot { it.userId == userId }

            docRef.update("reactions", reactions).await()

            val updatedDto = dto.copy(reactions = reactions)
            Result.Success(MessageMapper.toDomain(updatedDto))
        } catch (e: Exception) {
            Timber.e(e, "Error removing reaction")
            Result.Error(mapFirebaseException(e))
        }
    }

    override suspend fun markAsRead(familyId: String, userId: String, lastReadMessageId: String): Result<Unit> {
        return try {
            // Store read receipts in a separate subcollection
            firestore.collection("read_receipts")
                .document("${familyId}_${userId}")
                .set(mapOf(
                    "familyId" to familyId,
                    "userId" to userId,
                    "lastReadMessageId" to lastReadMessageId,
                    "timestamp" to Timestamp.now()
                ))
                .await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error marking messages as read")
            Result.Error(mapFirebaseException(e))
        }
    }

    private fun mapFirebaseException(exception: Exception): FamilyHubException {
        return when (exception) {
            is com.google.firebase.FirebaseNetworkException ->
                FamilyHubException.NetworkException(exception.message)
            is com.google.firebase.firestore.FirebaseFirestoreException ->
                FamilyHubException.DatabaseException(exception.message)
            else ->
                FamilyHubException.UnknownException(exception.message, exception)
        }
    }
}
