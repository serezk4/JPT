package com.serezka.jpt.telegram.commands.admin;

import com.serezka.jpt.database.model.authorization.User;
import com.serezka.jpt.database.model.gpt.Query;
import com.serezka.jpt.database.service.gpt.QueryService;
import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.types.step.Step;
import com.serezka.jpt.telegram.sessions.types.step.StepSession;
import com.serezka.jpt.telegram.utils.Keyboard;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class GetQueries extends Command<StepSession> {
    QueryService queryService;

    public GetQueries(QueryService queryService) {
        super(List.of("/queries"), "получить запросы пользователя", User.Role.ADMIN1.getAdminLvl());

        this.queryService = queryService;
    }

    @Override
    public StepSession createSession() {
        return new StepSession(List.of(new Step((bot, update) -> new Step.Data("\uD83C\uDD94 Введите <b>ID пользователя</b>"))), this);
    }

    @Override
    public void execute(TBot bot, TUpdate update, List<String> history) {
        if (!history.get(1).matches("\\d+")) {
            bot.execute(SendMessage.builder()
                    .chatId(update.getChatId()).text("Введите числовое значение!")
                    .build());
            return;
        }

        long selectedUserID = Long.parseLong(history.get(1));
        List<Query> queries = queryService.findAllByUserId(selectedUserID);

        if (queries.isEmpty()) {
            bot.execute(SendMessage.builder()
                    .chatId(update.getChatId()).text("Запросов от пользователя не найдено!\nУбедитесь в правильности ввода.")
                    .build());
            return;
        }

        try {
            bot.execute(SendDocument.builder()
                    .chatId(update.getChatId())
                    .document(new InputFile(new ByteArrayInputStream(queries.stream().map(query -> String.format("Query: %s \nAnswer: %s\n-------------------------------------------\n",
                            query.getQuery(), query.getAnswer())).collect(Collectors.joining()).getBytes()),
                            "queries" + selectedUserID + ".txt"))
                    .caption("<b>Запросы пользователя</b>")
                    .replyMarkup(Keyboard.Reply.DEFAULT)
                    .parseMode(ParseMode.HTML)
                    .build());
        } catch (TelegramApiException e) {
            log.warn(e.getMessage());
        }
    }
}
