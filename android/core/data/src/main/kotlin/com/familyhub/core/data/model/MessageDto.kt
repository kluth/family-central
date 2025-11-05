package com.familyhub.core.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

/**
 * MessageDto
 * Firestore Data Transfer Object for Message
 */
data class MessageDto(
    @DocumentId val id: String = "",
    val familyId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderPhotoUrl: String? = null,
    val content: String = "",
    val type: String = "TEXT",
    val timestamp: Timestamp = Timestamp.now(),
    val isEdited: Boolean = false,
    val editedAt: Timestamp? = null,
    val replyToMessageId: String? = null,
    val attachments: List<MessageAttachmentDto> = emptyList(),
    val reactions: List<MessageReactionDto> = emptyList(),
    val isDeleted: Boolean = false
)

/**
 * MessageAttachmentDto
 * DTO for message attachment
 */
data class MessageAttachmentDto(
    val id: String = "",
    val type: String = "IMAGE",
    val url: String = "",
    val fileName: String? = null,
    val fileSize: Long? = null,
    val thumbnailUrl: String? = null
)

/**
 * MessageReactionDto
 * DTO for message reaction
 */
data class MessageReactionDto(
    val userId: String = "",
    val emoji: String = "",
    val timestamp: Timestamp = Timestamp.now()
)
