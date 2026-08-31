package com.zia.agent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.zia.agent.ui.navigation.ZiaApp
import com.zia.agent.ui.theme.ZiaTheme
import com.zia.agent.data.repository.SettingsRepository

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as ZiaApplication
        val settingsRepo = app.settingsRepository

        setContent {
            val themeMode by settingsRepo.themeMode.collectAsState(initial = SettingsRepository.THEME_SYSTEM)

            ZiaTheme(themeMode = themeMode) {
                ZiaApp(
                    application = app,
                    settingsRepository = settingsRepo
                )
            }
        }
    }
}
