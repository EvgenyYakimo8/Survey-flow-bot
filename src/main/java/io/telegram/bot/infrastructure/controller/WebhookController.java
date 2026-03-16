package io.telegram.bot.infrastructure.controller;

import io.telegram.bot.application.service.UpdateHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebhookController {

    private final UpdateHandler updateHandler;
    private final TelegramClient telegramClient;

    @PostMapping("${telegram.bot.webhook-path}")
    public BotApiMethod<?> onUpdate(@RequestBody Update update) {
        List<BotApiMethod<?>> responses = updateHandler.handleUpdate(update);
        if (responses.isEmpty()) {
            return null;
        }

        // Первый ответ возвращаем в теле HTTP-ответа (синхронно)
        BotApiMethod<?> firstResponse = responses.getFirst();

        // Остальные ответы отправляем отдельными синхронными запросами
        for (int i = 1; i < responses.size(); i++) {
            try {
                telegramClient.execute(responses.get(i));
            } catch (TelegramApiException e) {
                log.error("Failed to send additional response: {}", responses.get(i), e);
            }
        }

        return firstResponse;
    }
}