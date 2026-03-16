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
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Component
public class TypageDialog implements DialogHandler {

    private static final Map<String, Question> QUESTIONS = new LinkedHashMap<>();
    private static final Map<String, Result> RESULTS = new HashMap<>();

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

        // ----- Вопрос 2 (для роста до 160) -----
        QUESTIONS.put("Q2", new Question(
                "Уточните телосложение:",
                List.of(
                        new Option("q2_opt1", "Хрупкое", "Q5"),
                        new Option("q2_opt2", "Среднее", "Q6"),
                        new Option("q2_opt3", "Плотное", "Q7")
                )));

        // ----- Вопрос 3 (для роста 160–175) -----
        QUESTIONS.put("Q3", new Question(
                "Какая у вас форма лица?",
                List.of(
                        new Option("q3_opt1", "Овальное", "Q8"),
                        new Option("q3_opt2", "Круглое", "Q9"),
                        new Option("q3_opt3", "Квадратное", "R1")
                )));

        // ----- Вопрос 4 (для роста выше 175) -----
        QUESTIONS.put("Q4", new Question(
                "Выберите цветотип:",
                List.of(
                        new Option("q4_opt1", "Весна", "R2"),
                        new Option("q4_opt2", "Лето", "R3"),
                        new Option("q4_opt3", "Осень", "R4"),
                        new Option("q4_opt4", "Зима", "R5")
                )));

        // ----- Вопрос 5 -----
        QUESTIONS.put("Q5", new Question(
                "Какой у вас тип волос?",
                List.of(
                        new Option("q5_opt1", "Прямые", "R6"),
                        new Option("q5_opt2", "Волнистые", "R7"),
                        new Option("q5_opt3", "Кудрявые", "R8")
                )));

        // ----- Вопрос 6 -----
        QUESTIONS.put("Q6", new Question(
                "Какой у вас разрез глаз?",
                List.of(
                        new Option("q6_opt1", "Миндалевидный", "R9"),
                        new Option("q6_opt2", "Круглый", "R10")
                )));

        // ----- Вопрос 7 -----
        QUESTIONS.put("Q7", new Question(
                "Выберите оттенок кожи:",
                List.of(
                        new Option("q7_opt1", "Светлый", "R11"),
                        new Option("q7_opt2", "Смуглый", "R12")
                )));

        // ----- Вопрос 8 (для овального лица) -----
        QUESTIONS.put("Q8", new Question(
                "Дополнительный вопрос для овального лица:",
                List.of(
                        new Option("q8_opt1", "Вариант А", "R1"),
                        new Option("q8_opt2", "Вариант Б", "R2")
                )));

        // ----- Вопрос 9 (для круглого лица) -----
        QUESTIONS.put("Q9", new Question(
                "Дополнительный вопрос для круглого лица:",
                List.of(
                        new Option("q9_opt1", "Вариант В", "R3"),
                        new Option("q9_opt2", "Вариант Г", "R4")
                )));

        // ----- 12 результатов -----
        RESULTS.put("R1", new Result("Типаж «Альфа»", "https://example.com/alpha"));
        RESULTS.put("R2", new Result("Типаж «Бета»", "https://example.com/beta"));
        RESULTS.put("R3", new Result("Типаж «Гамма»", "https://example.com/gamma"));
        RESULTS.put("R4", new Result("Типаж «Дельта»", "https://example.com/delta"));
        RESULTS.put("R5", new Result("Типаж «Эпсилон»", "https://example.com/epsilon"));
        RESULTS.put("R6", new Result("Типаж «Дзета»", "https://example.com/zeta"));
        RESULTS.put("R7", new Result("Типаж «Эта»", "https://example.com/eta"));
        RESULTS.put("R8", new Result("Типаж «Тета»", "https://example.com/theta"));
        RESULTS.put("R9", new Result("Типаж «Йота»", "https://example.com/iota"));
        RESULTS.put("R10", new Result("Типаж «Каппа»", "https://example.com/kappa"));
        RESULTS.put("R11", new Result("Типаж «Лямбда»", "https://example.com/lambda"));
        RESULTS.put("R12", new Result("Типаж «Мю»", "https://example.com/mu"));
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

        // Если состояние — результат
        if (state.startsWith("R")) {
            Result result = RESULTS.get(state);
            if (result == null) {
                userService.resetDialog(chatId);
                return List.of(createSimpleMessage(chatId, "Ошибка: результат не найден."));
            }
            userService.resetDialog(chatId);
            return List.of(createSimpleMessage(chatId,
                    "✅ Ваш типаж: *" + result.name + "*\n\nПодробнее: " + result.link));
        }

        // Получаем текущий вопрос
        Question current = QUESTIONS.get(state);
        if (current == null) {
            userService.resetDialog(chatId);
            return List.of(createSimpleMessage(chatId, "Ошибка. Начните тест заново командой /test"));
        }

        // Ищем выбранную опцию
        Option selected = current.options.stream()
                .filter(opt -> opt.id.equals(answerId))
                .findFirst()
                .orElse(null);

        if (selected == null) {
            // Неизвестная кнопка – повторяем вопрос
            return List.of(createQuestionMessage(chatId, current));
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
                return List.of(createSimpleMessage(chatId, "Ошибка: результат не найден."));
            } else {
                userService.resetDialog(chatId);
                return List.of(createSimpleMessage(chatId,
                        "✅ Ваш типаж: *" + result.name + "*\n\nПодробнее: " + result.link));
            }
        } else {
            userService.setConversationState(chatId, next);
            Question nextQuestion = QUESTIONS.get(next);
            if (nextQuestion == null) {
                userService.resetDialog(chatId);
                return List.of(createSimpleMessage(chatId, "Ошибка: следующий вопрос не найден."));
            } else {
                return List.of(createQuestionMessage(chatId, nextQuestion));
            }
        }
    }

    /**
     * Создаёт AnswerCallbackQuery для синхронного ответа на нажатие кнопки.
     */
    private List<BotApiMethod<?>> createAnswerCallback(Update update) {
        AnswerCallbackQuery answer = AnswerCallbackQuery.builder()
                .callbackQueryId(update.getCallbackQuery().getId())
                .build();
        return Collections.singletonList(answer);
    }

    /**
     * Асинхронная отправка сообщения через TelegramClient.
     */
    private void sendMessageAsync(SendMessage message) {
        CompletableFuture.runAsync(() -> {
            try {
                log.debug("Sending async message to chat {}: {}", message.getChatId(), message.getText());
                telegramClient.execute(message);
            } catch (TelegramApiException e) {
                log.error("Failed to send message asynchronously. ChatId: {}, Text: {}",
                        message.getChatId(), message.getText(), e);
            }
        });
    }

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