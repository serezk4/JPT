package com.serezka.jpt;

import com.serezka.jpt.telegram.commands.admin.CreateInvite;
import com.serezka.jpt.telegram.commands.admin.GetQueries;
import com.serezka.jpt.telegram.commands.admin.GetUsers;
import com.serezka.jpt.telegram.commands.user.ClearHistory;
import com.serezka.jpt.telegram.commands.user.Github;
import com.serezka.jpt.telegram.commands.user.HelpMe;
import com.serezka.jpt.telegram.commands.user.Profile;
import com.serezka.jpt.telegram.sessions.types.menu.Page;
import com.serezka.jpt.telegram.utils.Keyboard;
import com.serezka.jpt.telegram.utils.ReadOffice;
import com.serezka.jpt.telegram.utils.Send;
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

import java.io.File;
import java.io.FileInputStream;


@SpringBootApplication
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JptApplication implements ApplicationRunner {
    // bot stuff
    THandler tHandler;
    TBot tBot;

    // commands

    // user
    Profile profile;
    Github github;
    HelpMe helpMe;

    // admin
    GetQueries getQueries;
    GetUsers getUsers;
    CreateInvite createInvite;
    ClearHistory clearHistory;

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

        tHandler.addCommand(getQueries);
        tHandler.addCommand(getUsers);
        tHandler.addCommand(createInvite);

        tBot.setTHandler(tHandler);

        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(tBot);
    }

}
