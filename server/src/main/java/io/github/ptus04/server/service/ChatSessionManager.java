package io.github.ptus04.server.service;

import io.github.ptus04.server.dto.SupportSessionDto;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Component
public class ChatSessionManager {
    private final Map<String, SupportSessionDto> activeSessions = new ConcurrentHashMap<>();

    public Collection<SupportSessionDto> getActiveSessions() {
        return activeSessions.values();
    }

    public SupportSessionDto getSession(String sessionId) {
        return activeSessions.get(sessionId);
    }

    public void putSession(String sessionId, SupportSessionDto session) {
        activeSessions.put(sessionId, session);
    }

    public SupportSessionDto computeIfAbsent(String sessionId, Function<String, SupportSessionDto> mappingFunction) {
        return activeSessions.computeIfAbsent(sessionId, mappingFunction);
    }
    
    public void removeSession(String sessionId) {
        activeSessions.remove(sessionId);
    }
}
