package com.serezka.jpt.telegram.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;

import java.util.*;
import java.util.stream.IntStream;

@Log4j2
public class Keyboard {
    public static class Delimiter {
        public static final String SERVICE = "$";
        public static final String DATA = "*";
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @Getter
    @AllArgsConstructor
    public enum Actions {
        CLOSE("\uD83E\uDD0F Закрыть", "exit"), BACK("назад", "back");

        String name;
        String callback;
    }

    public static class Reply {
        public static final ReplyKeyboardMarkup DEFAULT = getDefault();

        public static ReplyKeyboardMarkup getDefault() {
            return getCustomKeyboard(new String[][]{
                    {"ℹ️ О боте", "◻️ Помогите!"},
                    {"\uD83C\uDD94 Профиль","🗑️ Очистить историю"}
            });

        }

        public static ReplyKeyboardMarkup getCustomKeyboard(List<List<String>> buttonsText) {
            return getCustomKeyboard(buttonsText, false);
        }

        public static ReplyKeyboardMarkup getCustomKeyboard(List<List<String>> buttonsText, boolean addButtons) {
            if (addButtons) buttonsText.add(List.of(Actions.BACK.getName(), Actions.CLOSE.getName()));
            ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup(buttonsText.stream()
                    .map(row -> new KeyboardRow(
                            row.stream()
                                    .filter(Objects::nonNull)
                                    .map(Reply::getButton).toList()))
                    .toList());

            replyKeyboard.setResizeKeyboard(true);

            return replyKeyboard;
        }

        public static ReplyKeyboardMarkup getCustomKeyboard(String[][] buttonsText) {
            return getCustomKeyboard(Arrays.stream(buttonsText).map(Arrays::asList).toList());
        }

        public static ReplyKeyboardMarkup getResizableKeyboard(List<Button> buttons, int rowSize) {
            List<KeyboardRow> mainRow = new ArrayList<>();
            Queue<Button> buttonsQueue = new PriorityQueue<>(buttons);

            while (!buttonsQueue.isEmpty()) {
                mainRow.add(new KeyboardRow(
                        IntStream.range(0, Math.min(rowSize, buttonsQueue.size()))
                                .mapToObj(i -> buttonsQueue.poll())
                                .filter(Objects::nonNull)
                                .map(Reply::getButton)
                                .toList()
                ));
            }

            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(mainRow);
            replyKeyboardMarkup.setResizeKeyboard(true);

            return replyKeyboardMarkup;
        }

        private static KeyboardButton getButton(Button button) {
            return getButton(button.getText(), button.getWebAppInfo());
        }

        private static KeyboardButton getButton(String text) {
            return new KeyboardButton(text);
        }

        private static KeyboardButton getButton(String text, WebAppInfo webAppInfo) {
            KeyboardButton tempButton = new KeyboardButton();
            tempButton.setText(text);
            tempButton.setWebApp(webAppInfo);

            return tempButton;
        }

        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        @Getter
        public static class Button {
            WebAppInfo webAppInfo;
            String text;

            public Button(String text, WebAppInfo webAppInfo) {
                this.webAppInfo = webAppInfo;
                this.text = text;
            }

            public Button(String text) {
                this.text = text;
                this.webAppInfo = null;
            }
        }
    }


    public static class Inline {
        public static InlineKeyboardMarkup getStaticKeyboard(Button[][] buttonsData) {
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();

            Arrays.stream(buttonsData).forEach(row ->
                    rows.add(Arrays.stream(row).
                            filter(Objects::nonNull).
                            map(Inline::getButton).
                            toList()
                    )
            );

            return new InlineKeyboardMarkup(rows);
        }

        public static InlineKeyboardMarkup getResizableKeyboard(List<Button> buttonsData, int rowSize) {
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            Queue<Button> buttonsQueue = new PriorityQueue<>(buttonsData);

            while (!buttonsQueue.isEmpty())
                rows.add(
                        IntStream.range(0, Math.min(buttonsQueue.size(), rowSize))
                                .mapToObj(i -> buttonsQueue.poll())
                                .filter(Objects::nonNull)
                                .map(Inline::getButton)
                                .toList()
                );

            return new InlineKeyboardMarkup(rows);
        }

        private static InlineKeyboardButton getButton(String text, String callbackData, long id) {
            InlineKeyboardButton tempInlineButton = new InlineKeyboardButton(text);
            tempInlineButton.setCallbackData(id + Delimiter.SERVICE + callbackData);

            return tempInlineButton;
        }

        private static InlineKeyboardButton getButton(Button button) {
            return button.getWebAppInfo() != null ?
                    getButton(button.getText(), button.getWebAppInfo(), button.getId()) :
                    getButton(button.getText(), String.join(Delimiter.SERVICE, button.getData()), button.getId());
        }

        private static InlineKeyboardButton getButton(String text, WebAppInfo webAppInfo, long id) {
            InlineKeyboardButton tempInlineButton = new InlineKeyboardButton(text);
            tempInlineButton.setWebApp(webAppInfo);

            return tempInlineButton;
        }

        @Getter
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        public static class Button {
            String text;
            List<String> data;
            long id;
            WebAppInfo webAppInfo;

            public Button(String text, List<String> data, long id) {
                this.text = text;
                this.id = id;
                this.data = data;
                this.webAppInfo = null;
            }

            public Button(String text, WebAppInfo webAppInfo, long id) {
                this.text = text;
                this.id = id;
                this.webAppInfo = webAppInfo;
                this.data = null;
            }
        }
    }
}