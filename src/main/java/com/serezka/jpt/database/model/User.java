package com.serezka.jpt.database.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter @Setter @ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    // basic user data
    @Column(name = "chat_id", unique = true)
    Long chatId;
    String username;

    // bot settings for user
    Role role = Role.DEFAULT;

    // gpt settings for user
    Float temperature = 0.5F;
    Long chat = 0L;

    // subscription
    @Column(name = "subscription_id")
    Long subscriptionId;

    @AllArgsConstructor
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public enum Role {
        DEFAULT(0), ADMIN1(1);

        int adminLvl;
    }

    public User(Long chatId, String username, Long subscriptionId) {
        this.chatId = chatId;
        this.username = username;
        this.subscriptionId=subscriptionId;
    }
}
