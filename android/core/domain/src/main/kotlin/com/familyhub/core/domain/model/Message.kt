package com.familyhub.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

/**
 * Message domain model
 * Represents a chat message in the family chat system
 */
@Parcelize
data class Message(
    val id: String = "",
    val familyId: String,
    val senderId: String,
    val senderName: String,
    val senderPhotoUrl: String? = null,
    val content: String,
    val type: MessageType = MessageType.TEXT,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    val isEdited: Boolean = false,
    val editedAt: LocalDateTime? = null,
    val replyToMessageId: String? = null,
    val attachments: List<MessageAttachment> = emptyList(),
    val reactions: List<MessageReaction> = emptyList(),
    val isDeleted: Boolean = false
) : Parcelable {
    val isFromCurrentUser: Boolean
        get() = false // TODO: Compare with current user ID
}

/**
 * Message type enum
 */
enum class MessageType {
    TEXT,
    IMAGE,
    FILE,
    VOICE,
    LOCATION,
    SYSTEM
}

/**
 * Message attachment
 */
@Parcelize
data class MessageAttachment(
    val id: String = "",
    val type: AttachmentType,
    val url: String,
    val fileName: String? = null,
    val fileSize: Long? = null,
    val thumbnailUrl: String? = null
) : Parcelable

/**
 * Attachment type
 */
enum class AttachmentType {
    IMAGE,
    VIDEO,
    DOCUMENT,
    AUDIO
}

/**
 * Message reaction
 */
@Parcelize
data class MessageReaction(
    val userId: String,
    val emoji: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
) : Parcelable
