package io.telegram.bot.application.service;

import io.telegram.bot.application.processor.UpdateProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateHandler {

    private final List<UpdateProcessor> processors;

    /**
     * Обрабатывает обновление, передавая его первому подходящему процессору.
     * Если ни один процессор не подошёл, возвращает пустой список.
     */
    public List<BotApiMethod<?>> handleUpdate(Update update) {
        for (UpdateProcessor processor : processors) {
            if (processor.canProcess(update)) {
                log.debug("Обработка обновления с помощью процессора: {}", processor.getClass().getSimpleName());
                return processor.process(update);
            }
        }
        log.warn("Процессор для обновления не найден.: {}", update);
        return Collections.emptyList();
    }
}