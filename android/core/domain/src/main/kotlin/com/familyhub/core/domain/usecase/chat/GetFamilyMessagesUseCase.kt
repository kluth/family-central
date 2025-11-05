package com.familyhub.core.domain.usecase.chat

import com.familyhub.core.domain.model.Message
import com.familyhub.core.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * GetFamilyMessagesUseCase
 * Retrieves messages for a family chat
 */
class GetFamilyMessagesUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(familyId: String, limit: Int = 50): Flow<List<Message>> {
        return chatRepository.getFamilyMessages(familyId, limit)
    }
}
