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
        log.info("Будет установлен вебхук на URL: {}", fullWebhookUrl);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registerWebhook() {
        int maxAttempts = 20;
        int attempt = 0;
        long delayMillis = 5000; // 3 секунды

        while (attempt < maxAttempts) {
            attempt++;
            try {
                log.info("Попытка {} установки вебхука на {}", attempt, fullWebhookUrl);
                SetWebhook setWebhook = SetWebhook.builder()
                        .url(fullWebhookUrl)
                        .build();
                telegramClient.execute(setWebhook);
                log.info("✅ Вебхук успешно установлен на {}", fullWebhookUrl);
                return;
            } catch (TelegramApiException e) {
                log.warn("Попытка {} не удалась: {}. Осталось попыток: {}", attempt, e.getMessage(), maxAttempts - attempt);
                if (attempt < maxAttempts) {
                    try {
                        Thread.sleep(delayMillis);
                    } catch (InterruptedException ignored) {}
                }
            }
        }
        log.error("❌ Не удалось установить вебхук после {} попыток.", maxAttempts);
    }
}