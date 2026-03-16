package io.telegram.bot.infrastructure.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@RestController
public class TestSendController {

    private final TelegramClient telegramClient;

    public TestSendController(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @GetMapping("/test-send")
    public String testSend(@RequestParam long chatId, @RequestParam String text) {
        try {
            SendMessage message = SendMessage.builder()
                    .chatId(chatId)
                    .text(text)
                    .build();
            telegramClient.execute(message);
            return "✅ OK: сообщение отправлено";
        } catch (TelegramApiException e) {
            return "❌ Ошибка: " + e.getMessage();
        }
    }
}