package com.serezka.jpt.telegram.commands.user;

import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.types.empty.EmptySession;

import java.util.List;

public class AskGPTFast extends Command<EmptySession> {
    public AskGPTFast() {
        super(null, "", Integer.MAX_VALUE);
    }

    @Override
    public EmptySession createSession() {
        return new EmptySession(this);
    }

    @Override
    public void execute(TBot bot, TUpdate update, List<String> history) {

    }
}
