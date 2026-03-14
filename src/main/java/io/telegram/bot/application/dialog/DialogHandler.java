package io.telegram.bot.application.dialog;

import io.telegram.bot.application.service.InMemoryUserService;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;
import java.util.Map;

/**
 * Обработчик конкретного сценария диалога (опрос, заказ, регистрация и т.д.).
 */
public interface DialogHandler {

    /**
     * Определяет, может ли этот обработчик обработать данное состояние.
     * @param state текущее состояние пользователя (из conversation_state)
     * @return true, если обработчик отвечает за это состояние
     */
    boolean canHandle(String state);

    /**
     * Обрабатывает входящее обновление в рамках активного диалога.
     * @param update        входящее обновление от Telegram
     * @param state         текущее состояние
     * @param context       контекст диалога (данные, накопленные на предыдущих шагах)
     * @return список ответных методов (SendMessage, AnswerCallbackQuery и т.д.)
     */
    List<BotApiMethod<?>> handle(Update update, String state, Map<String, Object> context, InMemoryUserService userService);
}