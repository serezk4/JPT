package com.serezka.jpt.telegram.sessions.types;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.*;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public abstract class Session implements Serializable {
    // init data
    final Queue<Integer> messagesId = new PriorityQueue<>();
    final String uuid = UuidCreator.getTimeBased().toString();

    private final List<String> history = new LinkedList<>();

    // generate answer
    public abstract void next(TBot bot, TUpdate update);
}
