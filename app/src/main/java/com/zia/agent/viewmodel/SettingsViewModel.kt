package com.zia.agent.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zia.agent.ZiaApplication
import com.zia.agent.data.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val app: ZiaApplication,
    private val settingsRepo: SettingsRepository,
    val supabasePlugin: SupabasePlugin
) : ViewModel() {

    val themeMode = settingsRepo.themeMode
        .stateIn(viewModelScope, SharingStarted.Lazily, SettingsRepository.THEME_SYSTEM)

    val supabaseUrl = settingsRepo.supabaseUrl
        .stateIn(viewModelScope, SharingStarted.Lazily, null)
    val supabaseKey = settingsRepo.supabaseKey
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val agentEnabled = settingsRepo.agentEnabled
        .stateIn(viewModelScope, SharingStarted.Lazily, false)
    val agentInterval = settingsRepo.agentIntervalSec
        .stateIn(viewModelScope, SharingStarted.Lazily, 60)

    val systemPrompt = settingsRepo.systemPrompt
        .stateIn(viewModelScope, SharingStarted.Lazily, SettingsRepository.DEFAULT_SYSTEM_PROMPT)

    fun setTheme(mode: String) {
        viewModelScope.launch { settingsRepo.setThemeMode(mode) }
    }

    fun setSupabaseConfig(url: String, key: String) {
        viewModelScope.launch {
            settingsRepo.setSupabaseConfig(url, key)
            if (url.isNotBlank() && key.isNotBlank()) {
                supabasePlugin.configure(url, key)
            }
        }
    }

    fun setAgentEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepo.setAgentEnabled(enabled)
            if (enabled) {
                com.zia.agent.service.AgentService.start(app)
            } else {
                com.zia.agent.service.AgentService.stop(app)
            }
        }
    }

    fun setAgentInterval(seconds: Int) {
        viewModelScope.launch { settingsRepo.setAgentInterval(seconds) }
    }

    fun setSystemPrompt(prompt: String) {
        viewModelScope.launch { settingsRepo.setSystemPrompt(prompt) }
    }

    companion object {
        fun factory(app: ZiaApplication) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T =
                SettingsViewModel(app, app.settingsRepository, app.supabasePlugin) as T
        }
    }
}
