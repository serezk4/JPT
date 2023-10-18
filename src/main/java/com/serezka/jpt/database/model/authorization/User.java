package com.serezka.jpt.database.model.authorization;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // -> basic user data
    @Column(name = "chat_id", unique = true)
    Long chatId;
    String username;
    // <

    // -> bot settings for user
    Role role = Role.DEFAULT;

    // -> gpt settings for user
    Float temperature = 0.5F;
    Long chat = 0L;

    @AllArgsConstructor
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public enum Role {
        DEFAULT(0), ADMIN1(1);

        int adminLvl;
    }

    @AllArgsConstructor
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public enum BotMode {
        ONLY_GPT("только чат"), COMMANDS("команды");

        String name;
    }

    public User(Long chatId, String username) {
        this.chatId = chatId;
        this.username = username;
    }
}
