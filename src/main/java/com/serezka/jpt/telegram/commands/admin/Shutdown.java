package com.serezka.jpt.telegram.commands.admin;

import com.serezka.jpt.database.model.authorization.User;
import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.types.empty.EmptySession;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component @Log4j2
public class Shutdown extends Command<EmptySession> {
    public Shutdown() {
        super(List.of("/shutdown"), "выключить бота", User.Role.ADMIN1.getAdminLvl());
    }

    @Override
    public EmptySession createSession() {
        return new EmptySession(this);
    }

    @Override
    public void execute(TBot bot, TUpdate update, List<String> history) {
        try {
            bot.shutdown(update);
        } catch (InterruptedException e) {
            log.warn(e.getMessage());
        }
    }
}
