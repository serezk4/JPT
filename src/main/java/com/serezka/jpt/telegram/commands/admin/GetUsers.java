package com.serezka.jpt.telegram.commands.admin;

import com.serezka.jpt.database.model.User;
import com.serezka.jpt.database.service.UserService;
import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.types.empty.EmptySession;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.stream.Collectors;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GetUsers extends Command<EmptySession> {
    UserService userService;

    public GetUsers(UserService userService) {
        super(List.of("/users"), "получить список пользвателей", User.Role.ADMIN1.getAdminLvl());

        this.userService = userService;
    }

    @Override
    public EmptySession createSession() {
        return new EmptySession(this);
    }

    @Override
    public void execute(TBot bot, TUpdate update, List<String> history) {
        List<User> users = userService.findAll();

        bot.execute(SendMessage.builder()
                .chatId(update.getChatId())
                .text("<b>Пользователи:</b>\n" + users.stream().map(user -> String.format("\t#%d <b>%s</b>%n", user.getId(), user.getUsername())).collect(Collectors.joining()))
                .parseMode(ParseMode.HTML)
                .build());
    }
}
