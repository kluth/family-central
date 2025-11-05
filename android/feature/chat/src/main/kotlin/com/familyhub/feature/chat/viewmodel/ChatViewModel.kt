package com.familyhub.feature.chat.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.familyhub.core.common.result.Result
import com.familyhub.core.domain.model.Message
import com.familyhub.core.domain.model.MessageType
import com.familyhub.core.domain.usecase.chat.DeleteMessageUseCase
import com.familyhub.core.domain.usecase.chat.GetFamilyMessagesUseCase
import com.familyhub.core.domain.usecase.chat.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getFamilyMessagesUseCase: GetFamilyMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage: StateFlow<String> = _errorMessage.asStateFlow()

    fun loadMessages(familyId: String) {
        viewModelScope.launch {
            getFamilyMessagesUseCase(familyId)
                .catch { exception ->
                    Timber.e(exception, "Error loading messages")
                    _uiState.value = ChatUiState.Error(exception.message ?: "Failed to load messages")
                }
                .collect { messages ->
                    _uiState.value = if (messages.isEmpty()) {
                        ChatUiState.Empty
                    } else {
                        ChatUiState.Success(messages)
                    }
                }
        }
    }

    fun sendMessage(familyId: String, senderId: String, senderName: String, content: String) {
        viewModelScope.launch {
            val message = Message(
                familyId = familyId,
                senderId = senderId,
                senderName = senderName,
                content = content,
                type = MessageType.TEXT
            )

            when (val result = sendMessageUseCase(message)) {
                is Result.Success -> {
                    Timber.d("Message sent successfully")
                }
                is Result.Error -> {
                    _errorMessage.value = result.exception.message ?: "Failed to send message"
                    Timber.e(result.exception, "Error sending message")
                }
                is Result.Loading -> {}
            }
        }
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            when (val result = deleteMessageUseCase(messageId)) {
                is Result.Success -> {
                    Timber.d("Message deleted successfully")
                }
                is Result.Error -> {
                    _errorMessage.value = result.exception.message ?: "Failed to delete message"
                    Timber.e(result.exception, "Error deleting message")
                }
                is Result.Loading -> {}
            }
        }
    }

    fun clearError() {
        _errorMessage.value = ""
    }
}

sealed class ChatUiState {
    object Loading : ChatUiState()
    object Empty : ChatUiState()
    data class Success(val messages: List<Message>) : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}
