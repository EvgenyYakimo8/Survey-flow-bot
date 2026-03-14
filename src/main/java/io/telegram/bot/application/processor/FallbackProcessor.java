package io.telegram.bot.application.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@Component
@Order(Integer.MAX_VALUE) // самый низкий приоритет
public class FallbackProcessor implements UpdateProcessor {

    @Override
    public boolean canProcess(Update update) {
        return true; // обрабатывает всё, что не обработали другие
    }

    @Override
    public List<BotApiMethod<?>> process(Update update) {
        log.debug("Необработанный тип обновления: {}", update);
        return List.of(); // ничего не отправляем
    }
}