package com.serezka.jpt;

import com.serezka.jpt.telegram.commands.admin.*;
import com.serezka.jpt.telegram.commands.user.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.serezka.jpt.telegram.bot.*;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.time.LocalDateTime;


@SpringBootApplication
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JptApplication implements ApplicationRunner {
    @Getter
    private static final LocalDateTime startTime = LocalDateTime.now();

    // bot stuff
    THandler tHandler;
    TBot tBot;

    // commands

    // user
    Profile profile;
    Github github;
    HelpMe helpMe;
    ClearHistory clearHistory;
    Status status;

    // admin
    GetQueries getQueries;
    GetUsers getUsers;
    CreateInvite createInvite;
    Shutdown shutdown;
    Cast cast;

    // ..

    public static void main(String[] args) {
        SpringApplication.run(JptApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        tHandler.addCommand(profile);
        tHandler.addCommand(github);
        tHandler.addCommand(helpMe);
        tHandler.addCommand(clearHistory);
        tHandler.addCommand(status);

        tHandler.addCommand(getQueries);
        tHandler.addCommand(getUsers);
        tHandler.addCommand(createInvite);
        tHandler.addCommand(shutdown);
        tHandler.addCommand(cast);

        tBot.setTHandler(tHandler);

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(tBot);
    }

}
