package com.serezka.jpt.telegram.commands.user;

import com.serezka.jpt.database.model.authorization.User;
import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.types.empty.EmptySession;
import com.serezka.jpt.telegram.utils.methods.v2.Send;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

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
                                
                bot:  <b>v0.1.2</b>
                core: <b>v1.2E</b>
                                
                ⁉️ <b>При возникновении багов/проблем - пишите @serezkk.</b> Все пофиксим.
                                
                🆕 Из последних обновлений:
                =| Добавлена <b>поддержка обработки файлов</b> <b>.xls</b>, <b>.docx</b>, <b>.txt</b>
                <i>скоро появится поддержка всех файлов, которые могут содержать текст</i>
                =| Обновлен интерфейс 
                =| Кот снизу теперь жирным шрифтом
                                
                <b>   ⠀  ⠀   (\\_/)</b>
                <b>   ⠀(  =(^Y^)=</b>
                <b>____\\_(m___m)_____________</b>""")
                .parseMode(ParseMode.HTML).build());

        bot.deleteMessage(update.getChatId(), update.getMessageId());
    }
}
