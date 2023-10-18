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
import com.serezka.jpt.telegram.utils.Read;
import com.serezka.jpt.telegram.utils.methods.v2.Send;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.net.URI;

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
        // TODO убрать ****-код
        String text = new String((update.getText() != null ? update.getText() : (update.getSelf().getMessage().getCaption() == null ? "" : update.getSelf().getMessage().getCaption())).getBytes(), StandardCharsets.UTF_8);
        TUpdate.QueryType queryType = update.getQueryType();

        log.info(String.format("New Message: chatId[%s] username[%s] message[%s] | QType: %s", chatId, username, text, queryType.toString()));

        // -> get user from database
        if (!userService.existsByUsernameOrChatId(username, chatId) && !inviteCodeService.existsByCode(text)) {
            bot.sendMessage(chatId, "Access denied");
            return;
        }

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

        String finalText = text;
        Optional<Command<? extends Session>> optionalSelected = commands.stream().filter(command -> command.getNames().contains(finalText)).findFirst();

        if (optionalSelected.isEmpty()) {
            // anti-spam system
            if (antiSpam.isSpam(user) && !update.getSelf().hasCallbackQuery()) {
                bot.sendMessage(Send.Message.builder()
                        .chatId(chatId).text("\uD83D\uDE21 <b>Не спамь!</b>")
                        .parseMode(Send.Parse.HTML)
                        .build());
                return;
            }

            List<String> error = new ArrayList<>();

            if (update.getSelf().hasMessage() && update.getSelf().getMessage().hasDocument()) {
                Document document = update.getSelf().getMessage().getDocument();

                String filePath = bot.execute(new GetFile(document.getFileId())).getFilePath();
                InputStream fileIs = new URI("https://api.telegram.org/file/bot" + bot.getBotToken() + "/" + filePath).toURL().openStream();

                String documentData = null;

                // .xls:
                if (filePath.endsWith(".xls"))
                    documentData = Read.excel(fileIs);

                // .word
                if (filePath.endsWith(".docx"))
                    documentData = Read.word(fileIs);

                // .txt
                if (!(filePath.endsWith(".xls") && filePath.endsWith(".docx")))
                    documentData = Read.file(fileIs);

                if (documentData != null)
                    text += "[document]\n" + documentData;
                else
                    error.add("Пока что поддерживаются только форматы *.xls*, *.docx*, *.txt*.");
            }

            int prepareMessageId = bot.sendMessage(Send.Message.builder()
                    .chatId(chatId).text("⌛ <i>Генерирую ответ...</i>")
                    .parseMode(Send.Parse.HTML).build()).getMessageId();
            String gptAnswer = gptUtil.completeQuery(chatId, text, GPTUtil.Formatting.TEXT);

            if (text.length() > 5000)
                error.add("*Запрос слишком длинный и не будет сохранен в историю*");

            if (gptAnswer.contains("자세한 내용이 필요합니다.")) {
                bot.sendMessage(Send.Message.builder()
                        .text("""
                                <b>Возможные проблемы с ответом:</b>
                                                                
                                ℹ️ <b>1.</b> <b>Данный тип файла</b> не поддерживается!
                                ℹ️ <b>2.</b> Очистите <b>историю</b> - /clh
                                                                
                                <code>Если проблема останется - напишите</code> <b>@serezkk</b>
                                """)
                        .chatId(chatId).parseMode(Send.Parse.HTML)
                        .build());
            }

            boolean isNull = bot.execute(com.serezka.jpt.telegram.utils.methods.v1.Send.message(chatId, error.stream().map(s -> "⁉️ " + s + "\n\n").collect(Collectors.joining()) +
                    "\uD83D\uDCAC " + gptAnswer, ParseMode.MARKDOWN, update.getMessageId())) == null;
            if (isNull) {
                bot.execute(com.serezka.jpt.telegram.utils.methods.v1.Send.document(chatId, new InputFile(
                        new ByteArrayInputStream(("т.к. Telegram не может отобразить данный ответ, он в файле:\n\n" + gptAnswer)
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