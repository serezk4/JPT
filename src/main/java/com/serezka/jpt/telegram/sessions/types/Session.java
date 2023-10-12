package com.serezka.jpt.telegram.sessions.types;

import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public abstract class Session implements Serializable {
    private static int idCounter = 0;

    // init data
    final Queue<Integer> botsMessagesIds = new PriorityQueue<>();
    final List<Integer> usersMessagesIds = new ArrayList<>();
    final long id = idCounter++;
    @Getter @Setter boolean saveUsersMessages = true;

    private final List<String> history = new LinkedList<>();
    private boolean created;

    // generate answer
    public abstract void next(TBot bot, TUpdate update);
    public abstract void destroy(TBot bot, TUpdate update);
}
