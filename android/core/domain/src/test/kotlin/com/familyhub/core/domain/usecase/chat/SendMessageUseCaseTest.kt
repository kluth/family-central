package com.familyhub.core.domain.usecase.chat

import com.familyhub.core.common.exception.FamilyHubException
import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.Message
import com.familyhub.core.domain.model.MessageType
import com.familyhub.core.domain.repository.ChatRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * SendMessageUseCaseTest
 * TDD tests for SendMessageUseCase (RED phase)
 */
class SendMessageUseCaseTest {

    private lateinit var sendMessageUseCase: SendMessageUseCase
    private lateinit var chatRepository: ChatRepository

    private val mockMessage = Message(
        id = "msg-123",
        familyId = "family-123",
        senderId = "user-123",
        senderName = "John Doe",
        content = "Hello, family!",
        type = MessageType.TEXT
    )

    @Before
    fun setup() {
        chatRepository = mockk()
        sendMessageUseCase = SendMessageUseCase(chatRepository)
    }

    @Test
    fun `invoke with valid message returns Success`() = runTest {
        // Given
        coEvery { chatRepository.sendMessage(any()) } returns Result.Success(mockMessage)

        // When
        val result = sendMessageUseCase(mockMessage)

        // Then
        assertThat(result).isInstanceOf(Result.Success::class.java)
        val successResult = result as Result.Success
        assertThat(successResult.data).isEqualTo(mockMessage)
        coVerify(exactly = 1) { chatRepository.sendMessage(mockMessage) }
    }

    @Test
    fun `invoke with empty content returns Error`() = runTest {
        // Given
        val messageWithEmptyContent = mockMessage.copy(content = "")

        // When
        val result = sendMessageUseCase(messageWithEmptyContent)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.ValidationException::class.java)
        assertThat(errorResult.exception.message).contains("content")
        coVerify(exactly = 0) { chatRepository.sendMessage(any()) }
    }

    @Test
    fun `invoke with blank content returns Error`() = runTest {
        // Given
        val messageWithBlankContent = mockMessage.copy(content = "   ")

        // When
        val result = sendMessageUseCase(messageWithBlankContent)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.ValidationException::class.java)
    }

    @Test
    fun `invoke with empty familyId returns Error`() = runTest {
        // Given
        val messageWithEmptyFamilyId = mockMessage.copy(familyId = "")

        // When
        val result = sendMessageUseCase(messageWithEmptyFamilyId)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.ValidationException::class.java)
        assertThat(errorResult.exception.message).contains("familyId")
    }

    @Test
    fun `invoke with empty senderId returns Error`() = runTest {
        // Given
        val messageWithEmptySenderId = mockMessage.copy(senderId = "")

        // When
        val result = sendMessageUseCase(messageWithEmptySenderId)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isInstanceOf(FamilyHubException.ValidationException::class.java)
        assertThat(errorResult.exception.message).contains("senderId")
    }

    @Test
    fun `invoke when repository fails returns Error`() = runTest {
        // Given
        val exception = FamilyHubException.DatabaseException("Failed to send message")
        coEvery { chatRepository.sendMessage(any()) } returns Result.Error(exception)

        // When
        val result = sendMessageUseCase(mockMessage)

        // Then
        assertThat(result).isInstanceOf(Result.Error::class.java)
        val errorResult = result as Result.Error
        assertThat(errorResult.exception).isEqualTo(exception)
    }
}
