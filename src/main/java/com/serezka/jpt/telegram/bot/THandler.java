package com.serezka.jpt.telegram.bot;

import com.serezka.jpt.api.GPTUtil;
import com.serezka.jpt.database.model.authorization.User;
import com.serezka.jpt.database.service.authorization.InviteService;
import com.serezka.jpt.database.service.authorization.UserService;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.manager.MenuManager;
import com.serezka.jpt.telegram.sessions.manager.StepManager;
import com.serezka.jpt.telegram.sessions.types.Session;
import com.serezka.jpt.telegram.sessions.types.menu.MenuSession;
import com.serezka.jpt.telegram.sessions.types.step.StepSession;
import com.serezka.jpt.telegram.utils.AntiSpam;
import com.serezka.jpt.telegram.utils.Keyboard;
import com.serezka.jpt.telegram.utils.Read;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
    InviteService inviteService;

    // anti-spam services
    AntiSpam antiSpam;

    // gpt services
    GPTUtil gptUtil;

    // session services
    MenuManager menuManager = MenuManager.getInstance();
    StepManager stepManager = StepManager.getInstance();

    // cache
    Set<Long> authorized = Collections.newSetFromMap(new WeakHashMap<>());

    public void addCommand(Command<? extends Session> command) {
        commands.add(command);
    }

    public void process(TBot bot, TUpdate update) throws Exception {
        // -> validate query
        if (!TSettings.availableQueryTypes.contains(update.getQueryType())) {
            bot.execute(SendMessage.builder()
                    .chatId(update.getChatId()).text("Unknown query type")
                    .build());
            return;
        }

        final long chatId = update.getChatId();
        final String username = update.getUsername();
        final String text = new String(update.getText().getBytes(), StandardCharsets.UTF_8);
        final TUpdate.QueryType queryType = update.getQueryType();

        log.info(String.format("New Message: chatId[%s] username[%s] message[%s] | QType: %s", chatId, username, text, queryType.toString()));

        // check auth
        if (!authorized.contains(chatId) && !userService.existsByUsernameOrChatId(username, chatId) && !inviteService.existsByCode(text)) {
            bot.execute(SendMessage.builder()
                    .chatId(chatId).text("*Вы еще не авторизовались в боте.*\n_Введите токен, который вы получили:_")
                    .parseMode(ParseMode.MARKDOWN)
                    .build());

            return;
        }

        if (!authorized.contains(chatId) && !userService.existsByUsernameOrChatId(username, chatId)) {
            // if user entered token we will message him about it and add new row in database

            bot.execute(DeleteMessage.builder()
                    .chatId(chatId).messageId(update.getMessageId())
                    .build());

            bot.execute(SendMessage.builder()
                    .chatId(chatId).text("✅ *Вы успешно авторизовались!*")
                    .parseMode(ParseMode.MARKDOWN)
                    .build());

            bot.execute(SendMessage.builder()
                    .chatId(chatId).text("""
                            админ данного паблика *@serezkk*.
                                                        
                            ℹ️ Работает на модели *gpt-4*.
                            ℹ️ *Поддерживает* _(практически)_ *все файлы для запросов*.
                                   (.txt, .docx, .xml, .py, .cpp, ...)
                            ℹ️ *Работает* `24/7` _(иногда выключается для обновления)_.
                                                        
                            *Нажимайте на кнопку* `\uD83D\uDDD1️ Очистить историю` *почаще*.
                            """)
                    .parseMode(ParseMode.MARKDOWN).build());

            // save user to database
            userService.save(new User(chatId, username));

            return;
        }

        // get optional user
        Optional<User> optionalUser = userService.findByChatId(chatId);
        if (optionalUser.isEmpty()) { // check if returned user is present
            log.warn("User exception (can't find or create) | {} : {}", username, chatId);
            bot.execute(SendMessage.builder()
                    .chatId(chatId).text("*Проблемы с сервисами БД*\nНапишите *@serezkk* для устранения проблемы.")
                    .parseMode(ParseMode.MARKDOWN).build());
            return;
        }

        // add user to authorized list
        authorized.add(chatId);

        // get user
        User user = optionalUser.get();

        // check for step & menu manager
        if (stepManager.containsSession(chatId) && queryType != TUpdate.QueryType.INLINE_QUERY) {
            stepManager.getSession(chatId).next(bot, update);
            return;
        }

        if (menuManager.containsSession(chatId) && update.getSelf().hasCallbackQuery() && text.split("\\" + Keyboard.Delimiter.SERVICE)[1].matches("\\d+")) {
            menuManager.getSession(chatId, Long.parseLong(text.split("\\" + Keyboard.Delimiter.SERVICE)[1])).ifPresent(menuSession -> menuSession.next(bot, update));
            return;
        }

        Optional<Command<? extends Session>> optionalSelectedCommand = commands.stream()
                .filter(command -> command.getNames().contains(text))
                .findFirst();

        if (optionalSelectedCommand.isEmpty()) {
            // anti-spam system
            if (antiSpam.isSpam(user) && !update.getSelf().hasCallbackQuery()) {
                bot.execute(SendMessage.builder()
                        .chatId(chatId).text("\uD83D\uDE21 *Не спамь!*")
                        .parseMode(ParseMode.MARKDOWN)
                        .build());
                return;
            }

            // todo maybe remove list and replace with string
            List<String> error = new ArrayList<>();

            String fileData = null;
            if (update.getSelf().hasMessage() && update.getSelf().getMessage().hasDocument()) {
                Document document = update.getSelf().getMessage().getDocument();

                String fileUrl = bot.execute(new GetFile(document.getFileId())).getFileUrl(bot.getBotToken());
                InputStream fileIs = new URI(fileUrl).toURL().openStream();

                fileData = "\n[document]\n" + Read.getData(fileIs, fileUrl);
            }

            int prepareMessageId = bot.execute(SendMessage.builder()
                    .chatId(chatId).text("⌛ _Генерирую ответ..._")
                    .parseMode(ParseMode.MARKDOWN).build()).getMessageId();

            String gptAnswer = gptUtil.completeQuery(chatId,
                    text + Optional.ofNullable(fileData).orElse(""),
                    GPTUtil.Formatting.TEXT);

            if (text.length() > 5000)
                error.add("*Запрос слишком длинный и не будет сохранен в историю*");

            if (gptAnswer.contains("자세한 내용이 필요합니다")) {
                bot.execute(SendMessage.builder()
                        .text("""
                                *Возможные проблемы с ответом:*
                                                                
                                1️⃣ <b>Данный тип файла* не поддерживается!
                                2️⃣ Очистите *историю* - /clh
                                                                
                                `Если проблема останется - напишите` *@serezkk*
                                """)
                        .chatId(chatId).parseMode(ParseMode.MARKDOWN)
                        .build());
            }

            boolean isNull = bot.execute(SendMessage.builder()
                    .chatId(chatId).text(error.stream().map(s -> "⁉️ " + s + "\n\n").collect(Collectors.joining()) + "\uD83D\uDCAC " + gptAnswer)
                    .replyToMessageId(update.getMessageId())
                    .parseMode(ParseMode.MARKDOWN)
                    .build()) == null;

            if (isNull) {
                bot.execute(SendDocument.builder()
                        .chatId(chatId)
                        .document(new InputFile(new ByteArrayInputStream(("т.к. Telegram не может отобразить данный ответ, он в файле:\n\n" + gptAnswer)
                                .getBytes(StandardCharsets.UTF_8)), "answer.txt"))
                        .replyToMessageId(update.getMessageId())
                        .caption("\uD83D\uDCC1 <b>Из-за ограничений телеграма ответ в файле.</b>").parseMode(ParseMode.HTML)
                        .replyMarkup(Keyboard.Reply.DEFAULT)
                        .build());
            }

            bot.execute(DeleteMessage.builder()
                    .chatId(chatId).messageId(prepareMessageId)
                    .build());
            return;
        }

        // get selected command
        Command<? extends Session> selectedCommand = optionalSelectedCommand.get();
        Session session = selectedCommand.createSession();

        // add to session manager
        if (session instanceof MenuSession) menuManager.addSession((MenuSession) session, chatId);
        if (session instanceof StepSession) stepManager.addSession((StepSession) session, chatId);

        // run session
        session.next(bot, update);
    }

    @Deprecated // for this bot
    public String getHelp(int adminLvl) {
        StringBuilder help = new StringBuilder("Кажется, вы ошиблись в команде. Список допустимых команд:\n");
        help.append(commands.stream()
                .filter(command -> command.getAdminLvl() <= adminLvl)
                .map(command -> String.format(" - <b>%s</b> - %s%n", command.getNames(), command.getHelp()))
                .collect(Collectors.joining()));
        return help.toString();
    }
}