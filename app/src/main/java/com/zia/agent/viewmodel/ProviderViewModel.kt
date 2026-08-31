package com.zia.agent.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zia.agent.ZiaApplication
import com.zia.agent.data.api.LlmProviderRegistry
import com.zia.agent.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProviderViewModel(
    private val settingsRepo: SettingsRepository
) : ViewModel() {

    val activeProviderId = settingsRepo.activeProviderId
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
    val activeModelId = settingsRepo.activeModelId
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val freeProviders = LlmProviderRegistry.freeProviders
    val paidProviders = LlmProviderRegistry.paidProviders

    fun selectProvider(providerId: String, modelId: String) {
        viewModelScope.launch {
            settingsRepo.setActiveProvider(providerId, modelId)
        }
    }

    fun saveApiKey(providerId: String, key: String) {
        viewModelScope.launch {
            settingsRepo.setApiKey(providerId, key)
        }
    }

    companion object {
        fun factory(app: ZiaApplication) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                ProviderViewModel(app.settingsRepository) as T
        }
    }
}
