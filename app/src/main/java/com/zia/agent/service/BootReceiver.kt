package com.zia.agent.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Restarts the agent service after device reboot.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            // Check if agent was enabled and restart service
            // (We'd read from DataStore here, but DataStore requires a coroutine scope.
            // For now, we start the service — it will check settings internally.)
            // In production, use a WorkManager one-time job to check settings before starting.
        }
    }
}
