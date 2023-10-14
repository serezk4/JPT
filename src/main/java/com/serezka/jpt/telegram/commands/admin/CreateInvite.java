package com.serezka.jpt.telegram.commands.admin;

import com.github.f4b6a3.uuid.UuidCreator;
import com.serezka.jpt.database.model.authorization.InviteCode;
import com.serezka.jpt.database.model.authorization.User;
import com.serezka.jpt.database.service.authorization.InviteCodeService;
import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.types.step.Step;
import com.serezka.jpt.telegram.sessions.types.step.StepSession;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreateInvite extends Command<StepSession> {
    InviteCodeService inviteCodeService;

    public CreateInvite(InviteCodeService inviteCodeService) {
        super(List.of("/invite"), "создать инвайт", User.Role.ADMIN1.getAdminLvl());

        this.inviteCodeService = inviteCodeService;
    }

    @Override
    public StepSession createSession() {
        return new StepSession(List.of(
                new Step((bot, update) -> new Step.Data("Укажите кол-во использований: ", new Step.Button.Reply[][]{
                        {new Step.Button.Reply("5"), new Step.Button.Reply("10"), new Step.Button.Reply("15")}
                })),
                new Step((bot, update) -> new Step.Data("Введите код:\n<i>выберите предложенный или введите свой...</i>", new Step.Button.Reply[][]{
                        {new Step.Button.Reply(UuidCreator.getRandomBased().toString())}
                }))
        ), this);
    }

    @Override
    public void execute(TBot bot, TUpdate update, List<String> history) {
        if (history.get(1).isEmpty() || !history.get(2).matches("\\d+")) {
            bot.sendMessage(update.getChatId(), "ошибка ввода");
            return;
        }

        InviteCode newCode = inviteCodeService.save(new InviteCode(history.get(1), Integer.parseInt(history.get(2))));
        bot.sendMessage(update.getChatId(), String.format("Добавлен новый <b>инвайт-код</b>:%n<code>#%d</code> <code>%s</code> - <code>%d использований</code>",
                newCode.getId(), newCode.getCode(), newCode.getUsageCount()));
    }
}
