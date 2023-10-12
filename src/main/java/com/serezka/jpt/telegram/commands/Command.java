package com.serezka.jpt.telegram.commands;

import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.sessions.types.Session;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import java.util.List;
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor @Getter @AllArgsConstructor
public abstract class Command<T extends Session> {
    @NonFinal @Setter
    T session; // todo remove

    List<String> names;
    String help;
    int adminLvl; // 0 - user

    public void execute(TBot bot, TUpdate update) {execute(bot,update, null);};
    public abstract void execute(TBot bot, TUpdate update, List<String> history);
    public T createSession() {return null;};
}
