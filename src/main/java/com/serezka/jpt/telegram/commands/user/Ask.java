package com.serezka.jpt.telegram.commands.user;

import com.serezka.jpt.api.GPTApi;
import com.serezka.jpt.database.model.authorization.User;
import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.types.step.Step;
import com.serezka.jpt.telegram.sessions.types.step.StepSession;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class Ask extends Command<StepSession> {
    GPTApi gptApi;

    public Ask(GPTApi gptApi) {
        super(List.of("Запрос", "/ask"), "сделать запрос", User.Role.DEFAULT.getAdminLvl());
        this.gptApi = gptApi;
    }

    @Override
    public StepSession createSession() {
        return new StepSession(List.of(new Step((bot, update) -> new Step.Data("Введите запрос: "))), this);
    }

    @Override
    public void execute(TBot bot, TUpdate update, List<String> history) {
        try {
            String query = history.get(1);
            bot.sendMessage(update.getChatId(), "Обработка..");
            String answer = gptApi.query(Collections.singletonList("user: " +query), 0.8);
            bot.sendMessage(update.getChatId(), answer);
        } catch (Exception ex) {log.warn(ex.getMessage());}
    }
}
