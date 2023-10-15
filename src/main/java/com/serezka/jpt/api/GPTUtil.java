package com.serezka.jpt.api;

import com.serezka.jpt.database.model.authorization.User;
import com.serezka.jpt.database.model.gpt.Query;
import com.serezka.jpt.database.service.authorization.UserService;
import com.serezka.jpt.database.service.gpt.QueryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class GPTUtil {
    public static final String TEMPLATE = """
            <!DOCTYPE html>
            <html lang="en">
              <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <meta http-equiv="X-UA-Compatible" content="ie=edge">
                <title>My Website</title>
                <link rel="stylesheet" href="./style.css">
                <link rel="icon" href="./favicon.ico" type="image/x-icon">
              </head>
              <body>
                <main>
                    <h1>Answer:</h1> \s
                    <a>%s</a>
                </main>
              </body>
            </html>
            """;

    GPTApi gptApi;
    UserService userService;
    QueryService queryService;

    public enum Formatting {
        HTML, TEXT;
    }

    public String completeQuery(long chatId, String query, Formatting formatting) throws IOException {
        Optional<User> optionalUser = userService.findByChatId(chatId);
        if (optionalUser.isEmpty()) {
            log.warn("can't find optionalUser! {}", chatId);
            return "error #23 - [user didn't found]";
        }

        User user = optionalUser.get();


        List<GPTApi.Query.Message> messages = new ArrayList<>();
        queryService.findAllByUserIdAndChat(user.getId(), user.getChat())
                .forEach(u -> {
                    messages.add(new GPTApi.Query.Message("user", u.getQuery()));
                    messages.add(new GPTApi.Query.Message("assistant", u.getAnswer()));
                });

        messages.add(new GPTApi.Query.Message("user", query));

        String answer = gptApi.query(messages, user.getTemperature());

        log.info("Query: {} | Answer: {}", query, answer);
        queryService.save(new Query(user.getId(), user.getChat(), query, answer));

        return switch (formatting) {
            case TEXT -> answer.replaceAll("<br/>", "\n");
            case HTML -> answer;
            case null -> answer;
        };
    }
}
