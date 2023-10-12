package com.serezka.jpt.telegram.bot;

import com.github.f4b6a3.uuid.UuidCreator;
import com.serezka.jpt.api.GPTApi;
import com.serezka.jpt.database.service.authorization.InviteCodeService;
import com.serezka.jpt.database.service.authorization.UserService;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.manager.MenuManager;
import com.serezka.jpt.telegram.sessions.manager.StepManager;
import com.serezka.jpt.telegram.sessions.types.Session;
import com.serezka.jpt.telegram.utils.Send;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Log4j2
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class THandler {
    GPTApi gptApi;

    public static final String template = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "  <head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <meta http-equiv=\"X-UA-Compatible\" content=\"ie=edge\">\n" +
            "    <title>My Website</title>\n" +
            "    <link rel=\"stylesheet\" href=\"./style.css\">\n" +
            "    <link rel=\"icon\" href=\"./favicon.ico\" type=\"image/x-icon\">\n" +
            "  </head>\n" +
            "  <body>\n" +
            "    <main>\n" +
            "        <h1>Answer:</h1>  \n" +
            "        %s" +
            "    </main>\n" +
            "\t<script src=\"index.js\"></script>\n" +
            "  </body>\n" +
            "</html>\n";

    // handler per-init settings
    @Getter
    List<Command<? extends Session>> commands = new ArrayList<>();

    // database services
    UserService userService;
    InviteCodeService inviteCodeService;

    // session services
    MenuManager menuManager = MenuManager.getInstance();
    StepManager stepManager = StepManager.getInstance();

    public void addCommand(Command<? extends Session> command) {
        commands.add(command);
    }

    public void process(TBot bot, TUpdate update) {
        // -> validate query
        if (!TSettings.availableQueryTypes.contains(update.getQueryType())) {
            bot.sendMessage(update.getChatId(), "Unknown query type");
            return;
        }

        // -> collect data
        long chatId = update.getChatId();
        String username = update.getUsername();
        String query = update.getText();
        TUpdate.QueryType queryType = update.getQueryType();

        log.info(String.format("New Message: chatId[%s] username[%s] message[%s] | QType: %s", chatId, username, query, queryType.toString()));

        try {
            int msgId = bot.sendMessage(update.getChatId(), "Обработка..").getMessageId();
            String answer= gptApi.query(Collections.singletonList("user: " +query), 0.7);
            log.info("Query: {} | Answer: {}", query, answer);

            if (answer.length() < 4096) {
                bot.execute(Send.message(chatId, answer.replaceAll("<br/>", "\n"), update.getMessageId()));
                bot.deleteMessage(chatId, msgId);
                return;
            }

            String answerHTML = String.format(template,answer);

            File tempFile = Files.createTempFile("answer", ".html").toFile();
            Files.write(Paths.get(tempFile.getPath()), Arrays.stream(answerHTML.split("\n")).toList(), StandardCharsets.UTF_8);

            InputFile inputFile = new InputFile();
            inputFile.setMedia(tempFile);

            bot.execute(Send.document(update.getChatId(), inputFile, update.getMessageId()));
            bot.deleteMessage(chatId, msgId);
            tempFile.delete();
        } catch (Exception ex) {log.warn(ex.getMessage());}

//        if (stepManager.containsSession(chatId) && queryType != TUpdate.QueryType.INLINE_QUERY) {
//            stepManager.getSession(chatId).next(bot, update);
//            return;
//        }
//
//        if (menuManager.containsSession(chatId) && text.split("\\" + Keyboard.Delimiter.SERVICE)[1].matches("\\d+")) {
//            menuManager.getSession(chatId, Long.parseLong(text.split("\\" + Keyboard.Delimiter.SERVICE)[1])).ifPresent(menuSession -> menuSession.next(bot, update));
//            return;
//        }
//
//        Optional<Command<? extends Session>> optionalSelected = commands.stream().filter(command -> command.getNames().contains(text)).findFirst();
//        if (optionalSelected.isEmpty()) {
//            bot.sendMessage(chatId, getHelp(user.getRole().getAdminLvl()));
//            return;
//        }
//
//        // get selected command
//        Command<? extends Session> selected = optionalSelected.get();
//        Session session = selected.createSession();
//
//        // add to session manager
//        if (session instanceof MenuSession) menuManager.addSession((MenuSession) session, chatId);
//        if (session instanceof StepSession) stepManager.addSession((StepSession) session, chatId);
//
//        // run session
//        session.next(bot, update);
    }

//    public String getHelp(int adminLvl) {
//        StringBuilder help = new StringBuilder("Кажется, вы ошиблись в команде. Список допустимых команд:\n");
//        help.append(commands.stream()
//                .filter(command -> command.getAdminLvl() <= adminLvl)
//                .map(command -> String.format(" - <b>%s</b> - %s%n", command.getNames(), command.getHelp()))
//                .collect(Collectors.joining()));
//        return help.toString();
//    }
}