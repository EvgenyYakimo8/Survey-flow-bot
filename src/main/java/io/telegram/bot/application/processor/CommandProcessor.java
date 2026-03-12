package io.telegram.bot.application.processor;

import io.telegram.bot.application.dialog.TypageDialog;
import io.telegram.bot.application.service.InMemoryUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
@Order(1)  // Самый высокий приоритет
@RequiredArgsConstructor
public class CommandProcessor implements UpdateProcessor {

    private final InMemoryUserService userService;
    private final TypageDialog typageDialog;

    @Value("${payment.provider.token:}")
    private String providerToken;

    @Override
    public boolean canProcess(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && update.getMessage().getText().startsWith("/");
    }

    @Override
    public List<BotApiMethod<?>> process(Update update) {
        try {
            String command = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            log.info("Обработка команды: {} из чата {}", command, chatId);

            return switch (command) {
                case "/start" ->
                        List.of(createSimpleMessage(chatId, "👋 Привет! Я бот-шаблон. Используй /help для списка команд."));
                case "/help" -> List.of(createSimpleMessage(chatId, """
                        📋 Доступные команды:
                        /start - приветствие
                        /help - эта справка
                        /test - начать тест
                        /cancel - отменить диалог
                        """));
                case "/test" -> {
                    userService.resetDialog(chatId);
                    userService.setConversationState(chatId, "Q1");
                    userService.setDialogContext(chatId, new HashMap<>());
                    SendMessage firstQuestion = typageDialog.createFirstQuestion(chatId);
                    yield List.of(firstQuestion);
                }
                case "/cancel" -> {
                    userService.resetDialog(chatId);
                    yield List.of(createSimpleMessage(chatId, "Диалог отменён."));
                }
                default -> List.of(createSimpleMessage(chatId, "❌ Неизвестная команда. Введите /help"));
            };
        } catch (Exception e) {
            log.error("Ошибка обработки команды", e);
            return List.of();
        }
    }


    private SendMessage createSimpleMessage(long chatId, String text) {
        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .build();
    }
}