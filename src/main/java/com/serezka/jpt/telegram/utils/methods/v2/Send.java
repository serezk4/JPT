package com.serezka.jpt.telegram.utils.methods.v2;

import com.serezka.jpt.telegram.utils.Keyboard;
import jdk.jfr.Experimental;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Experimental
public class Send {
    @AllArgsConstructor
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public enum Parse {
        HTML(ParseMode.HTML), MARKDOWN(ParseMode.MARKDOWN), MARKDOWNV2(ParseMode.MARKDOWNV2);

        String name;
    }

    @Builder @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Document{
        @NonNull Long chatId;
        @NonNull InputFile inputFile;

        String caption;

        int replyTo;
        @Builder.Default
        ReplyKeyboard replyKeyboard = Keyboard.Reply.DEFAULT;

        @Builder.Default
        boolean disableNotification = false;
        @Builder.Default
        boolean disableContentTypeDetection = false;

        public SendDocument get() {
            SendDocument sendDocument = new SendDocument();

            sendDocument.setDocument(inputFile);
            sendDocument.setChatId(getChatId());

            if (getReplyTo() != 0) sendDocument.setReplyToMessageId(getReplyTo());
            if (getCaption() != null) sendDocument.setCaption(caption);

            sendDocument.setReplyMarkup(getReplyKeyboard());
            sendDocument.setDisableNotification(isDisableNotification());
            sendDocument.setDisableContentTypeDetection(disableContentTypeDetection);

            return sendDocument;
        }
    }


    @Builder @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Sticker{
        @NonNull Long chatId;
        @NonNull String fileId;

        int replyTo;
        @Builder.Default
        ReplyKeyboard replyKeyboard = Keyboard.Reply.DEFAULT;

        @Builder.Default
        boolean disableNotification = false;

        public SendSticker get() {
            SendSticker sendSticker = new SendSticker();

            sendSticker.setSticker(new InputFile(fileId));
            sendSticker.setChatId(getChatId());

            if (getReplyTo() != 0) sendSticker.setReplyToMessageId(getReplyTo());
            sendSticker.setReplyMarkup(getReplyKeyboard());
            sendSticker.setDisableNotification(isDisableNotification());

            return sendSticker;
        }
    }

    @Builder @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Message {
        @NonNull Long chatId;
        @NonNull String text;

        int replyTo;

        @Builder.Default
        Parse parseMode = Parse.HTML;

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
