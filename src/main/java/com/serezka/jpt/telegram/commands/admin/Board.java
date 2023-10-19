package com.serezka.jpt.telegram.commands.admin;

import com.serezka.jpt.database.model.authorization.User;
import com.serezka.jpt.database.service.authorization.UserService;
import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.types.step.Step;
import com.serezka.jpt.telegram.sessions.types.step.StepSession;
import com.serezka.jpt.telegram.utils.methods.v2.Send;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;

import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class Board extends Command<StepSession> {
    UserService userService;

    public Board(UserService userService) {
        super(List.of("/board"), "сделать объявление", User.Role.ADMIN1.getAdminLvl());

        this.userService = userService;
    }

    @Override
    public StepSession createSession() {
        return new StepSession(List.of(
                new Step((bot, update) -> new Step.Data("Вы уверены? Да/Нет")),
                new Step((bot, update) -> new Step.Data("Закрепить? Да/Нет")),
                new Step((bot, update) -> new Step.Data("Введите объявление:"))
        ), this);
    }

    @Override
    public void execute(TBot bot, TUpdate update, List<String> history) {
        if (history.size() != 4) {
            log.warn("Error: History len != 3 {}", history);
            return;
        }

        if (!history.get(3).equalsIgnoreCase("да")) {
            bot.sendMessage(Send.Message.builder()
                    .chatId(update.getChatId()).text("Отменено")
                    .build());
            return;
        }

        // todo delay (when < 100 users - ignore -_-)
        userService.findAll().forEach(user -> {
            final int messageId = bot.sendMessage(Send.Message.builder()
                    .chatId(user.getChatId()).text(history.get(1))
                    .build()).getMessageId();

            if (history.get(2).equalsIgnoreCase("да")) {
                PinChatMessage pin = new PinChatMessage();
                pin.setChatId(user.getChatId());
                pin.setMessageId(messageId);
                pin.setDisableNotification(true);

                bot.execute(pin);
            }
        });
    }
}
