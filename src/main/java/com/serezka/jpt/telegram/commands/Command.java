package com.serezka.jpt.telegram.commands;

import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.sessions.types.Session;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.List;
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter @AllArgsConstructor
public abstract class Command<T extends Session> {
    List<String> names;
    String help;
    int adminLvl; // 0 - user

    public void execute(TBot bot, TUpdate update) {execute(bot,update, null);}
    public abstract void execute(TBot bot, TUpdate update, List<String> history);
    public abstract T createSession();
}
