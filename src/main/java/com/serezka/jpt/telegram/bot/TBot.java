package com.serezka.jpt.telegram.bot;

import com.serezka.jpt.telegram.utils.Keyboard;
import com.serezka.jpt.telegram.utils.Send;
import com.serezka.jpt.telegram.utils.messages.SendV2;
import jdk.jfr.Experimental;
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
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;

@Component
@PropertySource("classpath:telegram.properties")
@Log4j2
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TBot extends TelegramLongPollingBot {
    String botUsername, botToken;

    @NonFinal
    @Setter
    THandler tHandler;

    public TBot(@Value("${telegram.bot.username}") String botUsername,
                @Value("${telegram.bot.token}") String botToken) {
        super(botToken);

        this.botUsername = botUsername;
        this.botToken = botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        new Thread(() -> tHandler.process(this, new TUpdate(update))).start();
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
    public Message sendMessage(long chatId, String text, Send.Parse parseMode) {
        return execute(SendV2.Message.build()
                .chatId(chatId).text(text)
                .parseMode(parseMode)
                .build().get());
    }

    public Message sendMessage(long chatId, String text) {
        return execute(SendV2.Message.build()
                .chatId(chatId).text(text)
                .build().get());
    }

    public Message sendMessage(long chatId, String text, ReplyKeyboard replyKeyboard) {
        return execute(SendV2.Message.build()
                .chatId(chatId).text(text)
                .replyKeyboard(replyKeyboard)
                .build().get());
    }

    public void deleteMessage(long chatId, int messageId) {
        execute(Send.delete(chatId, messageId));
    }

    public Message sendSticker(long chatId, String stickerId) {
        try {
            return execute(Send.sticker(chatId, stickerId));
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
