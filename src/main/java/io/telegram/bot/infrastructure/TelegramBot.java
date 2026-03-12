package io.telegram.bot.infrastructure;

import io.telegram.bot.application.service.UpdateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

@Slf4j
@Component
public class TelegramBot extends SpringWebhookBot {

    private final UpdateHandler updateHandler;
    private final String botPath;
    private final String botUsername;
    private final String botToken;

    public TelegramBot(UpdateHandler updateHandler,
                       @Value("${telegram.bot.token}") String botToken,
                       @Value("${telegram.bot.username}") String botUsername,
                       @Value("${telegram.bot.webhook-path}") String botPath) {
        super(new SetWebhook(botPath), botToken);
        this.updateHandler = updateHandler;
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.botPath = botPath;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        var responses = updateHandler.handleUpdate(update);
        return responses.isEmpty() ? null : responses.getFirst();
    }

    @Override
    public String getBotPath() {
        return botPath;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}