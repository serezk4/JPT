package com.serezka.jpt.telegram.sessions.types.menu;

import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.sessions.manager.MenuManager;
import com.serezka.jpt.telegram.sessions.types.Session;
import com.serezka.jpt.telegram.utils.Keyboard;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Log4j2
public class MenuSession extends Session {
    // session data
    Page currentPage;
    Page.Button lastPressedButton = null;

    public MenuSession(Page root) {
        this.currentPage = root;
    }

    // store vault
    final Map<Long, Page.Button> buttons = new HashMap<>();
    boolean created = false;

    @Override
    public void next(TBot bot, TUpdate update) {
        // if menu didn't created
        if (!created) {
            create(bot, update);
            return;
        }

        deleteUsersMessages(bot, update);

        // generate answer
        String[] data = update.getText().split("\\" + Keyboard.Delimiter.SERVICE);
        long chatId = update.getChatId();

        if (Arrays.stream(data).limit(3).anyMatch(s -> !s.matches("\\d+"))) {
            log.warn("Menu session error: {}, {}", update.getText(), update.getChatId());
            return;
        }

        long[] init = Arrays.stream(data).limit(3).mapToLong(Long::parseLong).toArray();

        // close session
        if (data[3].equals(Keyboard.Actions.CLOSE.getCallback())) {
            destroy(bot, update);
            return;
        }

        Page.Button usedButton = buttons.getOrDefault(init[0], null);
        if (usedButton == null && !data[3].equals(Keyboard.Actions.BACK.getCallback())) {
            log.warn("Error with button: {}, {}", buttons.toString(), chatId);
            return;
        }

        // todo make back button
        if (usedButton == null) usedButton = lastPressedButton;
        else lastPressedButton = usedButton;

        Page nextPage = usedButton.getNextPage() == null ? currentPage : usedButton.getNextPage();

        // create page
        // todo ??? why here todo!!!
        Page.Data pageData = cacheButtons(nextPage.getGenerator().apply(bot, update, data[3].equals("e") ? null : data[3], currentPage));
        InlineKeyboardMarkup keyboard = Keyboard.Inline.getStaticKeyboard(pageData.transferButtons(getId()));

        // send answer
        if (getBotsMessagesIds().isEmpty())
            getBotsMessagesIds().add(bot.execute(
                    SendMessage.builder()
                            .chatId(update.getChatId()).text(pageData.getText())
                            .replyMarkup(keyboard).parseMode(ParseMode.HTML)
                            .build()).getMessageId());

        else bot.execute(EditMessageText.builder()
                .chatId(chatId).messageId(getBotsMessagesIds().peek())
                .text(pageData.getText()).parseMode(ParseMode.HTML)
                .replyMarkup(keyboard).build());

        currentPage = nextPage;
    }

    private void create(TBot bot, TUpdate update) {
        created = true;

        // delete user's message
        deleteUsersMessages(bot, update);

        // create page
        Page.Data pageData = cacheButtons(currentPage.getGenerator().apply(bot, update, null, currentPage));
        InlineKeyboardMarkup keyboard = Keyboard.Inline.getStaticKeyboard(pageData.transferButtons(getId()));

        // send answer
        getBotsMessagesIds().add(bot.execute(
                SendMessage.builder()
                        .chatId(update.getChatId()).text(pageData.getText())
                        .replyMarkup(keyboard).parseMode(ParseMode.HTML)
                        .build()
        ).getMessageId());
    }

    @Override
    public void destroy(TBot bot, TUpdate update) {
        getBotsMessagesIds().forEach(msgId -> bot.execute(DeleteMessage.builder()
                .chatId(update.getChatId()).messageId(msgId)
                .build()));

        getUsersMessagesIds().forEach(msgId -> bot.execute(DeleteMessage.builder()
                .chatId(update.getChatId()).messageId(msgId).build()));

        MenuManager.getInstance().destroySession(update.getChatId(), this);
    }

    private void deleteUsersMessages(TBot bot, TUpdate update) {
        // add message id
        if (update.getQueryType() == TUpdate.QueryType.MESSAGE)
            getUsersMessagesIds().add(update.getMessageId());

        // delete user's message
        if (!isSaveUsersMessages() && !getUsersMessagesIds().isEmpty()) {
            getUsersMessagesIds().forEach(messageId -> bot.execute(DeleteMessage.builder()
                    .chatId(update.getChatId()).messageId(messageId).build()));
            getUsersMessagesIds().clear();
        }
    }

    private Page.Data cacheButtons(Page.Data data) {
        buttons.clear();
        if (data.getButtons() == null) return data; // todo make back button

        Arrays.stream(data.getButtons()).forEach(row -> Arrays.stream(row).forEach(button -> buttons.put(button.getButtonId(), button)));
        return data;
    }
}
