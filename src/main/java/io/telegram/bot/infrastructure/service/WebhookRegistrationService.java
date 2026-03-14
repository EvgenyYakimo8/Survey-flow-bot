package io.telegram.bot.infrastructure.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Service
public class WebhookRegistrationService {

    private final TelegramClient telegramClient;
    private final String fullWebhookUrl;

    public WebhookRegistrationService(TelegramClient telegramClient,
                                      @Value("${telegram.bot.webhook-url}") String baseUrl,
                                      @Value("${telegram.bot.webhook-path}") String path) {
        this.telegramClient = telegramClient;
        this.fullWebhookUrl = baseUrl + path;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registerWebhook() {
        try {
            SetWebhook setWebhook = SetWebhook.builder()
                    .url(fullWebhookUrl)
                    .build();
            telegramClient.execute(setWebhook);
            log.info("✅ Вебхук успешно установлен на {}", fullWebhookUrl);
        } catch (TelegramApiException e) {
            log.error("❌ Не удалось установить вебхук: {}", e.getMessage(), e);
        }
    }
}