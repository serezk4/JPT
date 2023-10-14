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
        super(List.of("\uD83D\uDCDC О боте", "/bot"), "get github link", User.Role.DEFAULT.getAdminLvl());
    }

    @Override
    public EmptySession createSession() {
        return new EmptySession(this);
    }

    @Override
    public void execute(TBot bot, TUpdate update, List<String> history) {
        bot.sendMessage(update.getChatId(), """
                <b>Автор: Sergey Dorokhin</b>
                \\_-? @serezkk ?-_/
                
                <b>Полезные ссылочки:</b>
                =| <a href="https://github.com/serezk4/JPT">проект на гитхабе</a>
                =| <a href="https://github.com/serezk4/JPT/issues">отправить проблему</a>
                
                При возникновении багов/проблем - пишите или в тг, или <a href="https://github.com/serezk4/JPT/issues">сюда</a>. Все пофиксим.
                
                   ⠀  ⠀⠀   (\\_/)
                   ⠀⠀(  =(^Y^)=
                ____\\_(m___m)_____________""");
    }
}
