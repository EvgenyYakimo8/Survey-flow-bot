package io.telegram.bot.infrastructure.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.webhook.starter.SpringTelegramWebhookBot;

@RestController
public class WebhookController {

    private final SpringTelegramWebhookBot webhookBot;

    public WebhookController(SpringTelegramWebhookBot webhookBot) {
        this.webhookBot = webhookBot;
    }

    @PostMapping("${telegram.bot.webhook-path}")
    public BotApiMethod<?> onWebhookUpdate(@RequestBody Update update) {
        return webhookBot.consumeUpdate(update);
    }
}