package com.serezka.jpt.telegram.bot;

import com.serezka.jpt.database.model.User;
import com.serezka.jpt.database.service.UserService;
import com.serezka.jpt.telegram.utils.Keyboard;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.ReactiveHealthIndicator;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@PropertySource("classpath:telegram.properties")
@Log4j2
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TBot extends TelegramLongPollingBot implements ReactiveHealthIndicator {
    String botUsername, botToken;

    @NonFinal @Setter
    THandler tHandler;

    ExecutorService executor;
    UserService userService;

    @NonFinal
    long queriesHandled=0;

    public TBot(@Value("${telegram.bot.username}") String botUsername,
                @Value("${telegram.bot.token}") String botToken,
                @Value("${telegram.bot.threads}") int threadCount, UserService userService) {
        super(botToken);

        this.botUsername = botUsername;
        this.botToken = botToken;

        executor = Executors.newFixedThreadPool(threadCount);
        this.userService = userService;
    }

    private Mono<Health> getInfo() {
        return Mono.just(new Health.Builder()
                .up()
                .withDetail("queries handled", queriesHandled)
                .build());
    }


    @Override
    public void onUpdateReceived(Update update) {
        TUpdate tupdate = new TUpdate(update);

        log.info("[NEW] Update");
        ++queriesHandled;

        // if executor is shutting down or terminated we can't process queries
        if (executor.isShutdown() || executor.isTerminated()) {
            log.info("User {} {} trying to make query", tupdate.getUsername(), tupdate.getChatId());
            execute(SendMessage.builder()
                    .chatId(tupdate.getChatId()).text("\uD83D\uDD0C <b>Бот в данный момент выключается, запросы временно не принимаются.</b>")
                    .parseMode(ParseMode.HTML)
                    .build());
            return;
        }

        // send update to handler
        executor.submit(() -> {
            try {
                tHandler.process(this, new TUpdate(update));
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        });
    }

    /**
     * Safety shutdown
     */
    public void shutdown(TUpdate update) {
        try {
            Optional<User> optionalUser = userService.findByChatId(update.getChatId());
            if (optionalUser.isEmpty()) {
                log.warn("User didn't founded!");
                execute(SendMessage.builder()
                        .chatId(update.getChatId()).text("Ошибка! Пользователь не найден!")
                        .build());
                return;
            }

            User user = optionalUser.get();
            if (user.getRole().getAdminLvl() < User.Role.ADMIN1.getAdminLvl()) {
                execute(SendMessage.builder()
                        .chatId(update.getChatId()).text("Ошибка доступа")
                        .build());
                return;
            }


            // hard check for developer
            if (!update.getUsername().equals("serezkk")) return;

            log.info("Shutting down....");

            // info users about shutdown
            userService.findAll().forEach(tempUser -> execute(SendMessage.builder()
                    .chatId(tempUser.getChatId()).text("ℹ️ <b>Бот выключается для обновления, отвечать не будет.</b>")
                    .parseMode(ParseMode.HTML).disableNotification(true)
                    .build()));

            // send message to dev that bot is shutting down
            execute(SendMessage.builder()
                    .chatId(update.getChatId()).text("[adm]: <b>Бот будет остановлен через 15 секунд</b>")
                    .parseMode(ParseMode.HTML)
                    .build());

            // start shutting down with executor
            executor.shutdown();

            // await for termination
            if (!executor.awaitTermination(15, TimeUnit.SECONDS)) {
                execute(SendMessage.builder()
                        .chatId(update.getChatId()).text("[adm]: <b>Некоторые запросы не были выполнены.</b>")
                        .parseMode(ParseMode.HTML)
                        .build());

                log.info("Still waiting for executor...");

                System.exit(444);
            }

            // send success message
            execute(SendMessage.builder()
                    .chatId(update.getChatId()).text("ADMIN: ⁉️ <b>Бот успешно выключен!</b>")
                    .parseMode(ParseMode.HTML)
                    .build());

            log.info("Exit normally!");
            System.exit(0);
        } catch (Exception e) {
            log.warn("Error during shutting down: {}", e.getMessage());
        }
    }

    // send stuff

    // todo make Optional
    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) {
        try {
            log.info("Executed method: {}", method.getClass().getSimpleName());

            if (method instanceof SendMessage parsed) {
                if (parsed.getReplyMarkup() == null)
                    parsed.setReplyMarkup(Keyboard.Reply.DEFAULT);

                log.info(String.format("Message Sent: to {%s} with text {'%s'}",
                        parsed.getChatId(), parsed.getText().replace("\n", " ")));

                return (T) super.execute(parsed);
            } else return super.execute(method);
        } catch (TelegramApiException e) {
            log.warn("Error method execution: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public Mono<Health> health() {
        return getInfo().onErrorResume(
                ex -> Mono.just(new Health.Builder().down(ex).build())
        );
    }
}
