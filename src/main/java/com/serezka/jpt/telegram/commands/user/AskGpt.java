package com.serezka.jpt.telegram.commands.user;

import com.serezka.jpt.api.GPTApi;
import com.serezka.jpt.api.GPTUtil;
import com.serezka.jpt.database.model.authorization.User;
import com.serezka.jpt.database.service.authorization.UserService;
import com.serezka.jpt.database.service.gpt.QueryService;
import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.types.empty.EmptySession;
import com.serezka.jpt.telegram.sessions.types.step.Step;
import com.serezka.jpt.telegram.sessions.types.step.StepSession;
import com.serezka.jpt.telegram.utils.Send;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AskGpt extends Command<StepSession> {
    GPTApi gptApi;
    UserService userService;
    QueryService queryService;

    public AskGpt(GPTApi gptApi, UserService userService, QueryService queryService) {
        super(List.of("Запрос", "/ask"), "запрос к gpt-4", User.Role.DEFAULT.getAdminLvl());

        this.gptApi = gptApi;
        this.userService = userService;
        this.queryService = queryService;
    }

    @Override
    public StepSession createSession() {
        return new StepSession(List.of(new Step((bot, update) -> new Step.Data("Введите запрос:"))), this);
    }

    @Override
    public void execute(TBot bot, TUpdate update, List<String> history) {
        String query = history.get(1);
        long chatId = update.getChatId();

        try {
            // TODO: 10/13/23
            int msgId = bot.sendMessage(update.getChatId(), "Обработка..").getMessageId();
            String answer = gptApi.query(Collections.singletonList("user: " + query), 0.7);
            log.info("Query: {} | Answer: {}", query, answer);

            if (answer.length() < 4096) {
                bot.execute(Send.message(chatId, answer.replaceAll("<br/>", "\n"), update.getMessageId()));
                bot.deleteMessage(chatId, msgId);
                return;
            }

            String answerHTML = String.format(GPTUtil.TEMPLATE, answer);

            File tempFile = Files.createTempFile("answer", ".html").toFile();
            Files.write(Paths.get(tempFile.getPath()), Arrays.stream(answerHTML.split("\n")).toList(), StandardCharsets.UTF_8);

            InputFile inputFile = new InputFile();
            inputFile.setMedia(tempFile);

            bot.execute(Send.document(update.getChatId(), inputFile, update.getMessageId()));
            bot.deleteMessage(chatId, msgId);
            tempFile.delete();
        } catch (Exception ex) {
            log.warn(ex.getMessage());
        }
    }
}
