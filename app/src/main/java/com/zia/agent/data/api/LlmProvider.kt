package com.zia.agent.data.api

import kotlinx.coroutines.flow.Flow

/**
 * A single LLM provider definition.
 * Most providers are OpenAI-compatible — they share the same /v1/chat/completions format
 * and only differ in baseUrl + apiKey + available models.
 */
data class LlmProvider(
    val id: String,
    val name: String,
    val category: ProviderCategory,
    val baseUrl: String,
    val models: List<LlmModel>,
    val description: String,
    val isFree: Boolean,
    val isOpenAICompatible: Boolean = true,
    val signupUrl: String = ""
)

data class LlmModel(
    val id: String,
    val displayName: String,
    val contextWindow: Int = 0,
    val description: String = ""
)

enum class ProviderCategory {
    FREE,
    PAID
}

/**
 * Registry of all supported LLM providers.
 * Free providers first, then paid.
 */
object LlmProviderRegistry {

    val providers: List<LlmProvider> = listOf(
        // ===== FREE =====

        LlmProvider(
            id = "groq",
            name = "Groq",
            category = ProviderCategory.FREE,
            baseUrl = "https://api.groq.com/openai/v1",
            isFree = true,
            signupUrl = "https://console.groq.com/keys",
            description = "Inferência ultra-rápida em hardware LPU. Llama, Kimi K2, Qwen, Gemma.",
            models = listOf(
                LlmModel("llama-3.3-70b-versatile", "Llama 3.3 70B", 131072, "Melhor modelo geral grátis"),
                LlmModel("kimi-k2-instruct", "Kimi K2", 131072, "Kimi K2 no Groq"),
                LlmModel("llama-4-scout-17b-16e-instruct", "Llama 4 Scout", 131072),
                LlmModel("llama-4-maverick-17b-128e-instruct", "Llama 4 Maverick", 131072),
                LlmModel("qwen-qwq-32b", "Qwen QwQ 32B", 131072, "Raciocínio"),
                LlmModel("gemma2-9b-it", "Gemma 2 9B", 8192)
            )
        ),

        LlmProvider(
            id = "gemini",
            name = "Google Gemini",
            category = ProviderCategory.FREE,
            baseUrl = "https://generativelanguage.googleapis.com/v1beta/openai",
            isFree = true,
            signupUrl = "https://aistudio.google.com/app/apikey",
            description = "Free tier generoso: Gemini 2.5 Pro, 2.0 Flash. 15 RPM.",
            models = listOf(
                LlmModel("gemini-2.5-pro", "Gemini 2.5 Pro", 1048576, "Topo de linha grátis"),
                LlmModel("gemini-2.0-flash", "Gemini 2.0 Flash", 1048576, "Rápido e capaz"),
                LlmModel("gemini-1.5-flash", "Gemini 1.5 Flash", 1048576),
                LlmModel("gemini-1.5-flash-8b", "Gemini 1.5 Flash 8B", 1048576)
            )
        ),

        LlmProvider(
            id = "kimi",
            name = "Kimi (Moonshot)",
            category = ProviderCategory.FREE,
            baseUrl = "https://api.moonshot.cn/v1",
            isFree = true,
            signupUrl = "https://platform.moonshot.cn/console/api-keys",
            description = "Kimi AI da Moonshot. Janela de contexto enorme. Tem tier gratuito.",
            models = listOf(
                LlmModel("moonshot-v1-8k", "Moonshot V1 8K", 8192),
                LlmModel("moonshot-v1-32k", "Moonshot V1 32K", 32768),
                LlmModel("moonshot-v1-128k", "Moonshot V1 128K", 131072),
                LlmModel("kimi-latest", "Kimi Latest", 131072, "Modelo mais recente")
            )
        ),

        LlmProvider(
            id = "dola",
            name = "Dola (ByteDance)",
            category = ProviderCategory.FREE,
            baseUrl = "https://api.aimlapi.com/v1",
            isFree = true,
            signupUrl = "https://aimlapi.com/app/profile",
            description = "Dola Seed 2.0 da ByteDance via AIML API. Tier gratuito disponível.",
            models = listOf(
                LlmModel("dola-seed-2-0-code", "Dola Seed 2.0 Code", 131072, "Code-focused"),
                LlmModel("dola-seed-2-0", "Dola Seed 2.0", 131072)
            )
        ),

        LlmProvider(
            id = "mistral",
            name = "Mistral AI",
            category = ProviderCategory.FREE,
            baseUrl = "https://api.mistral.ai/v1",
            isFree = true,
            signupUrl = "https://console.mistral.ai/api-keys",
            description = "Modelos Apache 2.0. Mistral Small, Large, Codestral. 1 req/s grátis.",
            models = listOf(
                LlmModel("mistral-small-latest", "Mistral Small 3.1", 32768),
                LlmModel("mistral-large-latest", "Mistral Large 3", 131072),
                LlmModel("codestral-latest", "Codestral", 32768, "Especializado em código"),
                LlmModel("ministral-8b-latest", "Ministral 8B", 131072)
            )
        ),

        LlmProvider(
            id = "cerebras",
            name = "Cerebras",
            category = ProviderCategory.FREE,
            baseUrl = "https://api.cerebras.ai/v1",
            isFree = true,
            signupUrl = "https://cloud.cerebras.ai/",
            description = "Inferência em wafer-scale chip. Compara com Groq em velocidade.",
            models = listOf(
                LlmModel("llama-3.3-70b", "Llama 3.3 70B", 131072),
                LlmModel("qwen-3-235b-22b", "Qwen3 235B", 131072),
                LlmModel("llama-4-scout-17b-16e", "Llama 4 Scout", 131072)
            )
        ),

        LlmProvider(
            id = "openrouter_free",
            name = "OpenRouter (Free)",
            category = ProviderCategory.FREE,
            baseUrl = "https://openrouter.ai/api/v1",
            isFree = true,
            signupUrl = "https://openrouter.ai/keys",
            description = "30+ modelos grátis com um único API key. Modelos com sufixo :free.",
            models = listOf(
                LlmModel("deepseek/deepseek-r1:free", "DeepSeek R1 (Free)", 65536, "Raciocínio"),
                LlmModel("meta-llama/llama-3.3-70b-instruct:free", "Llama 3.3 70B (Free)", 65536),
                LlmModel("qwen/qwen-3-coder-480b:free", "Qwen3 Coder 480B (Free)", 131072),
                LlmModel("google/gemini-2.0-flash-exp:free", "Gemini 2.0 Flash (Free)", 1048576)
            )
        ),

        LlmProvider(
            id = "pollinations",
            name = "Pollinations AI",
            category = ProviderCategory.FREE,
            baseUrl = "https://gen.pollinations.ai/v1",
            isFree = true,
            signupUrl = "https://enter.pollinations.ai",
            description = "Plataforma open-source. Texto, imagem, áudio. Não requer chave para básico.",
            models = listOf(
                LlmModel("openai", "OpenAI (via Pollinations)", 8192),
                LlmModel("openai-large", "OpenAI Large", 8192),
                LlmModel("mistral", "Mistral", 8192),
                LlmModel("llama", "Llama", 8192)
            )
        ),

        LlmProvider(
            id = "github_models",
            name = "GitHub Models",
            category = ProviderCategory.FREE,
            baseUrl = "https://models.inference.ai.azure.com",
            isFree = true,
            signupUrl = "https://github.com/marketplace/models",
            description = "Inferência grátis via conta GitHub. GPT-4o, Llama, Phi, DeepSeek.",
            models = listOf(
                LlmModel("gpt-4o", "GPT-4o", 131072, "Frontier grátis via GitHub"),
                LlmModel("gpt-4o-mini", "GPT-4o Mini", 131072),
                LlmModel("Phi-4", "Phi-4", 16384),
                LlmModel("DeepSeek-R1", "DeepSeek R1", 65536)
            )
        ),

        LlmProvider(
            id = "huggingface",
            name = "Hugging Face",
            category = ProviderCategory.FREE,
            baseUrl = "https://api-inference.huggingface.co/v1",
            isFree = true,
            signupUrl = "https://huggingface.co/settings/tokens",
            description = "$0.10/mês em créditos grátis (auto-recarregável). Milhares de modelos.",
            models = listOf(
                LlmModel("meta-llama/Llama-3.3-70B-Instruct", "Llama 3.3 70B", 131072),
                LlmModel("Qwen/Qwen2.5-72B-Instruct", "Qwen 2.5 72B", 32768),
                LlmModel("mistralai/Mistral-7B-Instruct-v0.3", "Mistral 7B", 32768)
            )
        ),

        // ===== PAID =====

        LlmProvider(
            id = "openai",
            name = "OpenAI",
            category = ProviderCategory.PAID,
            baseUrl = "https://api.openai.com/v1",
            isFree = false,
            signupUrl = "https://platform.openai.com/api-keys",
            description = "GPT-4o, GPT-4 Turbo, o1. O padrão da indústria.",
            models = listOf(
                LlmModel("gpt-4o", "GPT-4o", 131072),
                LlmModel("gpt-4o-mini", "GPT-4o Mini", 131072, "Custo-benefício"),
                LlmModel("gpt-4-turbo", "GPT-4 Turbo", 131072),
                LlmModel("o1", "o1", 200000, "Raciocínio avançado"),
                LlmModel("o1-mini", "o1 Mini", 131072)
            )
        ),

        LlmProvider(
            id = "anthropic",
            name = "Anthropic",
            category = ProviderCategory.PAID,
            baseUrl = "https://api.anthropic.com",
            isFree = false,
            isOpenAICompatible = false,
            signupUrl = "https://console.anthropic.com/settings/keys",
            description = "Claude 3.5 Sonnet, Opus, Haiku. Excelente para raciocínio e código.",
            models = listOf(
                LlmModel("claude-3-5-sonnet-20241022", "Claude 3.5 Sonnet", 200000, "Melhor custo-benefício"),
                LlmModel("claude-3-5-haiku-20241022", "Claude 3.5 Haiku", 200000, "Rápido e barato"),
                LlmModel("claude-3-opus-20240229", "Claude 3 Opus", 200000, "Mais capaz")
            )
        ),

        LlmProvider(
            id = "openrouter_paid",
            name = "OpenRouter (Paid)",
            category = ProviderCategory.PAID,
            baseUrl = "https://openrouter.ai/api/v1",
            isFree = false,
            signupUrl = "https://openrouter.ai/keys",
            description = "Agregador: 300+ modelos de todos os provedores com um único API key.",
            models = listOf(
                LlmModel("anthropic/claude-3.5-sonnet", "Claude 3.5 Sonnet", 200000),
                LlmModel("openai/gpt-4o", "GPT-4o", 131072),
                LlmModel("google/gemini-pro-1.5", "Gemini Pro 1.5", 2000000),
                LlmModel("meta-llama/llama-3.3-70b-instruct", "Llama 3.3 70B", 131072)
            )
        ),

        LlmProvider(
            id = "groq_paid",
            name = "Groq (Labs)",
            category = ProviderCategory.PAID,
            baseUrl = "https://api.groq.com/openai/v1",
            isFree = false,
            signupUrl = "https://console.groq.com/keys",
            description = "Acesso a modelos premium do Groq com limites maiores.",
            models = listOf(
                LlmModel("llama-3.3-70b-versatile", "Llama 3.3 70B (Pro)", 131072)
            )
        )
    )

    val freeProviders get() = providers.filter { it.category == ProviderCategory.FREE }
    val paidProviders get() = providers.filter { it.category == ProviderCategory.PAID }

    fun byId(id: String): LlmProvider? = providers.find { it.id == id }
}
