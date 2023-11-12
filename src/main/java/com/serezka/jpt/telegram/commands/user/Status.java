package com.serezka.jpt.telegram.commands.user;

import com.serezka.jpt.JptApplication;
import com.serezka.jpt.api.GPTApi;
import com.serezka.jpt.api.GPTUtil;
import com.serezka.jpt.database.model.User;
import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.types.empty.EmptySession;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Status extends Command<EmptySession> {
    GPTUtil gptUtil;

    public Status(GPTUtil gptUtil) {
        super(List.of("/status"), "получить статус бота", User.Role.DEFAULT.getAdminLvl());

        this.gptUtil = gptUtil;
    }

    private static final String pattern = """
            Бот:
             | Аптайм: %.1f дней
                        
            GPT
             | Ответ: %s
            """;

    @SneakyThrows
    @Override
    public void execute(TBot bot, TUpdate update, List<String> history) {
        double range = ChronoUnit.DAYS.between(JptApplication.getStartTime(), LocalDateTime.now());

        Message message = bot.execute(SendMessage.builder()
                .chatId(update.getChatId()).text(String.format(pattern, range, "ожидание ответа"))
                .build());

        try {
            bot.execute(EditMessageText.builder()
                    .messageId(message.getMessageId()).chatId(message.getChatId()).text(String.format(pattern, range, gptUtil.completeQuery(update.getChatId(), "ты работаешь?", GPTUtil.Formatting.TEXT)))
                    .build());
        } catch (Exception ex) {
            // todo ебучий телеграм не редачит сообщения хуйло
            bot.execute(EditMessageText.builder()
                    .messageId(message.getMessageId()).chatId(message.getChatId())
                    .text(String.format(pattern, range, ex.getMessage()))
                    .build());
        }
    }

    @Override
    public EmptySession createSession() {
        return new EmptySession(this);
    }
}
