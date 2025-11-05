package com.familyhub.core.domain.repository

import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.Message
import kotlinx.coroutines.flow.Flow

/**
 * ChatRepository
 * Repository interface for chat data operations
 */
interface ChatRepository {
    /**
     * Get messages for a family chat
     * @param familyId The family ID
     * @param limit Maximum number of messages to retrieve
     * @return Flow of messages list
     */
    fun getFamilyMessages(familyId: String, limit: Int = 50): Flow<List<Message>>

    /**
     * Send a text message
     * @param message The message to send
     * @return Result with sent message or error
     */
    suspend fun sendMessage(message: Message): Result<Message>

    /**
     * Update an existing message
     * @param message The message to update
     * @return Result with updated message or error
     */
    suspend fun updateMessage(message: Message): Result<Message>

    /**
     * Delete a message
     * @param messageId The message ID to delete
     * @return Result with success or error
     */
    suspend fun deleteMessage(messageId: String): Result<Unit>

    /**
     * Add reaction to a message
     * @param messageId The message ID
     * @param userId The user ID adding the reaction
     * @param emoji The emoji reaction
     * @return Result with updated message or error
     */
    suspend fun addReaction(messageId: String, userId: String, emoji: String): Result<Message>

    /**
     * Remove reaction from a message
     * @param messageId The message ID
     * @param userId The user ID removing the reaction
     * @return Result with updated message or error
     */
    suspend fun removeReaction(messageId: String, userId: String): Result<Message>

    /**
     * Mark messages as read
     * @param familyId The family ID
     * @param userId The user ID marking messages as read
     * @param lastReadMessageId The last message ID that was read
     * @return Result with success or error
     */
    suspend fun markAsRead(familyId: String, userId: String, lastReadMessageId: String): Result<Unit>
}
