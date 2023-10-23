package com.serezka.jpt.telegram.commands.user;

import com.serezka.jpt.database.model.User;
import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.types.empty.EmptySession;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

import java.util.List;

@Component
public class Github extends Command<EmptySession> {
    public Github() {
        super(List.of("ℹ️ О боте", "/bot"), "get github link", User.Role.DEFAULT.getAdminLvl());
    }

    @Override
    public EmptySession createSession() {
        return new EmptySession(this);
    }

    @Override
    public void execute(TBot bot, TUpdate update, List<String> history) {
        bot.execute(SendMessage.builder()
                .chatId(update.getChatId()).text("""
                <b>Автор: Sergey Dorokhin</b>
                \\_-? @serezkk ?-_/
                                
                bot:  <b>v0.2</b>
                core: <b>v1.3</b>
                                
                ⁉️ <b>При возникновении багов/проблем - пишите @serezkk.</b> Все пофиксим.
                                
                🆕 Из последних обновлений:
                =| небольшие фиксы, доработки
                                
                <b>   ⠀  ⠀   (\\_/)</b>
                <b>   ⠀(  =(^Y^)=</b>
                <b>____\\_(m___m)_____________</b>""")
                .parseMode(ParseMode.HTML).build());

        bot.execute(DeleteMessage.builder()
                .chatId(update.getChatId()).messageId(update.getMessageId())
                .build());
    }
}
