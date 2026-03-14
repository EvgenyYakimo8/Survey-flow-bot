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

        return new SpringTelegramWebhookBot(
                botPath,
                update -> {
                    List<BotApiMethod<?>> responses = updateHandler.handleUpdate(update);
                    return responses.isEmpty() ? null : responses.get(0);
                },
                () -> {}, // Оставляем пустым
                () -> {}  // Оставляем пустым
        );
    }
}