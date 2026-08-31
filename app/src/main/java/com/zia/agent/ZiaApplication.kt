package com.zia.agent

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.zia.agent.data.local.AppDatabase
import com.zia.agent.data.repository.ChatRepository
import com.zia.agent.data.repository.SettingsRepository
import com.zia.agent.data.supabase.SupabasePlugin

class ZiaApplication : Application() {

    val database by lazy { AppDatabase.getInstance(this) }
    val settingsRepository by lazy { SettingsRepository(this) }
    val chatRepository by lazy { ChatRepository(database.chatDao()) }
    val supabasePlugin by lazy { SupabasePlugin() }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AGENT_CHANNEL_ID,
                getString(R.string.agent_channel_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.agent_channel_desc)
                setShowBadge(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val AGENT_CHANNEL_ID = "zia_agent_channel"
    }
}
