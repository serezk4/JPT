package com.serezka.jpt.telegram.sessions.manager;


import com.serezka.jpt.telegram.sessions.types.menu.MenuSession;
import lombok.extern.log4j.Log4j2;

import java.util.*;

@Log4j2
public class MenuManager implements SessionManager<MenuSession> {
    // ...

    private MenuManager() {
    }

    private static MenuManager instance = null;

    public static MenuManager getInstance() {
        if (instance == null) instance = new MenuManager();
        return instance;
    }

    // ...
    // todo make auto-delete after time
    private final Map<Long, List<MenuSession>> menuSessions = new HashMap<>();

    public synchronized boolean containsSession(long chatId) {
        return menuSessions.containsKey(chatId);
    }

    public synchronized void addSession(MenuSession session, long chatId) {
        if (!menuSessions.containsKey(chatId))
            menuSessions.put(chatId, new ArrayList<>(Collections.singletonList(session)));
        else menuSessions.get(chatId).add(session);

    }

    public synchronized Optional<MenuSession> getSession(long chatId, long id) {
            return menuSessions.get(chatId).stream().filter(currentSession -> currentSession.getId() == id).findFirst();
    }

    public synchronized boolean destroySession(long chatId, MenuSession session) {
        if (menuSessions.containsKey(chatId)) return false;
        return menuSessions.get(chatId).remove(session);
    }
}
