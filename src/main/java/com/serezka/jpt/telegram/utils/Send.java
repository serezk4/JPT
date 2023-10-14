package com.serezka.jpt.telegram.utils;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public class Send {
    public static DeleteMessage delete(Long chatId, int messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setMessageId(messageId);
        deleteMessage.setChatId(String.valueOf(chatId));

        return deleteMessage;
    }

    public static SendDocument document(Long chatId, InputFile file, int replyTo) {
        SendDocument sendDocument = document(chatId,file);
        sendDocument.setReplyToMessageId(replyTo);

        return sendDocument;
    }

    public static SendDocument document(Long chatId, InputFile file) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(String.valueOf(chatId));
        sendDocument.setDocument(file);
        sendDocument.setDisableContentTypeDetection(true);

        return sendDocument;
    }

    public static SendPhoto photo(Long chatId, InputFile file) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(String.valueOf(chatId));
        sendPhoto.setPhoto(file);

        return sendPhoto;
    }

    public static EditMessageText editWithoutParseMode(Long chatId, int messageId, String text) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(chatId));
        editMessageText.setMessageId(messageId);
        editMessageText.setText(text);
        editMessageText.setDisableWebPagePreview(true);

        return editMessageText;
    }

    public static EditMessageText edit(Long chatId, int messageId) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(String.valueOf(chatId));
        editMessageText.setMessageId(messageId);
        editMessageText.setParseMode(ParseMode.HTML);
        editMessageText.setDisableWebPagePreview(true);

        return editMessageText;
    }

    public static EditMessageText edit(Long chatId, int messageId, String newText) {
        EditMessageText editMessageText = edit(chatId,messageId);
        editMessageText.setText(newText);

        return editMessageText;
    }

    public static EditMessageText edit(Long chatId, int messageId, String newText, InlineKeyboardMarkup inlineKeyboard) {
        EditMessageText editMessageText = edit(chatId,messageId,newText);
        editMessageText.setReplyMarkup(inlineKeyboard);
        editMessageText.setDisableWebPagePreview(true);

        return editMessageText;
    }

    public static SendMessage messageWithoutParseMode(Long chatId, String text) {
        SendMessage sm = new SendMessage();
        sm.setChatId(String.valueOf(chatId));
        sm.setText(text);
        sm.setReplyMarkup(Keyboard.Reply.getDefault());
        sm.setDisableWebPagePreview(true);

        return sm;
    }

    public static SendMessage message(Long chatId, String text, String parseMode, int replyTo) {
        SendMessage sm = new SendMessage();
        sm.setChatId(String.valueOf(chatId));
        sm.setText(text);
        sm.setReplyToMessageId(replyTo);
        sm.setParseMode(parseMode);
        sm.setReplyMarkup(Keyboard.Reply.getDefault());
        sm.setDisableWebPagePreview(true);

        return sm;
    }

    public static SendMessage message(Long chatId, String text) {
        SendMessage sm = new SendMessage();
        sm.setChatId(String.valueOf(chatId));
        sm.setText(text);
        sm.setParseMode(ParseMode.HTML);
        sm.setReplyMarkup(Keyboard.Reply.getDefault());
        sm.setDisableWebPagePreview(true);

        return sm;
    }

    public static SendMessage message(Long chatId, String text, int replyTo) {
        SendMessage sm = message(chatId,text);
        sm.setReplyToMessageId(replyTo);

        return sm;
    }

    public static SendMessage message(Long chatId, String text, ReplyKeyboard replyKeyboard) {
        SendMessage sm = message(chatId,text);
        sm.setReplyMarkup(replyKeyboard);

        return sm;
    }

    public static SendSticker sticker(Long chatId, String fileId) {
        SendSticker sendSticker = new SendSticker();
        sendSticker.setSticker(new InputFile(fileId));
        sendSticker.setChatId(String.valueOf(chatId));

        return sendSticker;
    }
}
