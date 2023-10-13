package com.serezka.jpt.telegram.commands.user;

import com.serezka.jpt.telegram.utils.Send;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

public class AskGpt {
    {
//        try {
//            int msgId = bot.sendMessage(update.getChatId(), "Обработка..").getMessageId();
//            String answer= gptApi.query(Collections.singletonList("user: " +query), 0.7);
//            log.info("Query: {} | Answer: {}", query, answer);
//
//            if (answer.length() < 4096) {
//                bot.execute(Send.message(chatId, answer.replaceAll("<br/>", "\n"), update.getMessageId()));
//                bot.deleteMessage(chatId, msgId);
//                return;
//            }
//
//            String answerHTML = String.format(template,answer);
//
//            File tempFile = Files.createTempFile("answer", ".html").toFile();
//            Files.write(Paths.get(tempFile.getPath()), Arrays.stream(answerHTML.split("\n")).toList(), StandardCharsets.UTF_8);
//
//            InputFile inputFile = new InputFile();
//            inputFile.setMedia(tempFile);
//
//            bot.execute(Send.document(update.getChatId(), inputFile, update.getMessageId()));
//            bot.deleteMessage(chatId, msgId);
//            tempFile.delete();
//        } catch (Exception ex) {log.warn(ex.getMessage());}
    }
}
