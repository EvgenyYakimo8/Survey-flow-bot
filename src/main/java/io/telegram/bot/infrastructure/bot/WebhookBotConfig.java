package io.telegram.bot.infrastructure.bot;

import io.telegram.bot.application.service.UpdateHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.webhook.starter.SpringTelegramWebhookBot;

import java.util.List;

@Configuration
@Profile("!dev")
public class WebhookBotConfig {

    @Bean
    public SpringTelegramWebhookBot webhookBot(
            UpdateHandler updateHandler,
            @Value("${telegram.bot.webhook-path}") String botPath) {

        // 1. Логика обработки Update (то, что раньше было в onWebhookUpdateReceived)
        return new SpringTelegramWebhookBot(
                botPath,
                update -> {
                    List<BotApiMethod<?>> responses = updateHandler.handleUpdate(update);
                    return (responses != null && !responses.isEmpty()) ? responses.getFirst() : null;
                },
                () -> { /* логика установки вебхука, можно оставить пустой, если стартер сам делает */ },
                () -> { /* логика удаления вебхука */ }
        );
    }
}