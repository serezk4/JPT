package com.serezka.jpt.telegram.sessions.types.step;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.utils.Keyboard;
import java.util.Arrays;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class Step {
    Function<TBot, TUpdate, Data> generator;

    @FunctionalInterface
    public interface Function<bot, update, data>  {
        public Data apply(TBot bot, TUpdate update);
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor @Getter
    public static class Data {
        String text;
        Button.Reply[][] buttons;
        boolean canGoNext = true;

        public Data(String text) {
            this.text = text;
            this.buttons = null;
        }

        // todo make more good algorithm
        public ReplyKeyboardMarkup transferButtons() {
            if (buttons == null || buttons.length == 0) return null;

            String[][] transferred = new String[buttons.length][];
            for (int row = 0; row < buttons.length; row++)
                transferred[row] = Arrays.stream(buttons[row]).map(Button.Reply::getText).toList().toArray(new String[0]);
            return Keyboard.Reply.getCustomKeyboard(transferred);
        }
    }

    public static class Button {
        private Button() {}

        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        @AllArgsConstructor
        @Deprecated // todo in future
        public static class Inline extends Button {
            String text;
            String callback;
        }

        @RequiredArgsConstructor
        @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
        @Getter
        public static class Reply extends Button {
            String text;
        }
    }
}
