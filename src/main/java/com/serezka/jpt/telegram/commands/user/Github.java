package com.serezka.jpt.telegram.commands.user;

import com.serezka.jpt.database.model.authorization.User;
import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.types.empty.EmptySession;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Github extends Command<EmptySession> {
    public Github() {
        super(List.of("\uD83D\uDCDC Github"), "get github link", User.Role.DEFAULT.getAdminLvl());
    }

    @Override
    public EmptySession createSession() {
        return new EmptySession(this);
    }

    @Override
    public void execute(TBot bot, TUpdate update, List<String> history) {
        bot.sendMessage(update.getChatId(), "<b>Author: Sergey Dorokhin</b>\n<code>code:</code> https://github.com/serezk4/JPT\n<code>issues:</code> https://github.com/serezk4/JPT/issues");
    }
}
