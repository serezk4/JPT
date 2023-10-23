package com.serezka.jpt.telegram.commands.user;

import com.serezka.jpt.database.model.Subscription;
import com.serezka.jpt.database.model.User;
import com.serezka.jpt.database.service.SubscriptionService;
import com.serezka.jpt.database.service.UserService;
import com.serezka.jpt.database.service.QueryService;
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
import java.util.Random;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Profile extends Command<MenuSession> {
    ProfilePage profilePage;

    public Profile(ProfilePage profilePage) {
        super(List.of("\uD83C\uDD94 Профиль"), "настройки чата", User.Role.DEFAULT.getAdminLvl());

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
                <b>Подписка</b>: <i>%s</i>
                <b>Осталось запросов</b>: <i>%s</i>
                <b>Кол-во запросов:</b> <i>%d</i>
                %s
                """;

        private static final List<String> exitWords = List.of("Закрыть менюшку", "Уйди!", "Закрыть окно");

        public ProfilePage(UserService userService, QueryService queryService, SubscriptionService subscriptionService) {

            setGenerator((bot, update, callback, page) -> {
                long chatId = update.getChatId();

                Optional<User> optionalUser = userService.findByChatId(chatId);
                if (optionalUser.isEmpty())
                    return new Data("Error: can't find user", new Button[][]{{new Button(Keyboard.Actions.CLOSE.getName(), Keyboard.Actions.CLOSE.getCallback())}});
                User user = optionalUser.get();

                Optional<Subscription> optionalSubscription = subscriptionService.findById(user.getSubscriptionId());
                if (optionalSubscription.isEmpty())
                    return new Data("Error: can't optionalSubscription", new Button[][]{{new Button(Keyboard.Actions.CLOSE.getName(), Keyboard.Actions.CLOSE.getCallback())}});
                Subscription subscription = optionalSubscription.get();

                String incall = "";
                if (callback != null && callback.startsWith("temp/") && callback.matches("temp/[+-]\\d.\\d$")) {
                    float delta = Float.parseFloat(callback.substring("temp/".length()));
                    final float oldTemp = user.getTemperature(), newTemp = oldTemp + delta;
                    user.setTemperature(newTemp);
                    incall = String.format("<code>Update:</code> <b>Temp changed: %.1f -> %.1f </b>", oldTemp, newTemp);
                    userService.save(user);
                }

                if (callback != null && callback.startsWith("remove_chat_history")) {
                    user.setChat(user.getChat() + 1);
                    incall = "<code>Update:</code> <b>История чата очищенна</b>";
                    userService.save(user);
                }

                return new Data(String.format(TEMPLATE,
                        user.getUsername(), subscription.getName(), subscription.getUsagesCount(), queryService.countAllByUserId(user.getId()), incall)
                        , new Button[][]{
                        {
                                new Button("⬇️ -0.1", "temp/-0.1", this),
                                new Button(String.format("\uD83C\uDF21️ Temp: %.1f", user.getTemperature()), "ignored"),
                                new Button("⬆️ +0.1", "temp/+0.1", this)
                        },
                        {
                                new Button(exitWords.get(new Random().nextInt(exitWords.size())), Keyboard.Actions.CLOSE.getCallback())
                        }
                });
            });
        }
    }

    @Override
    public void execute(TBot bot, TUpdate update, List<String> history) {

    }
}
