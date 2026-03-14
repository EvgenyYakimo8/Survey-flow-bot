package io.telegram.bot.application.processor;

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

/**
 * Обработчик определённого типа обновлений от Telegram.
 */
public interface UpdateProcessor {

    /**
     * Проверяет, может ли этот процессор обработать данное обновление.
     */
    boolean canProcess(Update update);

    /**
     * Обрабатывает обновление и возвращает список ответных сообщений.
     */
    List<BotApiMethod<?>> process(Update update);
}