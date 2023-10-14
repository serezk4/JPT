package com.serezka.jpt.telegram.commands.user;

import com.serezka.jpt.database.model.authorization.User;
import com.serezka.jpt.database.service.authorization.UserService;
import com.serezka.jpt.database.service.gpt.QueryService;
import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.types.menu.MenuSession;
import com.serezka.jpt.telegram.sessions.types.menu.Page;
import com.serezka.jpt.telegram.utils.Keyboard;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Profile extends Command<MenuSession> {
    ProfilePage profilePage;

    public Profile(ProfilePage profilePage) {
        super(List.of("\uD83D\uDCD1 Настройки чата"), "настройки чата", User.Role.DEFAULT.getAdminLvl());

        this.profilePage = profilePage;
    }

    @Override
    public MenuSession createSession() {
        return new MenuSession(profilePage);
    }

    @Component
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class ProfilePage extends Page {
        private static final String TEMPLATE = """
                <b>Имя:</b> <i>%s</i>
                <b>Кол-во запросов:</b> <i>%d</i>
                %s
                ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
                """;

        public ProfilePage(UserService userService, QueryService queryService) {

            setGenerator((bot, update, callback, page) -> {
                long chatId = update.getChatId();

                Optional<User> optionalUser = userService.findByChatId(chatId);
                if (optionalUser.isEmpty())
                    return new Data("Error: can't find user", new Button[][]{{new Button(Keyboard.Actions.CLOSE.getName(), Keyboard.Actions.CLOSE.getCallback())}});
                User user = optionalUser.get();

                String incall = "";
                if (callback != null && callback.startsWith("temp/") && callback.matches("temp/[+-]\\d.\\d$")) {
                    double delta = Double.parseDouble(callback.substring("temp/".length()));
                    user.setTemperature(user.getTemperature() + delta);
                    userService.save(user);
                }

                if (callback != null && callback.startsWith("remove_chat_history")) {
                    user.setChat(user.getChat() + 1);
                    incall = "<code>Update:</code> <b>История чата очищенна</b>";
                    userService.save(user);
                }

                return new Data(String.format(TEMPLATE, user.getUsername(), queryService.countAllByUserId(user.getId()), incall)
                        , new Button[][]{
                        {
                                new Button("⬇️ -0.1", "temp/-0.1", this),
                                new Button(String.format("\uD83C\uDF21️ Temp: %.1f", user.getTemperature()), "ignored"),
                                new Button("⬆️ +0.1", "temp/+0.1", this)
                        },
                        {
                                new Button("\uD83D\uDCAC Очистить историю чата", "remove_chat_history")
                        }
                });
            });
        }
    }

    @Override
    public void execute(TBot bot, TUpdate update, List<String> history) {

    }
}
