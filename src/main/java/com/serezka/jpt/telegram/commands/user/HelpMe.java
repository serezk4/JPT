package com.serezka.jpt.telegram.commands.user;

import com.serezka.jpt.database.model.authorization.User;
import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.types.empty.EmptySession;
import com.serezka.jpt.telegram.utils.methods.v2.Parse;
import com.serezka.jpt.telegram.utils.methods.v2.Send;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Component
public class HelpMe extends Command<EmptySession> {
    public HelpMe() {
        super(List.of("◻️ Помогите!"), "базовые особенности бота", User.Role.DEFAULT.getAdminLvl());
    }

    @Override
    public EmptySession createSession() {
        return new EmptySession(this);
    }

    @Override
    public void execute(TBot bot, TUpdate update, List<String> history) {

        bot.execute(SendMessage.builder()
                .chatId(update.getChatId()).text( """
                <b>Самые частые проблемы:</b>
                
                ⁉️ <b>Странные ответы / Ответы на китайском?</b>
                 | Нажмите <code>\uD83D\uDDD1️ Очистить историю запросов</code>
                
                ℹ️ Если закончили с вопросами и хотите переключиться на другую тему - всегда очищайте <b>историю запросов</b>
                
                ⁉️ <b>Бот не выдает ответ? Хотите предложить улучшение?</b>
                 | Напишите <b>@serezkk.</b>
                 
                 <i>Так же можно включить автоудаление сообщений в этом чате посредством самого telegram.</i>
                """)
                .parseMode(ParseMode.HTML).build());

        bot.deleteMessage(update.getChatId(), update.getMessageId());

    }
}
