package io.telegram.bot.application.processor;

import io.telegram.bot.application.dialog.DialogHandler;
import io.telegram.bot.application.service.InMemoryUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Order(2)
@RequiredArgsConstructor
public class DialogProcessor implements UpdateProcessor {

    private final InMemoryUserService userService;
    private final List<DialogHandler> dialogHandlers;

    @Override
    public boolean canProcess(Update update) {
        Long chatId = null;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        }
        if (chatId == null) return false;
        String state = userService.getConversationState(chatId);
        return state != null && !state.isEmpty();
    }

    @Override
    public List<BotApiMethod<?>> process(Update update) {
        Long chatId;
        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
        } else {
            return List.of();
        }
        String state = userService.getConversationState(chatId);
        Map<String, Object> context = userService.getDialogContext(chatId);

        for (DialogHandler handler : dialogHandlers) {
            if (handler.canHandle(state)) {
                List<BotApiMethod<?>> responses = handler.handle(update, state, context, userService);
                if (update.hasCallbackQuery()) {
                    AnswerCallbackQuery answer = new AnswerCallbackQuery();
                    answer.setCallbackQueryId(update.getCallbackQuery().getId());
                    // Можно добавить короткое уведомление (необязательно)
                    // answer.setText("✓");
                    responses = new ArrayList<>(responses);
                    responses.addFirst(answer); // Чтобы ответ на callback ушёл первым
                }
                return responses;
            }
        }
        userService.resetDialog(chatId);
        return List.of();
    }
}