package com.serezka.jpt.telegram.commands.user;

import com.serezka.jpt.database.model.authorization.User;
import com.serezka.jpt.database.service.authorization.UserService;
import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.types.empty.EmptySession;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

import java.util.List;
import java.util.Optional;

@Component
@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ClearHistory extends Command<EmptySession> {
    UserService userService;

    public ClearHistory(UserService userService) {
        super(List.of("\uD83D\uDDD1️ Очистить историю", "/clearHistory", "/clh"), "очистить историю чата", User.Role.DEFAULT.getAdminLvl());

        this.userService = userService;
    }

    @Override
    public EmptySession createSession() {
        return new EmptySession(this);
    }

    @Override
    public void execute(TBot bot, TUpdate update, List<String> history) {
        long chatId = update.getChatId();
        int messageId = update.getMessageId();

        Optional<User> optionalUser = userService.findByChatId(chatId);
        if (optionalUser.isEmpty()) {
            bot.execute(SendMessage.builder()
                    .chatId(chatId).text("\uD83D\uDD0D <b>Пользователь не найден.</b>")
                    .parseMode(ParseMode.HTML)
                    .build());
            log.info("user with {} didn't founded", chatId);
            return;
        }

        User user = optionalUser.get();
        user.setChat(user.getChat() + 1);
        userService.save(user);
        bot.execute(SendMessage.builder()
                .chatId(chatId).text("ℹ️ *История чата очищена.*")
                .parseMode(ParseMode.MARKDOWN)
                .build());

        bot.execute(DeleteMessage.builder()
                .chatId(chatId).messageId(messageId)
                .build());
    }
}
