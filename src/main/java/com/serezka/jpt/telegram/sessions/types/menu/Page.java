package com.serezka.jpt.telegram.sessions.types.menu;

import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.utils.Keyboard;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.log4j.Log4j2;

import java.security.Key;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
@Setter
public class Page {
    // pages data
    Page root = null;
    Function<TBot, TUpdate, String, Page, Data> generator;

    @FunctionalInterface
    public interface Function<bot, update, callback, page, data> {
        public Data apply(TBot bot, TUpdate update, String callback, Page page);
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor
    @Getter
    // todo make resizable keyboard available
    public static class Data {
        String text;
        Button[][] buttons;

        public Keyboard.Inline.Button[][] transferButtons(long sessionId) {
            // todo!!
            if (getButtons() == null) return new Keyboard.Inline.Button[][]{{}};

            Keyboard.Inline.Button[][] transferred = new Keyboard.Inline.Button[getButtons().length/* + 1*/][];

            for (int row = 0; row < getButtons().length; row++) {
                transferred[row] = Arrays.stream(getButtons()[row])
                        .toList().stream()
                        .map(button -> button.transfer(sessionId))
                        .toList().toArray(new Keyboard.Inline.Button[0]);
            }

//            transferred[transferred.length - 1] = new Keyboard.Inline.Button[]{
////                    new Button(Keyboard.Actions.BACK.getName(), Keyboard.Actions.BACK.getCallback()).transfer(sessionId),
//                    new Button(Keyboard.Actions.CLOSE.getName(), Keyboard.Actions.CLOSE.getCallback()).transfer(sessionId)
//            };

            return transferred;
        }
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @Getter
    @Setter
    @Log4j2
    @ToString
    public static class Button {
        final String text;

        final Page nextPage;
        String callback;

        private static long buttonIdCounter = 0;
        final long buttonId = buttonIdCounter++;

        public void action(TBot bot, TUpdate update, Page page) {
        }

        public Keyboard.Inline.Button transfer(long sessionId) {
            return new Keyboard.Inline.Button(this.text, List.of(String.valueOf(sessionId), String.valueOf(buttonId), callback == null ? "e" : callback), this.buttonId);
        }

        public Button(String text, Page nextPage) {
            this.text = text;
            this.nextPage = nextPage;
            this.callback = null;
        }

        public Button(String text, String callback) {
            this.text = text;
            this.callback = callback;
            this.nextPage = null;
        }

        public Button(String text, String callback, Page nextPage) {
            this.text = text;
            this.callback = callback;
            this.nextPage = nextPage;
        }
    }
}
