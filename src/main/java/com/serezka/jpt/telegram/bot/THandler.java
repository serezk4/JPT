package com.serezka.jpt.telegram.bot;

import com.serezka.jpt.api.GPTUtil;
import com.serezka.jpt.database.model.authorization.User;
import com.serezka.jpt.database.service.authorization.InviteCodeService;
import com.serezka.jpt.database.service.authorization.UserService;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.manager.MenuManager;
import com.serezka.jpt.telegram.sessions.manager.StepManager;
import com.serezka.jpt.telegram.sessions.types.Session;
import com.serezka.jpt.telegram.sessions.types.menu.MenuSession;
import com.serezka.jpt.telegram.sessions.types.step.StepSession;
import com.serezka.jpt.telegram.utils.AntiSpam;
import com.serezka.jpt.telegram.utils.Keyboard;
import com.serezka.jpt.telegram.utils.Send;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Controller
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@PropertySource("classpath:telegram.properties")
public class THandler {
    // handler per-init settings
    @Getter
    List<Command<? extends Session>> commands = new ArrayList<>();

    // database services
    UserService userService;
    InviteCodeService inviteCodeService;

    // anti-spam services
    AntiSpam antiSpam;

    // gpt services
    GPTUtil gptUtil;

    // session services
    MenuManager menuManager = MenuManager.getInstance();
    StepManager stepManager = StepManager.getInstance();

    public void addCommand(Command<? extends Session> command) {
        commands.add(command);
    }
    // settings from proerties

    @SneakyThrows
    public void process(TBot bot, TUpdate update) {
        // -> validate query
        if (!TSettings.availableQueryTypes.contains(update.getQueryType())) {
            bot.sendMessage(update.getChatId(), "Unknown query type");
            return;
        }

        // -> collect data
        long chatId = update.getChatId();
        String username = update.getUsername();
        String text = new String(update.getText().getBytes(), StandardCharsets.UTF_8);
        TUpdate.QueryType queryType = update.getQueryType();

        log.info(String.format("New Message: chatId[%s] username[%s] message[%s] | QType: %s", chatId, username, text, queryType.toString()));

        // -> get user from database
        // TODO: 10/13/23 temp 
//        if (!userService.existsByUsernameOrChatId(username, chatId) && !inviteCodeService.existsByCode(text)) {
//            bot.sendMessage(chatId, "Access denied");
//            return;
//        }

        Optional<User> optionalUser = userService.existsByChatId(chatId) ? userService.findByChatId(chatId) : userService.save(new User(chatId, username));
        if (optionalUser.isEmpty()) {
            log.warn("User exception (can't find or create) | {} : {}", username, chatId);
            return;
        }
        User user = optionalUser.get();

        if (stepManager.containsSession(chatId) && queryType != TUpdate.QueryType.INLINE_QUERY) {
            stepManager.getSession(chatId).next(bot, update);
            return;
        }

        if (menuManager.containsSession(chatId) && update.getSelf().hasCallbackQuery() && text.split("\\" + Keyboard.Delimiter.SERVICE)[1].matches("\\d+")) {
            menuManager.getSession(chatId, Long.parseLong(text.split("\\" + Keyboard.Delimiter.SERVICE)[1])).ifPresent(menuSession -> menuSession.next(bot, update));
            return;
        }

        Optional<Command<? extends Session>> optionalSelected = commands.stream().filter(command -> command.getNames().contains(text)).findFirst();

        if (optionalSelected.isEmpty()) {
            // anti-spam system
            if (antiSpam.isSpam(user.getId(), 3) && !update.getSelf().hasCallbackQuery()) {
                bot.sendMessage(chatId, "\uD83D\uDFE5 Вы <b>слишком часто</b> отправляете запросы! Подождите.");
                return;
            }

            long start = System.currentTimeMillis();
            int prepareMessageId = bot.sendMessage(chatId, "<i>генерация... </i>").getMessageId();
            String gptAnswer = gptUtil.completeQuery(chatId, text, GPTUtil.Formatting.TEXT);

            boolean isNull = bot.execute(Send.message(chatId, String.format("*Ответ* _%ds_%n%s", (System.currentTimeMillis() - start) / 1000,gptAnswer), ParseMode.MARKDOWN, update.getMessageId())) == null;
            if (isNull) {
                bot.execute(Send.document(chatId, new InputFile(
                        new ByteArrayInputStream(("т.к. Telegram не может отобразить данный ответ, он в файле.\n\n" + gptAnswer)
                                .getBytes(StandardCharsets.UTF_8)), "answer.txt"), update.getMessageId())
                );
            }

            bot.deleteMessage(chatId, prepareMessageId);
            return;
        }

        // get selected command
        Command<? extends Session> selected = optionalSelected.get();
        Session session = selected.createSession();

        // add to session manager
        if (session instanceof MenuSession) menuManager.addSession((MenuSession) session, chatId);
        if (session instanceof StepSession) stepManager.addSession((StepSession) session, chatId);

        // run session
        session.next(bot, update);
    }

    public String getHelp(int adminLvl) {
        StringBuilder help = new StringBuilder("Кажется, вы ошиблись в команде. Список допустимых команд:\n");
        help.append(commands.stream()
                .filter(command -> command.getAdminLvl() <= adminLvl)
                .map(command -> String.format(" - <b>%s</b> - %s%n", command.getNames(), command.getHelp()))
                .collect(Collectors.joining()));
        return help.toString();
    }
}