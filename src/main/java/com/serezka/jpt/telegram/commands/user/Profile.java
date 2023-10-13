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
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Profile extends Command<MenuSession> {
    ProfilePage profilePage;

    public Profile(ProfilePage profilePage) {
        super(List.of("Профиль"), "просмотр профиля", User.Role.DEFAULT.getAdminLvl());

        this.profilePage = profilePage;
    }

    @Override
    public MenuSession createSession() {
        return new MenuSession(profilePage);
    }

    @Component
    @FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
    public static class ProfilePage extends Page {
        private static final String TEMPLATE = """
                Username: %s
                Queries count: %d
                Info: %s
                """;

        public ProfilePage(UserService userService, QueryService queryService) {

            setGenerator((bot, update, callback, page) -> {
                long chatId = update.getChatId();

                Optional<User> optionalUser = userService.findByChatId(chatId);
                if (optionalUser.isEmpty())
                    return new Data("Error: can't find user", new Button[][]{{new Button(Keyboard.Actions.CLOSE.getName(), Keyboard.Actions.CLOSE.getCallback())}});
                User user = optionalUser.get();

                String info="empty";

                if (callback != null && callback.startsWith("temp/") && callback.matches("temp/[+-]\\d.\\d$")) {
                    double delta = Double.parseDouble(callback.substring("temp/".length()));

                    info = String.format("temperature changed from %.1f to %.1f", user.getTemperature(), user.getTemperature() + delta);
                    user.setTemperature(user.getTemperature() + delta);

                    userService.save(user);
                }

                if (callback != null && callback.startsWith("chat/") && callback.matches("chat/[+-]1$")) {
                    int delta = Integer.parseInt(callback.substring("chat/".length()));

                    info = String.format("chat changed from %d to %d", user.getChat(), user.getChat() + delta);
                    user.setChat(user.getChat() + delta);

                    userService.save(user);
                }

                return new Data(String.format(TEMPLATE, user.getUsername(), queryService.countAllByUserId(user.getId()),info)
                        , new Button[][]{
                        {new Button(String.format("GPT temperature %.1f", user.getTemperature()) , "ignored")},
                        {new Button("+0.1", "temp/+0.1", this), new Button("-0.1", "temp/-0.1",this)},
                        {new Button(" ", "ignored")},
                        {new Button("Chat", "ignored")},
                        {new Button("+1", "chat/+1"), new Button("-1", "chat/-1")}
                });
            });
        }
    }

    @Override
    public void execute(TBot bot, TUpdate update, List<String> history) {

    }
}
