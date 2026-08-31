# Z.ia — Agente de IA para Android

App Android nativo (Kotlin + Jetpack Compose) que funciona como um agente de IA 24/7 com suporte a múltiplos LLMs gratuitos e pagos, integração com Supabase, e sistema de plugins.

## Funcionalidades

- **Chat com IA** — interface de chat com histórico salvo localmente (Room)
- **Múltiplos LLMs** — alterna entre provedores gratuitos e pagos
- **Agente 24/7** — foreground service mantém o agente ativo em segundo plano
- **Supabase** — plugin de banco de dados, autenticação e realtime
- **Sistema de plugins** — arquitetura extensível

## LLMs Suportados

### Gratuitos
| Provedor | Modelos | Como obter chave |
|----------|---------|------------------|
| **Groq** | Llama 3.3 70B, Kimi K2, Llama 4, Qwen QwQ | [console.groq.com/keys](https://console.groq.com/keys) |
| **Google Gemini** | Gemini 2.5 Pro, 2.0 Flash | [aistudio.google.com](https://aistudio.google.com/app/apikey) |
| **Kimi (Moonshot)** | Moonshot V1, Kimi Latest | [platform.moonshot.cn](https://platform.moonshot.cn/console/api-keys) |
| **Dola (ByteDance)** | Dola Seed 2.0, Code | [aimlapi.com](https://aimlapi.com/app/profile) |
| **Mistral AI** | Mistral Small, Large, Codestral | [console.mistral.ai](https://console.mistral.ai/api-keys) |
| **Cerebras** | Llama 3.3 70B, Qwen3 235B | [cloud.cerebras.ai](https://cloud.cerebras.ai/) |
| **OpenRouter (Free)** | DeepSeek R1, Llama 3.3, Qwen3 Coder | [openrouter.ai/keys](https://openrouter.ai/keys) |
| **Pollinations AI** | OpenAI, Mistral, Llama | [enter.pollinations.ai](https://enter.pollinations.ai) |
| **GitHub Models** | GPT-4o, Phi-4, DeepSeek R1 | [github.com/marketplace/models](https://github.com/marketplace/models) |
| **Hugging Face** | Llama 3.3, Qwen 2.5, Mistral | [huggingface.co/settings/tokens](https://huggingface.co/settings/tokens) |

### Pagos
| Provedor | Modelos |
|----------|---------|
| **OpenAI** | GPT-4o, GPT-4 Turbo, o1 |
| **Anthropic** | Claude 3.5 Sonnet, Opus, Haiku |
| **OpenRouter (Paid)** | 300+ modelos |

## Como compilar

### Opção 1: GitHub Actions (recomendado)

1. Crie um repositório no GitHub
2. Faça push de todo este projeto
3. O workflow `.github/workflows/build-apk.yml` compila automaticamente
4. Baixe o APK em **Actions → Build APK → Artifacts**

### Opção 2: Local

```bash
./gradlew assembleDebug
# APK em app/build/outputs/apk/debug/
```

Requer JDK 17 + Android SDK 34.

## Configuração no App

1. Abra o app → vá em **Modelos**
2. Escolha um provedor (aba Gratuitos ou Pagos)
3. Toque no provedor, cole sua API key
4. Selecione um modelo
5. Volte ao **Chat** e comece a conversar

### Supabase
1. Vá em **Config → Supabase**
2. Insira a URL do projeto e a anon key
3. Toque em "Salvar e conectar"
4. O plugin aparece como "Conectado" na aba Plugins

### Agente 24/7
1. Vá em **Config → Agente 24/7**
2. Ative o switch
3. Ajuste o intervalo de verificação
4. Uma notificação persistente aparece — o agente está ativo

## Estrutura do Projeto

```
zia/
├── app/
│   ├── build.gradle.kts
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   ├── java/com/zia/agent/
│   │   │   ├── ZiaApplication.kt
│   │   │   ├── MainActivity.kt
│   │   │   ├── data/
│   │   │   │   ├── api/          # LLM providers + service
│   │   │   │   ├── local/        # Room database
│   │   │   │   ├── repository/   # Settings + chat repos
│   │   │   │   └── supabase/     # Supabase plugin
│   │   │   ├── plugins/          # Plugin system
│   │   │   ├── service/          # Foreground service 24/7
│   │   │   ├── ui/
│   │   │   │   ├── theme/        # Material 3 theme
│   │   │   │   ├── screens/      # Chat, Providers, Plugins, Settings
│   │   │   │   └── navigation/   # NavHost + bottom bar
│   │   │   └── viewmodel/        # ViewModels
│   │   └── res/                  # Strings, colors, icons
├── .github/workflows/
│   └── build-apk.yml             # CI: compila APK
├── build.gradle.kts
├── settings.gradle.kts
└── gradlew
```

## Tech Stack

- **Kotlin 2.0** + **Jetpack Compose** (Material 3)
- **Room** — banco local
- **DataStore** — preferências
- **OkHttp** — chamadas de API
- **Supabase Kotlin SDK** — backend
- **Foreground Service** — operação 24/7
- **GitHub Actions** — CI/CD

## Licença

MIT
