package com.serezka.jpt.telegram.commands.admin;

import com.serezka.jpt.database.model.User;
import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.types.step.Step;
import com.serezka.jpt.telegram.sessions.types.step.StepSession;

import java.util.List;

public class UpdateSubscriptions extends Command<StepSession> {
    public UpdateSubscriptions() {
        super(List.of("/updates"), "обновить подписки", User.Role.ADMIN1.getAdminLvl());
    }

    @Override
    public StepSession createSession() {
        return new StepSession(
                List.of(new Step((bot, update) -> new Step.Data("вы уверены?")))
                ,this);
    }

    @Override
    public void execute(TBot bot, TUpdate update, List<String> history) {

    }
}
