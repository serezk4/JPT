package com.serezka.jpt.telegram.sessions.manager;


import com.serezka.telegrambots.telegram.sessions.types.menu.MenuSession;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.extern.log4j.Log4j2;
import org.glassfish.jersey.internal.guava.Joiner;
import org.hibernate.annotations.ManyToAny;


import java.util.*;

@Log4j2
public class MenuManager implements SessionManager<MenuSession> {
    // ...

    private MenuManager() {}

    private static MenuManager instance = null;

    public static MenuManager getInstance() {
        if (instance == null) instance = new MenuManager();
        return instance;
    }

    // ...

    private final Map<Long, List<MenuSession>> menuSessions = new HashMap<>();

    public void addSession(MenuSession session, long chatId) {
        synchronized (menuSessions) {
            if (!menuSessions.containsKey(chatId))
                menuSessions.put(chatId, new ArrayList<>(Collections.singletonList(session)));
            else menuSessions.get(chatId).add(session);
        }
    }

    public MenuSession getSession(long chatId, String uuid) {
        return menuSessions.get(chatId).stream().filter(currentSession -> currentSession.getUuid().equals(uuid)).findFirst().orElseGet(null);
    }

    public boolean destroySession(long chatId, MenuSession session) {
        if (menuSessions.containsKey(chatId)) return false;
        return menuSessions.get(chatId).remove(session);
    }
}
