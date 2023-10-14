package com.serezka.jpt.telegram.commands.user;

import com.serezka.jpt.database.model.authorization.User;
import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.types.empty.EmptySession;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HelpMe extends Command<EmptySession> {
    public HelpMe() {
        super(List.of("\uD83D\uDCD9 Помогите!"), "базовые особенности бота", User.Role.DEFAULT.getAdminLvl());
    }

    @Override
    public EmptySession createSession() {
        return new EmptySession(this);
    }

    @Override
    public void execute(TBot bot, TUpdate update, List<String> history) {
        bot.sendMessage(update.getChatId(), """
                <b>Бот несет бред?</b> ->
                <code>⚙️ Настройки чата -> \uD83D\uDDD1️ Очистить историю запросов</code>
                
                <b>Бот несет полный бред?</b> ->
                <code>⚙️ Настройки чата -> \uD83D\uDDD1️ Очистить историю запросов</code>
                
                <b>Бот не выдает ответ?</b> ->
                ждать / написать @serezkk / кинуть проблему <a href="https://github.com/serezk4/JPT/issues">сюда</a>
                
                <b>Бота нету?</b> -> грустить.
                
                Бот был запущен недавно и в настоящее время проходит через ряд улучшений для повышения стабильности работы.
                Некоторые функциональные возможности все еще находятся в процессе разработки и будут реализованы в ближайшем будущем.
                """);
    }
}
