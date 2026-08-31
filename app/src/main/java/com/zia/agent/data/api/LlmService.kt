package com.zia.agent.data.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Unified chat service. Handles both OpenAI-compatible and Anthropic providers.
 */
class LlmService(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
) {

    suspend fun chat(
        provider: LlmProvider,
        model: LlmModel,
        apiKey: String,
        messages: List<ChatMessage>,
        temperature: Double = 0.7,
        maxTokens: Int = 4096
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            if (provider.isOpenAICompatible) {
                chatOpenAICompatible(provider, model, apiKey, messages, temperature, maxTokens)
            } else if (provider.id == "anthropic") {
                chatAnthropic(provider, model, apiKey, messages, temperature, maxTokens)
            } else {
                Result.failure(IllegalArgumentException("Provider não suportado: ${provider.id}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun chatOpenAICompatible(
        provider: LlmProvider,
        model: LlmModel,
        apiKey: String,
        messages: List<ChatMessage>,
        temperature: Double,
        maxTokens: Int
    ): Result<String> {
        val body = JSONObject().apply {
            put("model", model.id)
            put("temperature", temperature)
            put("max_tokens", maxTokens)
            put("stream", false)
            val msgs = JSONArray()
            messages.forEach { msg ->
                msgs.put(JSONObject().apply {
                    put("role", msg.role.value)
                    put("content", msg.content)
                })
            }
            put("messages", msgs)
        }

        val request = Request.Builder()
            .url("${provider.baseUrl}/chat/completions")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .post(body.toString().toRequestBody(JSON_MEDIA))
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                return Result.failure(Exception("HTTP ${response.code}: ${parseError(responseBody)}"))
            }

            val json = JSONObject(responseBody)
            val content = json
                .optJSONArray("choices")
                ?.optJSONObject(0)
                ?.optJSONObject("message")
                ?.optString("content")
                ?: return Result.failure(Exception("Resposta vazia do provedor"))

            Result.success(content)
        }
    }

    private fun chatAnthropic(
        provider: LlmProvider,
        model: LlmModel,
        apiKey: String,
        messages: List<ChatMessage>,
        temperature: Double,
        maxTokens: Int
    ): Result<String> {
        // Anthropic uses different API format: system message goes separately
        val systemMsg = messages.firstOrNull { it.role == ChatRole.SYSTEM }?.content ?: ""
        val chatMessages = messages.filter { it.role != ChatRole.SYSTEM }

        val body = JSONObject().apply {
            put("model", model.id)
            put("max_tokens", maxTokens)
            put("temperature", temperature)
            if (systemMsg.isNotEmpty()) put("system", systemMsg)
            val msgs = JSONArray()
            chatMessages.forEach { msg ->
                msgs.put(JSONObject().apply {
                    put("role", if (msg.role == ChatRole.ASSISTANT) "assistant" else "user")
                    put("content", msg.content)
                })
            }
            put("messages", msgs)
        }

        val request = Request.Builder()
            .url("${provider.baseUrl}/v1/messages")
            .addHeader("x-api-key", apiKey)
            .addHeader("anthropic-version", "2023-06-01")
            .addHeader("Content-Type", "application/json")
            .post(body.toString().toRequestBody(JSON_MEDIA))
            .build()

        client.newCall(request).execute().use { response ->
            val responseBody = response.body?.string() ?: ""
            if (!response.isSuccessful) {
                return Result.failure(Exception("HTTP ${response.code}: ${parseError(responseBody)}"))
            }

            val json = JSONObject(responseBody)
            val content = json
                .optJSONArray("content")
                ?.optJSONObject(0)
                ?.optString("text")
                ?: return Result.failure(Exception("Resposta vazia do Anthropic"))

            Result.success(content)
        }
    }

    private fun parseError(body: String): String {
        return try {
            val json = JSONObject(body)
            json.optJSONObject("error")?.optString("message") ?: body.take(200)
        } catch (e: Exception) {
            body.take(200)
        }
    }

    companion object {
        private val JSON_MEDIA = "application/json".toMediaType()
    }
}

enum class ChatRole(val value: String) {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant")
}

data class ChatMessage(
    val role: ChatRole,
    val content: String
)
