package io.telegram.bot.application.dialog;

import io.telegram.bot.application.service.InMemoryUserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TypageDialog implements DialogHandler {

    private static final Map<String, Question> QUESTIONS = new LinkedHashMap<>();
    private static final Map<String, Result> RESULTS = new HashMap<>();

    // TelegramClient больше не нужен для отправки, так как все ответы возвращаются списком,
    // но может пригодиться в будущем. Оставим на всякий случай.
    private final TelegramClient telegramClient;

    public TypageDialog(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    static {
        // ----- Вопрос 1 -----
        QUESTIONS.put("Q1", new Question(
                "Какой у вас рост?",
                List.of(
                        new Option("q1_opt1", "до 160 см", "Q2"),
                        new Option("q1_opt2", "160–175 см", "Q3"),
                        new Option("q1_opt3", "выше 175 см", "Q4")
                )));
        // ... остальная инициализация без изменений ...
        // ----- 12 результатов -----
        RESULTS.put("R1", new Result("Типаж «Альфа»", "https://example.com/alpha"));
        // ... и так далее ...
    }

    @Override
    public boolean canHandle(String state) {
        return state != null && (state.startsWith("Q") || state.startsWith("R"));
    }

    @Override
    public List<BotApiMethod<?>> handle(Update update, String state, Map<String, Object> context, InMemoryUserService userService) {
        if (!update.hasCallbackQuery()) {
            return List.of();
        }

        long chatId = update.getCallbackQuery().getMessage().getChatId();
        String answerId = update.getCallbackQuery().getData();

        // Создаём список и сразу добавляем AnswerCallbackQuery
        List<BotApiMethod<?>> responses = new ArrayList<>();
        responses.add(AnswerCallbackQuery.builder()
                .callbackQueryId(update.getCallbackQuery().getId())
                .build());

        // Если состояние — результат
        if (state.startsWith("R")) {
            Result result = RESULTS.get(state);
            if (result == null) {
                userService.resetDialog(chatId);
                responses.add(createSimpleMessage(chatId, "Ошибка: результат не найден."));
                return responses;
            }
            userService.resetDialog(chatId);
            responses.add(createSimpleMessage(chatId,
                    "✅ Ваш типаж: *" + result.name + "*\n\nПодробнее: " + result.link));
            return responses;
        }

        // Получаем текущий вопрос
        Question current = QUESTIONS.get(state);
        if (current == null) {
            userService.resetDialog(chatId);
            responses.add(createSimpleMessage(chatId, "Ошибка. Начните тест заново командой /test"));
            return responses;
        }

        // Ищем выбранную опцию
        Option selected = current.options.stream()
                .filter(opt -> opt.id.equals(answerId))
                .findFirst()
                .orElse(null);

        if (selected == null) {
            // Неизвестная кнопка – повторяем вопрос
            responses.add(createQuestionMessage(chatId, current));
            return responses;
        }

        // Сохраняем ответ в контекст
        context.put(state, selected.text);
        userService.setDialogContext(chatId, context);

        // Переходим к следующему состоянию
        String next = selected.next;
        if (next.startsWith("R")) {
            Result result = RESULTS.get(next);
            if (result == null) {
                userService.resetDialog(chatId);
                responses.add(createSimpleMessage(chatId, "Ошибка: результат не найден."));
            } else {
                userService.resetDialog(chatId);
                responses.add(createSimpleMessage(chatId,
                        "✅ Ваш типаж: *" + result.name + "*\n\nПодробнее: " + result.link));
            }
        } else {
            userService.setConversationState(chatId, next);
            Question nextQuestion = QUESTIONS.get(next);
            if (nextQuestion == null) {
                userService.resetDialog(chatId);
                responses.add(createSimpleMessage(chatId, "Ошибка: следующий вопрос не найден."));
            } else {
                responses.add(createQuestionMessage(chatId, nextQuestion));
            }
        }

        return responses;
    }

    // Вспомогательные методы (без изменений)
    public SendMessage createFirstQuestion(long chatId) {
        return createQuestionMessage(chatId, QUESTIONS.get("Q1"));
    }

    private SendMessage createQuestionMessage(long chatId, Question q) {
        List<InlineKeyboardRow> rows = q.options.stream()
                .map(opt -> new InlineKeyboardRow(
                        InlineKeyboardButton.builder()
                                .text(opt.text)
                                .callbackData(opt.id)
                                .build()
                ))
                .collect(Collectors.toList());

        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(q.text)
                .replyMarkup(new InlineKeyboardMarkup(rows))
                .build();
    }

    private SendMessage createSimpleMessage(long chatId, String text) {
        return SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                //.parseMode("Markdown")
                .build();
    }

    @Data
    @AllArgsConstructor
    private static class Question {
        String text;
        List<Option> options;
    }

    @Data
    @AllArgsConstructor
    private static class Option {
        String id;
        String text;
        String next;
    }

    @Data
    @AllArgsConstructor
    private static class Result {
        String name;
        String link;
    }
}