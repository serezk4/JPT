package com.serezka.jpt.telegram.sessions.types.step;

import com.serezka.jpt.telegram.bot.TBot;
import com.serezka.jpt.telegram.bot.TUpdate;
import com.serezka.jpt.telegram.commands.Command;
import com.serezka.jpt.telegram.sessions.manager.StepManager;
import com.serezka.jpt.telegram.sessions.types.Session;
import com.serezka.jpt.telegram.utils.Keyboard;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;
import java.util.Stack;

@Log4j2
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class StepSession extends Session {
    public static final String EXIT_SESSION = "Отмена";

    List<Step> init;
    Stack<Step> steps;

    Command<StepSession> command;

    public StepSession(StepSession root) {
        this.init = root.getInit();

        this.steps = new Stack<>();
        this.steps.addAll(this.init);

        this.command = root.getCommand();
        this.setSaveUsersMessages(root.isSaveUsersMessages());
    }

    public StepSession(List<Step> steps, Command<StepSession> command) {
        this.init = steps;

        this.steps = new Stack<>();
        this.steps.addAll(init);

        this.command = command;
    }

    public StepSession(List<Step> steps, Command<StepSession> command, boolean saveUsersMessages) {
        this(steps, command);
        setSaveUsersMessages(saveUsersMessages);
    }

    @Override
    public void next(TBot bot, TUpdate update) {
        getUsersMessagesIds().add(update.getMessageId());
        getHistory().add(update.getText());

        if (!isSaveUsersMessages()) bot.execute(DeleteMessage.builder()
                .chatId(update.getChatId()).messageId(update.getMessageId())
                .build());

        String lastMessage = update.getText();
        if (lastMessage.equalsIgnoreCase(EXIT_SESSION)) {
            getBotsMessagesIds().add(bot.execute(SendMessage.builder()
                    .chatId(update.getChatId()).text("<b>Закрыто</b>")
                    .parseMode(ParseMode.HTML)
                    .build()).getMessageId());
            destroy(bot, update);
            return;
        }

        // check if
        if (steps.isEmpty()) {
            command.execute(bot, update, getHistory());
            destroy(bot, update);
            return;
        }

        Step.Data data = steps.peek().getGenerator().apply(bot, update);
        if (data.isCanGoNext()) steps.pop(); // if we can go next step - remove top element

        ReplyKeyboardMarkup replyKeyboard = data.transferButtons();
        String text = data.getText();

        if (replyKeyboard == null) getBotsMessagesIds().add(bot.execute(SendMessage.builder()
                .chatId(update.getChatId()).text(text)
                .parseMode(ParseMode.HTML).replyMarkup(Keyboard.Reply.DEFAULT)
                .build()).getMessageId());
        else getBotsMessagesIds().add(bot.execute(SendMessage.builder()
                .chatId(update.getChatId()).text(text)
                .parseMode(ParseMode.HTML).replyMarkup(replyKeyboard).build()).getMessageId());
    }

    @Override
    public void destroy(TBot bot, TUpdate update) {
        getBotsMessagesIds().forEach(msgId -> bot.execute(DeleteMessage.builder()
                .chatId(update.getChatId()).messageId(msgId)
                .build()));

        StepManager.getInstance().destroySession(update.getChatId());
    }
}
