package io.telegram.bot.infrastructure.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
public class WebhookRegistrationService {

    private final TelegramClient telegramClient;
    private final String fullWebhookUrl; // Сюда придёт полный URL

    public WebhookRegistrationService(TelegramClient telegramClient,
                                      // Собираем URL из двух частей прямо в конструкторе
                                      @Value("${telegram.bot.webhook-url}") String baseUrl,
                                      @Value("${telegram.bot.webhook-path}") String path) {
        this.telegramClient = telegramClient;
        this.fullWebhookUrl = baseUrl + path;
    }

    @PostConstruct
    public void registerWebhook() {
        try {
            SetWebhook setWebhook = SetWebhook.builder()
                    .url(fullWebhookUrl)
                    .build();
            telegramClient.execute(setWebhook);
            System.out.println("✅ Вебхук успешно установлен на " + fullWebhookUrl);
        } catch (TelegramApiException e) {
            System.err.println("❌ КРИТИЧЕСКАЯ ОШИБКА: Не удалось установить вебхук!");
            e.printStackTrace();
        }
    }
}