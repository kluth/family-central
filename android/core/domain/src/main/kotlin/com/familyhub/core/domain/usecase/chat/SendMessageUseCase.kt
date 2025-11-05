package com.familyhub.core.domain.usecase.chat

import com.familyhub.core.common.exception.FamilyHubException
import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.Message
import com.familyhub.core.domain.repository.ChatRepository
import javax.inject.Inject

/**
 * SendMessageUseCase
 * Sends a chat message with validation
 */
class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(message: Message): Result<Message> {
        // Validate content
        if (message.content.isBlank()) {
            return Result.Error(
                FamilyHubException.ValidationException("Message content cannot be empty")
            )
        }

        // Validate familyId
        if (message.familyId.isBlank()) {
            return Result.Error(
                FamilyHubException.ValidationException("Message familyId cannot be empty")
            )
        }

        // Validate senderId
        if (message.senderId.isBlank()) {
            return Result.Error(
                FamilyHubException.ValidationException("Message senderId cannot be empty")
            )
        }

        return chatRepository.sendMessage(message)
    }
}
