package io.telegram.bot.application.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@Component
@Order(3)  // После команд
public class MessageProcessor implements UpdateProcessor {

    @Override
    public boolean canProcess(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && !update.getMessage().getText().startsWith("/");
    }

    @Override
    public List<BotApiMethod<?>> process(Update update) {
        try {
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            log.info("Обработка сообщения: {} из чата {}", text, chatId);

            // Здесь может быть сложная логика, вызов сервисов и т.д.
            String responseText = "Вы написали: " + text;

            SendMessage reply = SendMessage.builder()
                    .chatId(String.valueOf(chatId))
                    .text(responseText)
                    .build();
            return List.of(reply);
        } catch (Exception e) {
            log.error("Сообщение об ошибке обработки", e);
            return List.of();
        }
    }
}