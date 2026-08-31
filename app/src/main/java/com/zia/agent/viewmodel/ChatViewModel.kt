package com.zia.agent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zia.agent.ZiaApplication
import com.zia.agent.data.api.ChatMessage
import com.zia.agent.data.api.ChatRole
import com.zia.agent.data.api.LlmProviderRegistry
import com.zia.agent.data.api.LlmService
import com.zia.agent.data.repository.ChatRepository
import com.zia.agent.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatRepo: ChatRepository,
    private val settingsRepo: SettingsRepository,
    private val llmService: LlmService = LlmService()
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val activeProviderId = settingsRepo.activeProviderId
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
    val activeModelId = settingsRepo.activeModelId
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val systemPrompt = settingsRepo.systemPrompt
        .stateIn(viewModelScope, SharingStarted.Lazily, SettingsRepository.DEFAULT_SYSTEM_PROMPT)

    fun sendMessage(chatId: Long, userMessage: String) {
        if (userMessage.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            // Save user message
            chatRepo.addMessage(chatId, "user", userMessage)

            // Build message history
            val history = chatRepo.getMessagesSync(chatId)
            val systemPromptValue = systemPrompt.value
            val messages = buildList {
                add(ChatMessage(ChatRole.SYSTEM, systemPromptValue))
                history.forEach { msg ->
                    val role = when (msg.role) {
                        "user" -> ChatRole.USER
                        "assistant" -> ChatRole.ASSISTANT
                        else -> ChatRole.SYSTEM
                    }
                    if (!msg.isError) add(ChatMessage(role, msg.content))
                }
            }

            // Get active provider and API key
            val providerId = activeProviderId.value
            val modelId = activeModelId.value

            if (providerId == null || modelId == null) {
                _error.value = "Nenhum modelo ativo. Vá em 'Modelos' para selecionar."
                _isLoading.value = false
                return@launch
            }

            val provider = LlmProviderRegistry.byId(providerId)
            val model = provider?.models?.find { it.id == modelId }

            if (provider == null || model == null) {
                _error.value = "Modelo não encontrado"
                _isLoading.value = false
                return@launch
            }

            val apiKey = settingsRepo.apiKeyFor(providerId).first()

            if (apiKey.isNullOrBlank() && provider.id != "pollinations") {
                _error.value = "API key não configurada para ${provider.name}"
                _isLoading.value = false
                return@launch
            }

            // Call LLM
            val result = llmService.chat(
                provider = provider,
                model = model,
                apiKey = apiKey ?: "",
                messages = messages
            )

            result.onSuccess { response ->
                chatRepo.addMessage(chatId, "assistant", response)
            }.onFailure { e ->
                chatRepo.addMessage(chatId, "assistant", "Erro: ${e.message}", isError = true)
                _error.value = e.message
            }

            _isLoading.value = false
        }
    }

    companion object {
        fun factory(app: ZiaApplication) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ChatViewModel(app.chatRepository, app.settingsRepository) as T
        }
    }
}
