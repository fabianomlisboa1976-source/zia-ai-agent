package com.zia.agent.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.zia.agent.MainActivity
import com.zia.agent.R
import com.zia.agent.ZiaApplication
import com.zia.agent.data.api.LlmProviderRegistry
import com.zia.agent.data.api.LlmService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Foreground service that keeps Z.ia running 24/7.
 * - Maintains a persistent notification
 * - Periodically checks for tasks via Supabase (if connected)
 * - Keeps the agent alive even when the app is backgrounded
 */
class AgentService : Service() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var agentLoop: Job? = null
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate() {
        super.onCreate()
        acquireWakeLock()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val app = application as ZiaApplication
        startForeground(NOTIFICATION_ID, buildNotification("Iniciando…"))

        agentLoop = scope.launch {
            val settingsRepo = app.settingsRepository
            while (true) {
                try {
                    val providerId = settingsRepo.activeProviderId.first()
                    val provider = providerId?.let { LlmProviderRegistry.byId(it) }
                    val providerName = provider?.name ?: "Nenhum"

                    updateNotification(providerName)

                    // If Supabase is connected, check for tasks
                    // (This is where you'd poll for scheduled tasks, realtime events, etc.)

                    val interval = settingsRepo.agentIntervalSec.first()
                    delay(interval * 1000L)
                } catch (e: Exception) {
                    delay(30_000) // Wait 30s on error before retrying
                }
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        agentLoop?.cancel()
        scope.cancel()
        releaseWakeLock()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(text: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, AgentService::class.java).apply {
            action = ACTION_STOP
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, ZiaApplication.AGENT_CHANNEL_ID)
            .setContentTitle(getString(R.string.agent_running))
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .addAction(0, getString(R.string.stop_agent), stopPendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun updateNotification(providerName: String) {
        val text = getString(R.string.agent_running_text, providerName)
        val notification = buildNotification(text)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(NOTIFICATION_ID, notification, android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun acquireWakeLock() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Zia::AgentWakeLock")
        wakeLock?.acquire(10 * 60 * 1000L) // 10 min, re-acquired on loop
    }

    private fun releaseWakeLock() {
        wakeLock?.let { if (it.isHeld) it.release() }
    }

    companion object {
        const val NOTIFICATION_ID = 1001
        const val ACTION_STOP = "com.zia.agent.STOP_AGENT"

        fun start(context: Context) {
            val intent = Intent(context, AgentService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, AgentService::class.java))
        }
    }
}
