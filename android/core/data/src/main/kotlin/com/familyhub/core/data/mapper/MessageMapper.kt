package com.familyhub.core.data.mapper

import com.familyhub.core.data.model.MessageAttachmentDto
import com.familyhub.core.data.model.MessageDto
import com.familyhub.core.data.model.MessageReactionDto
import com.familyhub.core.domain.model.*
import com.google.firebase.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * MessageMapper
 * Maps between Message domain model and MessageDto
 */
object MessageMapper {

    fun toDomain(dto: MessageDto): Message {
        return Message(
            id = dto.id,
            familyId = dto.familyId,
            senderId = dto.senderId,
            senderName = dto.senderName,
            senderPhotoUrl = dto.senderPhotoUrl,
            content = dto.content,
            type = MessageType.valueOf(dto.type),
            timestamp = dto.timestamp.toLocalDateTime(),
            isEdited = dto.isEdited,
            editedAt = dto.editedAt?.toLocalDateTime(),
            replyToMessageId = dto.replyToMessageId,
            attachments = dto.attachments.map { it.toDomain() },
            reactions = dto.reactions.map { it.toDomain() },
            isDeleted = dto.isDeleted
        )
    }

    fun toDto(domain: Message): MessageDto {
        return MessageDto(
            id = domain.id,
            familyId = domain.familyId,
            senderId = domain.senderId,
            senderName = domain.senderName,
            senderPhotoUrl = domain.senderPhotoUrl,
            content = domain.content,
            type = domain.type.name,
            timestamp = domain.timestamp.toTimestamp(),
            isEdited = domain.isEdited,
            editedAt = domain.editedAt?.toTimestamp(),
            replyToMessageId = domain.replyToMessageId,
            attachments = domain.attachments.map { it.toDto() },
            reactions = domain.reactions.map { it.toDto() },
            isDeleted = domain.isDeleted
        )
    }

    // Extension functions for nested models
    private fun MessageAttachmentDto.toDomain(): MessageAttachment {
        return MessageAttachment(
            id = id,
            type = AttachmentType.valueOf(type),
            url = url,
            fileName = fileName,
            fileSize = fileSize,
            thumbnailUrl = thumbnailUrl
        )
    }

    private fun MessageAttachment.toDto(): MessageAttachmentDto {
        return MessageAttachmentDto(
            id = id,
            type = type.name,
            url = url,
            fileName = fileName,
            fileSize = fileSize,
            thumbnailUrl = thumbnailUrl
        )
    }

    private fun MessageReactionDto.toDomain(): MessageReaction {
        return MessageReaction(
            userId = userId,
            emoji = emoji,
            timestamp = timestamp.toLocalDateTime()
        )
    }

    private fun MessageReaction.toDto(): MessageReactionDto {
        return MessageReactionDto(
            userId = userId,
            emoji = emoji,
            timestamp = timestamp.toTimestamp()
        )
    }

    // Timestamp conversion helpers
    private fun Timestamp.toLocalDateTime(): LocalDateTime {
        return LocalDateTime.ofInstant(
            Instant.ofEpochSecond(seconds, nanoseconds.toLong()),
            ZoneId.systemDefault()
        )
    }

    private fun LocalDateTime.toTimestamp(): Timestamp {
        val instant = atZone(ZoneId.systemDefault()).toInstant()
        return Timestamp(instant.epochSecond, instant.nano)
    }
}
