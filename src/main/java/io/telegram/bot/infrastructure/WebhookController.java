package io.telegram.bot.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequiredArgsConstructor
public class WebhookController {
    private final TelegramBot telegramBot;

    @PostMapping("/webhook")
    public BotApiMethod<?> onWebhookUpdate(@RequestBody Update update) {
        return telegramBot.onWebhookUpdateReceived(update);
    }
}