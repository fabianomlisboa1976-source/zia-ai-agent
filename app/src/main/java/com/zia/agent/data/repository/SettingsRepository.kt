package com.zia.agent.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("zia_settings")

class SettingsRepository(private val context: Context) {

    private val ds = context.dataStore

    // ===== Theme =====
    val themeMode: Flow<String> = ds.data.map { it[KEY_THEME] ?: THEME_SYSTEM }

    suspend fun setThemeMode(mode: String) {
        ds.edit { it[KEY_THEME] = mode }
    }

    // ===== Active provider & model =====
    val activeProviderId: Flow<String?> = ds.data.map { it[KEY_PROVIDER] }
    val activeModelId: Flow<String?> = ds.data.map { it[KEY_MODEL] }

    suspend fun setActiveProvider(providerId: String, modelId: String) {
        ds.edit {
            it[KEY_PROVIDER] = providerId
            it[KEY_MODEL] = modelId
        }
    }

    // ===== API keys (one per provider) =====
    fun apiKeyFor(providerId: String): Flow<String?> =
        ds.data.map { it[stringPreferencesKey("api_key_$providerId")] }

    suspend fun setApiKey(providerId: String, key: String) {
        ds.edit { it[stringPreferencesKey("api_key_$providerId")] = key }
    }

    suspend fun clearApiKey(providerId: String) {
        ds.edit { it.remove(stringPreferencesKey("api_key_$providerId")) }
    }

    // ===== Supabase =====
    val supabaseUrl: Flow<String?> = ds.data.map { it[KEY_SUPABASE_URL] }
    val supabaseKey: Flow<String?> = ds.data.map { it[KEY_SUPABASE_KEY] }

    suspend fun setSupabaseConfig(url: String, key: String) {
        ds.edit {
            it[KEY_SUPABASE_URL] = url
            it[KEY_SUPABASE_KEY] = key
        }
    }

    // ===== Agent 24/7 =====
    val agentEnabled: Flow<Boolean> = ds.data.map { it[KEY_AGENT_ENABLED] ?: false }
    val agentIntervalSec: Flow<Int> = ds.data.map { it[KEY_AGENT_INTERVAL] ?: 60 }

    suspend fun setAgentEnabled(enabled: Boolean) {
        ds.edit { it[KEY_AGENT_ENABLED] = enabled }
    }

    suspend fun setAgentInterval(seconds: Int) {
        ds.edit { it[KEY_AGENT_INTERVAL] = seconds }
    }

    // ===== System prompt =====
    val systemPrompt: Flow<String> = ds.data.map {
        it[KEY_SYSTEM_PROMPT] ?: DEFAULT_SYSTEM_PROMPT
    }

    suspend fun setSystemPrompt(prompt: String) {
        ds.edit { it[KEY_SYSTEM_PROMPT] = prompt }
    }

    companion object {
        const val THEME_SYSTEM = "system"
        const val THEME_DARK = "dark"
        const val THEME_LIGHT = "light"

        private val KEY_THEME = stringPreferencesKey("theme_mode")
        private val KEY_PROVIDER = stringPreferencesKey("active_provider")
        private val KEY_MODEL = stringPreferencesKey("active_model")
        private val KEY_SUPABASE_URL = stringPreferencesKey("supabase_url")
        private val KEY_SUPABASE_KEY = stringPreferencesKey("supabase_key")
        private val KEY_AGENT_ENABLED = androidx.datastore.preferences.core.booleanPreferencesKey("agent_enabled")
        private val KEY_AGENT_INTERVAL = androidx.datastore.preferences.core.intPreferencesKey("agent_interval")
        private val KEY_SYSTEM_PROMPT = stringPreferencesKey("system_prompt")

        const val DEFAULT_SYSTEM_PROMPT = "Você é Z.ia, uma assistente de IA inteligente e útil. Responda de forma clara e concisa."
    }
}
