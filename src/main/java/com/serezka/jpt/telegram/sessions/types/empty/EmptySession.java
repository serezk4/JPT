package com.serezka.jpt.telegram.sessions.types.empty;

import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.types.Session;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RequiredArgsConstructor @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmptySession extends Session {
    Command<EmptySession> command;

    @Override
    public void next(TBot bot, TUpdate update) {
        // just execute command...
        command.execute(bot,update);
    }

    @Override
    public void destroy(TBot bot, TUpdate update) {
        // empty
    }
}
