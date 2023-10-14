package com.serezka.jpt.telegram.commands.admin;

import com.serezka.jpt.database.model.authorization.User;
import com.serezka.jpt.database.service.authorization.UserService;
import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.types.empty.EmptySession;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
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

        bot.sendMessage(update.getChatId(), "<b>Пользователи:</b>\n"+
                users.stream().map(user -> String.format("\t#%d <b>%s</b>%n", user.getId(), user.getUsername())).collect(Collectors.joining()));
    }
}
