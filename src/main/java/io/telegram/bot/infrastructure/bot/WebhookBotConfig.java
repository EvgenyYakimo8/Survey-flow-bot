package io.telegram.bot.infrastructure.bot;

import io.telegram.bot.application.service.UpdateHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.webhook.starter.SpringTelegramWebhookBot;

import java.util.List;

@Configuration
@Profile("!dev")
public class WebhookBotConfig {

    @Bean
    public SpringTelegramWebhookBot webhookBot(
            UpdateHandler updateHandler,
            TelegramClient telegramClient,
            @Value("${telegram.bot.webhook-path}") String botPath,
            @Value("${telegram.bot.webhook-url}") String webhookUrl) {

        return new SpringTelegramWebhookBot(
                botPath,
                update -> {
                    List<BotApiMethod<?>> responses = updateHandler.handleUpdate(update);
                    return responses.isEmpty() ? null : responses.get(0);
                },
                () -> {  // установка вебхука
                    try {
                        SetWebhook setWebhook = SetWebhook.builder()
                                .url(webhookUrl + botPath)
                                .build();
                        telegramClient.execute(setWebhook);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException("Failed to set webhook", e);
                    }
                },
                () -> {  // удаление вебхука
                    try {
                        telegramClient.execute(new DeleteWebhook());
                    } catch (TelegramApiException e) {
                        throw new RuntimeException("Failed to delete webhook", e);
                    }
                }
        );
    }
}