package com.serezka.jpt.api;

import com.serezka.jpt.database.model.User;
import com.serezka.jpt.database.model.Query;
import com.serezka.jpt.database.service.UserService;
import com.serezka.jpt.database.service.QueryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Log4j2
public class GPTUtil {
    GPTApi gptApi;
    UserService userService;
    QueryService queryService;

    public enum Formatting {
        TEXT;//todo HTML,..
    }

    public String completeQuery(long chatId, String query, Formatting formatting) throws IOException {
        Optional<User> optionalUser = userService.findByChatId(chatId);
        if (optionalUser.isEmpty()) {
            log.warn("can't find optionalUser! {}", chatId);
            return "error #23 - [user didn't found]";
        }

        User user = optionalUser.get();

        StringTokenizer stringTokenizer = new StringTokenizer(query, " ");
        StringBuilder limitQuery = new StringBuilder();
        int usedTokens = 0;
        while (++usedTokens < 2048 && stringTokenizer.hasMoreTokens())
            limitQuery.append(stringTokenizer.nextToken()).append(" ");

        List<GPTApi.Query.Message> messages = new ArrayList<>();
        queryService.findAllByUserIdAndChat(user.getId(), user.getChat())
                .forEach(u -> {
                    messages.add(new GPTApi.Query.Message("user", u.getQuery()));
                    messages.add(new GPTApi.Query.Message("assistant", u.getAnswer()));
                });

        messages.add(new GPTApi.Query.Message("user", limitQuery.toString()));

        String answer = gptApi.query(messages, user.getTemperature())
                .replaceAll("<br/>", "\n")
                .replaceAll("assistant", "")
                .replaceAll("<\\|im_sep\\|>", "");

        if (limitQuery.length() < 5000 && answer.length() < 5000)
            queryService.save(new Query(user.getId(), user.getChat(), query, answer));

        return switch (formatting) {
            case TEXT -> answer; //todo in future
//            case HTML -> answer;
            case null -> answer;
        };
    }
}
