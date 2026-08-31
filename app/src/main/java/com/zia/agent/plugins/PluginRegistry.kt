package com.zia.agent.plugins

import com.zia.agent.data.supabase.SupabasePlugin

/**
 * Plugin interface — each plugin extends the app's capabilities.
 * Plugins are registered in PluginRegistry and managed from the Plugins screen.
 */
interface Plugin {
    val id: String
    val name: String
    val description: String
    val isEnabled: Boolean
    fun initialize()
    fun teardown()
}

/**
 * Supabase plugin wrapper — database, auth, realtime, storage.
 */
class SupabaseDatabasePlugin(
    private val supabase: SupabasePlugin
) : Plugin {
    override val id = "supabase"
    override val name = "Supabase"
    override val description = "Banco de dados, autenticação e realtime via Supabase"
    override val isEnabled get() = supabase.isConnected

    override fun initialize() {
        // Configuration is done via SupabasePlugin.configure()
    }

    override fun teardown() {
        supabase.disconnect()
    }
}

/**
 * Registry of all available plugins.
 */
object PluginRegistry {
    val availablePlugins = mutableListOf<Plugin>()

    fun register(plugin: Plugin) {
        if (availablePlugins.none { it.id == plugin.id }) {
            availablePlugins.add(plugin)
        }
    }

    fun unregister(id: String) {
        availablePlugins.removeAll { it.id == id }
    }

    fun byId(id: String): Plugin? = availablePlugins.find { it.id == id }
}
