package com.zia.agent.data.supabase

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Supabase plugin — manages the Supabase client connection.
 * Configured at runtime with user-provided URL + anon key.
 */
class SupabasePlugin {

    private var _client: SupabaseClient? = null
    private var _url: String? = null
    private var _key: String? = null

    private val _connectionState = MutableStateFlow(ConnectionState.DISCONNECTED)
    val connectionState: StateFlow<ConnectionState> = _connectionState

    val isConnected: Boolean get() = _client != null

    fun configure(url: String, key: String) {
        _url = url
        _key = key
        _client = createSupabaseClient(
            supabaseUrl = url,
            supabaseKey = key
        ) {
            install(Auth)
            install(Postgrest)
            install(Realtime)
            install(Storage)
        }
        _connectionState.value = ConnectionState.CONNECTED
    }

    fun disconnect() {
        _client = null
        _url = null
        _key = null
        _connectionState.value = ConnectionState.DISCONNECTED
    }

    fun client(): SupabaseClient? = _client

    fun url(): String? = _url

    // Convenience accessors
    fun auth(): Auth? = _client?.auth
    fun postgrest(): Postgrest? = _client?.postgrest
    fun realtime(): Realtime? = _client?.realtime
    fun storage(): Storage? = _client?.storage

    enum class ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        ERROR
    }
}
