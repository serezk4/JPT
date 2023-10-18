package com.serezka.jpt.telegram.bot;

import com.serezka.jpt.database.service.authorization.UserService;
import com.serezka.jpt.telegram.utils.Keyboard;
import com.serezka.jpt.telegram.utils.methods.v2.Send;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.concurrent.*;

@Component
@PropertySource("classpath:telegram.properties")
@Log4j2
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TBot extends TelegramLongPollingBot {
    String botUsername, botToken;

    @NonFinal @Setter
    THandler tHandler;

    ExecutorService executor;
    UserService userService;

    public TBot(@Value("${telegram.bot.username}") String botUsername,
                @Value("${telegram.bot.token}") String botToken,
                @Value("${telegram.bot.threads}") int threadCount, UserService userService) {
        super(botToken);

        this.botUsername = botUsername;
        this.botToken = botToken;

        executor = Executors.newFixedThreadPool(threadCount);
        this.userService = userService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        TUpdate tupdate = new TUpdate(update);

        // if executor is shutting down or terminated we can't process queries
        if (executor.isShutdown() || executor.isTerminated()) {
            sendMessage(Send.Message.builder()
                    .chatId(tupdate.getChatId()).text("<b>Бот в данный момент выключается, запросы временно не принимаются.</b>")
                    .build());
            return;
        }

        // send update to handler
        executor.submit(() -> tHandler.process(this, new TUpdate(update)));
    }

    /** Safety shutdown */
    public void shutdown(TUpdate update) throws InterruptedException {
        // hard check for developer
        if (!update.getUsername().equals("serezkk")) return;

        // info users about shutdown
        userService.findAll().forEach(user -> sendMessage(user.getChatId(), "ℹ️ <b>Бот выключается для обновления, отвечать не будет.</b>"));

        // send message to dev that bot is shutting down
        sendMessage(Send.Message.builder()
                .chatId(update.getChatId()).text("⁉️ ADMIN: <b>Бот будет остановлен через 15 секунд</b>")
                .build());

        // start shutting down with executor
        executor.shutdown();

        // await for termination
        if (!executor.awaitTermination(15, TimeUnit.SECONDS)) {
            sendMessage(Send.Message.builder()
                    .chatId(update.getChatId()).text("⁉️ ADMIN: <b>Некоторые запросы не были выполнены.</b>")
                    .build());

            log.info("Still waiting for executor...");
            System.exit(444);
        }

        // send success message
        sendMessage(Send.Message.builder()
                .chatId(update.getChatId()).text("ADMIN: ⁉️ <b>Бот успешно выключен!</b>")
                .build());

        log.info("Exit normally!");
        System.exit(0);
    }

    // send stuff

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) {
        try {
            log.info("Something sent to user...");

            if (SendMessage.class.equals(method.getClass())) {
                SendMessage parsed = (SendMessage) method;

                if (parsed.getReplyMarkup() == null)
                    parsed.setReplyMarkup(Keyboard.Reply.getDefault());

                log.info(String.format("Message Sent: to {%s} with text {'%s'}",
                        parsed.getChatId(), parsed.getText().replace("\n", " ")));

                return (T) super.execute(parsed);
            } else return super.execute(method);
        } catch (TelegramApiException e) {
            log.warn("Error method execution: {}", e.getMessage());
            return null;
        }
    }

    // ...

    // send methods
    public Message sendMessage(Send.Message message) {
        return execute(message.get());
    }

    public Message sendMessage(long chatId, String text) {
        return execute(Send.Message.builder()
                .chatId(chatId).text(text)
                .build().get());
    }

    public Message sendMessage(long chatId, String text, ReplyKeyboard replyKeyboard) {
        return execute(Send.Message.builder()
                .chatId(chatId).text(text)
                .replyKeyboard(replyKeyboard)
                .build().get());
    }

    public void deleteMessage(long chatId, int messageId) {
        execute(com.serezka.jpt.telegram.utils.methods.v1.Send.delete(chatId, messageId));
    }

    public Message sendSticker(long chatId, String stickerId) {
        try {
            return execute(com.serezka.jpt.telegram.utils.methods.v1.Send.sticker(chatId, stickerId));
        } catch (TelegramApiException e) {
            log.warn(e.getMessage());
            return null;
        }
    }

    // ...

    // utils
    public void deleteLastMessageFromUser(TUpdate update) {
        if (update.isUserMessage() &&            // check if message from user
                update.getQueryType() == TUpdate.QueryType.MESSAGE) {  // check if message is text
            deleteMessage(update.getChatId(), update.getMessageId());
        }
    }
}
