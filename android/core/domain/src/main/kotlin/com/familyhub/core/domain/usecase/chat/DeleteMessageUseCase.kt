package com.familyhub.core.domain.usecase.chat

import com.familyhub.core.common.exception.FamilyHubException
import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.repository.ChatRepository
import javax.inject.Inject

/**
 * DeleteMessageUseCase
 * Deletes a chat message with validation
 */
class DeleteMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(messageId: String): Result<Unit> {
        if (messageId.isBlank()) {
            return Result.Error(
                FamilyHubException.ValidationException("Message ID cannot be empty")
            )
        }
        return chatRepository.deleteMessage(messageId)
    }
}
