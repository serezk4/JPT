package com.serezka.jpt.telegram.sessions.types.menu;

import com.serezka.telegrambots.telegram.bot.TBot;
import com.serezka.telegrambots.telegram.bot.TUpdate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;

@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class Page {
    // pages data
    Page root;
    List<Page> children = null;

    // current page data
    public abstract Data generate(TBot bot, TUpdate tUpdate);

    public static class Data {
        String text;
        ReplyKeyboard replyKeyboard;
    }
}
