package io.telegram.bot.application.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryUserService {
    private final Map<Long, String> states = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, Object>> contexts = new ConcurrentHashMap<>();

    public String getConversationState(Long chatId) { return states.get(chatId); }
    public void setConversationState(Long chatId, String state) {
        if (state == null) states.remove(chatId); else states.put(chatId, state);
    }
    public Map<String, Object> getDialogContext(Long chatId) {
        return contexts.computeIfAbsent(chatId, k -> new HashMap<>());
    }
    public void setDialogContext(Long chatId, Map<String, Object> context) {
        contexts.put(chatId, context);
    }
    public void resetDialog(Long chatId) {
        states.remove(chatId);
        contexts.remove(chatId);
    }
}