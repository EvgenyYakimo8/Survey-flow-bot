package io.telegram.bot.infrastructure.controller;

import io.telegram.bot.application.service.UpdateHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class WebhookController {

    private final UpdateHandler updateHandler;

    public WebhookController(UpdateHandler updateHandler) {
        this.updateHandler = updateHandler;
    }

    @PostMapping("${telegram.bot.webhook-path}")
    public BotApiMethod<?> onUpdate(@RequestBody Update update) {
        // Ваш UpdateHandler возвращает список ответов. Мы берём первый.
        var responses = updateHandler.handleUpdate(update);
        return responses.isEmpty() ? null : responses.getFirst();
    }
}