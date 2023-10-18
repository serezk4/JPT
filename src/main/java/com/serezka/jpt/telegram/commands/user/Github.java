package com.serezka.jpt.telegram.commands.user;

import com.serezka.jpt.database.model.authorization.User;
import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.types.empty.EmptySession;
import com.serezka.jpt.telegram.utils.messages.SendV2;
import org.springframework.stereotype.Component;

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
//        bot.sendMessage(update.getChatId(), """
//                <b>Автор: Sergey Dorokhin</b>
//                \\_-? @serezkk ?-_/
//
//                bot:  <b>v0.1.2</b>
//                core: <b>v1.2</b>
//
//                <b>Полезные ссылочки:</b>
//                =| <a href="https://github.com/serezk4/JPT">проект на гитхабе</a>
//                =| <a href="https://github.com/serezk4/JPT/issues">отправить проблему</a>
//
//                При возникновении багов/проблем - пишите или в тг, или <a href="https://github.com/serezk4/JPT/issues">сюда</a>. Все пофиксим.
//
//                Из последних обновлений:
//                =| Добавлена <b>поддержка обработки файлов</b> <b>.xls</b>, <b>.docx</b>, <b>.txt</b>
//                <i>скоро появится поддержка всех файлов, которые могут содержать текст</i>
//                =| Обновлен интерфейс
//                =| Кот снизу теперь жирным шрифтом
//
//                <b>   ⠀  ⠀   (\\_/)</b>
//                <b>   ⠀(  =(^Y^)=</b>
//                <b>____\\_(m___m)_____________</b>""", SendV2.Parse.HTML);

        bot.sendMessage(update.getChatId(), """
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
                <b>____\\_(m___m)_____________</b>""", SendV2.Parse.HTML);
    }
}
