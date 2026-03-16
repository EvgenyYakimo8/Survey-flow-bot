/*
package io.telegram.bot.infrastructure.bot;

import io.telegram.bot.application.service.UpdateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@Component
@Profile("dev")
public class LongPollingBot implements SpringLongPollingBot {

    private final UpdateHandler updateHandler;
    private final TelegramClient telegramClient;
    private final String botToken;

    public LongPollingBot(UpdateHandler updateHandler,
                          TelegramClient telegramClient,
                          @Value("${telegram.bot.token}") String botToken) {
        this.updateHandler = updateHandler;
        this.telegramClient = telegramClient;
        this.botToken = botToken;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return updates -> {
            for (Update update : updates) {
                List<BotApiMethod<?>> responses = updateHandler.handleUpdate(update);
                for (BotApiMethod<?> response : responses) {
                    try {
                        telegramClient.execute(response);  // отправляем ответ
                    } catch (TelegramApiException e) {
                        log.error("Failed to send response", e);
                    }
                }
            }
        };
    }
}*/
