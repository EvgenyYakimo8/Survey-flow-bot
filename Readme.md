## Переменные среды для Survey-flow-bot
BOT_TOKEN=8421542696:AAHm82WXSHWGfO5WQ2-F2x5b5pzqZBUJGj4

BOT_USERNAME=ShowCase_Basic_bot (имя используем как и в случае с ботом ShowCase_Basic_bot - так как он создан для тестов и разработок)

PORT=8080

## Команды

### 
Чтобы проверить, какой вебхук установлен сейчас:   https://api.telegram.org/bot<ВАШ_ТОКЕН>/getWebhookInfo

	С подставленным токеном -> https://api.telegram.org/bot7421542696:AAHm82WXSHWGfO5WQ2-F2x5b5pzqZBUJGj4/getWebhookInfo




Чтобы установить (перенастроить) вебхук вручную:   https://api.telegram.org/bot<ВАШ_ТОКЕН>/setWebhook?url=https://survey-flow-bot.onrender.com/webhook

	С подставленным токеном -> https://api.telegram.org/bot7421542696:AAHm82WXSHWGfO5WQ2-F2x5b5pzqZBUJGj4/setWebhook?url=https://survey-flow-bot.onrender.com/webhook


## Переменные среды для ShowCase_Basic_bot

// Для IDE
APP_PROFILE=dev

BOT_TOKEN=8421542696:AAHm82WXSHWGfO5WQ2-F2x5b5pzqZBUJGj4

BOT_USERNAME=ShowCase_Basic_bot

DB_URL=jdbc:postgresql://localhost:5432/botdb

DB_USER=postgres

DB_PASSWORD=secret

WEBHOOK_URL=https://example.com/webhook

PAYMENT_PROVIDER_TOKEN=твой_токен_от_провайдера



// Для .env
# ⚙️ Настройки бота (обязательные)
BOT_TOKEN=8421542696:AAHm82WXSHWGfO5WQ2-F2x5b5pzqZBUJGj4   # Токен от @BotFather
BOT_USERNAME=ShowCase_Basic_bot                              # Имя бота (без @)

# 🔐 Пароль для базы данных (обязательный, придумайте свой)
DB_PASSWORD=secret

# 🌍 Настройки webhook (если используется)
WEBHOOK_URL=https://example.com/webhook                      # Публичный HTTPS URL

# 🌦️ Ключи внешних API (необязательно, если не используются в боте)
OPENWEATHER_KEY=ваш_ключ_openweather                         # Для погоды
GEMINI_KEY=ваш_ключ_gemini                                   # Для Gemini AI

# 💳 Токен платежного провайдера (необязательно)
PAYMENT_PROVIDER_TOKEN=ваш_токен_провайдера

# 🚪 Порт на хосте (по умолчанию 8080, можно изменить)
HOST_PORT=8080