package com.zia.agent.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.zia.agent.data.repository.SettingsRepository

@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository
) {
    val themeMode by settingsRepository.themeMode.collectAsState(initial = SettingsRepository.THEME_SYSTEM)
    val supabaseUrl by settingsRepository.supabaseUrl.collectAsState(initial = "")
    val supabaseKey by settingsRepository.supabaseKey.collectAsState(initial = "")
    val agentEnabled by settingsRepository.agentEnabled.collectAsState(initial = false)
    val agentInterval by settingsRepository.agentIntervalSec.collectAsState(initial = 60)
    val systemPrompt by settingsRepository.systemPrompt.collectAsState(initial = SettingsRepository.DEFAULT_SYSTEM_PROMPT)

    var urlInput by remember(supabaseUrl) { mutableStateOf(supabaseUrl ?: "") }
    var keyInput by remember(supabaseKey) { mutableStateOf(supabaseKey ?: "") }
    var promptInput by remember(systemPrompt) { mutableStateOf(systemPrompt) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text("Configurações", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        }

        // ===== Theme =====
        item {
            SettingsCard("Tema") {
                val options = listOf(
                    SettingsRepository.THEME_SYSTEM to "Sistema",
                    SettingsRepository.THEME_DARK to "Escuro",
                    SettingsRepository.THEME_LIGHT to "Claro"
                )
                options.forEach { (value, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = themeMode == value,
                                onClick = { settingsRepository.setThemeMode(value) }
                            )
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = themeMode == value, onClick = { settingsRepository.setThemeMode(value) })
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text(label, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        // ===== Supabase =====
        item {
            SettingsCard("Supabase") {
                Text("URL do projeto", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    placeholder = { Text("https://xxxx.supabase.co") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Chave anônima (anon key)", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = keyInput,
                    onValueChange = { keyInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    placeholder = { Text("eyJhbGciOi...") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { settingsRepository.setSupabaseConfig(urlInput, keyInput) }) {
                    Text("Salvar e conectar")
                }
            }
        }

        // ===== Agent 24/7 =====
        item {
            SettingsCard("Agente 24/7") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Manter agente ativo", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                        Text("Roda em segundo plano com notificação persistente", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                    Switch(
                        checked = agentEnabled,
                        onCheckedChange = { settingsRepository.setAgentEnabled(it) }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text("Intervalo de verificação: ${agentInterval}s", style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = agentInterval.toFloat(),
                    onValueChange = { settingsRepository.setAgentInterval(it.toInt()) },
                    valueRange = 10f..600f,
                    steps = 58
                )
            }
        }

        // ===== System Prompt =====
        item {
            SettingsCard("Prompt do Sistema") {
                OutlinedTextField(
                    value = promptInput,
                    onValueChange = { promptInput = it },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    placeholder = { Text("Instruções base para a Z.ia…") },
                    maxLines = 5
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { settingsRepository.setSystemPrompt(promptInput) }) {
                    Text("Salvar prompt")
                }
            }
        }

        // ===== About =====
        item {
            SettingsCard("Sobre") {
                Text("Z.ia v1.0.0", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                Text("Agente de IA para Android", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Text("Kotlin + Jetpack Compose + Supabase", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
            }
        }
    }
}

@Composable
private fun SettingsCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}
