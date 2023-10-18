package com.serezka.jpt.telegram.utils.messages;

import com.serezka.jpt.telegram.utils.Keyboard;
import jdk.jfr.Experimental;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Experimental
public class SendV2 {
    @Builder(builderMethodName = "build") @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Message {
        @NonNull Long chatId;
        @NonNull String text;

        int replyTo;
        com.serezka.jpt.telegram.utils.Send.Parse parseMode;
        @Builder.Default
        ReplyKeyboard replyKeyboard = Keyboard.Reply.DEFAULT;

        @Builder.Default
        boolean disableWebPagePreview = true;
        @Builder.Default
        boolean disableNotification = false;

        public SendMessage get() {
            SendMessage sendMessage = new SendMessage();

            sendMessage.setText(getText());
            sendMessage.setChatId(getChatId());

            if (getReplyTo() != 0) sendMessage.setReplyToMessageId(getReplyTo());
            if (getParseMode() != null) sendMessage.setParseMode(getParseMode().getName());
            sendMessage.setReplyMarkup(getReplyKeyboard());
            sendMessage.setDisableWebPagePreview(isDisableWebPagePreview());
            sendMessage.setDisableNotification(isDisableNotification());

            return sendMessage;
        }
    }
}
