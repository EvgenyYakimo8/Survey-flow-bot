package io.telegram.bot.infrastructure.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Slf4j
@Component
@Profile("!dev")
@RequiredArgsConstructor
public class WebhookRegisterListener implements ApplicationListener<ApplicationReadyEvent> {

    private final TelegramClient telegramClient;

    @Value("${telegram.bot.webhook-url}")
    private String webhookUrl;

    @Value("${telegram.bot.webhook-path}")
    private String botPath;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            SetWebhook setWebhook = SetWebhook.builder()
                    .url(webhookUrl + botPath) // Собираем полный URL
                    .build();
            telegramClient.execute(setWebhook);
            log.info("✅ Вебхук успешно установлен на {}", webhookUrl + botPath);
        } catch (TelegramApiException e) {
            log.error("❌ Не удалось установить вебхук. Проверьте URL и токен.", e);
        }
    }
}