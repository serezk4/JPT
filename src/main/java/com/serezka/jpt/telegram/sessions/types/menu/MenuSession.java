package com.serezka.jpt.telegram.sessions.types.menu;


import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.sessions.types.Session;

public class MenuSession extends Session {
    @Override
    public void next(TBot bot, TUpdate update) {
        getMessagesId().add(update.getMessageId());

    }
}
