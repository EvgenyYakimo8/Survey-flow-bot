/*
package io.telegram.bot.infrastructure.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Service
public class WebhookRegistrationService {

    private final TelegramClient telegramClient;
    private final String fullWebhookUrl;
    private final RestTemplate restTemplate = new RestTemplate();

    public WebhookRegistrationService(TelegramClient telegramClient,
                                      @Value("${telegram.bot.webhook-url}") String baseUrl,
                                      @Value("${telegram.bot.webhook-path}") String path) {
        this.telegramClient = telegramClient;
        this.fullWebhookUrl = baseUrl + path;
        log.info("Будет установлен вебхук на URL: {}", fullWebhookUrl);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registerWebhook() {
        int maxAttempts = 30; // Увеличим количество попыток
        int attempt = 0;
        long delayMillis = 60000; // 60 секунд

        while (attempt < maxAttempts) {
            attempt++;

            // Сначала проверяем, доступен ли наш эндпоинт извне
            if (isEndpointAvailable()) {
                log.info("Эндпоинт {} доступен, пробуем установить вебхук", fullWebhookUrl);
                try {
                    SetWebhook setWebhook = SetWebhook.builder()
                            .url(fullWebhookUrl)
                            .build();
                    telegramClient.execute(setWebhook);
                    log.info("✅ Вебхук успешно установлен на {}", fullWebhookUrl);
                    return;
                } catch (TelegramApiException e) {
                    log.warn("Попытка {} установки вебхука не удалась: {}", attempt, e.getMessage());
                }
            } else {
                log.info("Эндпоинт ещё не доступен (попытка {}), ждём...", attempt);
            }

            if (attempt < maxAttempts) {
                try {
                    Thread.sleep(delayMillis);
                } catch (InterruptedException ignored) {}
            }
        }

        log.error("❌ Не удалось установить вебхук после {} попыток. Возможно, проблема с доступностью эндпоинта.", maxAttempts);
    }

    private boolean isEndpointAvailable() {
        try {
            // Пробуем сделать запрос, Главное — получить любой ответ, не 404
            ResponseEntity<String> response = restTemplate.exchange(
                    fullWebhookUrl,
                    HttpMethod.OPTIONS,
                    null,
                    String.class
            );
            return response.getStatusCode().is2xxSuccessful() ||
                    response.getStatusCode().value() == 405; // 405 — это успех для нашего случая!
        } catch (Exception e) {
            return false;
        }
    }
}*/
