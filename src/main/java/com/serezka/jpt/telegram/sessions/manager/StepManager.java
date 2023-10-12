package com.serezka.jpt.telegram.sessions.manager;

import com.serezka.jpt.telegram.sessions.types.step.StepSession;
import lombok.extern.log4j.Log4j2;

import java.util.*;

@Log4j2
public class StepManager implements SessionManager<StepSession> {
    // ...

    private StepManager() {}
    private static StepManager instance = null;

    public static StepManager getInstance() {
        if (instance == null) instance = new StepManager();
        return instance;
    }

    // ...

    private final Map<Long, Stack<StepSession>> stepSessions = new HashMap<>();

    public synchronized boolean containsSession(long chatId) {
        return stepSessions.containsKey(chatId) && !stepSessions.get(chatId).isEmpty();
    }

    public synchronized void addSession(StepSession session, long chatId) {
        if (!stepSessions.containsKey(chatId)) stepSessions.put(chatId, new Stack<>());
        stepSessions.get(chatId).add(session);
    }

    public synchronized StepSession getSession(long chatId) {
        if (!stepSessions.containsKey(chatId) || stepSessions.get(chatId).isEmpty()) return null;
        return stepSessions.get(chatId).peek();
    }

    public synchronized StepSession destroySession(long chatId) {
        if (!stepSessions.containsKey(chatId)) return null;
        return stepSessions.get(chatId).pop();
    }
}
