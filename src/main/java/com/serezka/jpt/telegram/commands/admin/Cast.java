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
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class Cast extends Command<StepSession> {
    UserService userService;

    public Cast(UserService userService) {
        super(List.of("/cast"), "сделать объявление", User.Role.ADMIN1.getAdminLvl());

        this.userService = userService;
    }

    @Override
    public StepSession createSession() {
        return new StepSession(List.of(
                new Step((bot, update) -> new Step.Data("\uD83E\uDD14 Вы уверены? <code>Да/Нет</code>")),
                new Step((bot, update) -> new Step.Data("\uD83D\uDCCC Закрепить? <code>Да/Нет</code>")),
                new Step((bot, update) -> new Step.Data("\uD83D\uDCDD Введите объявление:"))
        ), this);
    }

    @Override
    public void execute(TBot bot, TUpdate update, List<String> history) {
        if (history.size() != 4) {
            log.warn("Error: History len != 3 {}", history);
            return;
        }

        if (!history.get(3).equalsIgnoreCase("да")) {
            bot.execute(SendMessage.builder()
                    .chatId(update.getChatId()).text("❎ *Отменено*")
                            .parseMode(ParseMode.MARKDOWN)
                    .build());
            return;
        }

        // todo delay (when < 100 users - ignore -_-)
        userService.findAll().forEach(user -> {
            final int messageId = bot.execute(SendMessage.builder()
                    .chatId(user.getChatId()).text(history.get(1))
                    .disableNotification(true).parseMode(ParseMode.MARKDOWN)
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
