package com.serezka.jpt;

import com.serezka.jpt.telegram.commands.user.Github;
import com.serezka.jpt.telegram.commands.user.Profile;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.serezka.jpt.telegram.bot.*;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


@SpringBootApplication
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JptApplication implements ApplicationRunner {
    // bot stuff
    THandler tHandler;
    TBot tBot;

    // commands
    Profile profile;
    Github github;
    // ..

    public static void main(String[] args) {
        SpringApplication.run(JptApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        tHandler.addCommand(profile);
        tHandler.addCommand(github);

        tBot.setTHandler(tHandler);

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(tBot);
    }

}
