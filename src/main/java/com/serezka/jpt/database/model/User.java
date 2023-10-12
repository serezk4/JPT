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

    @Column(name = "chat_id", unique = true)
    Long chatId;

    String username;

    Role role = Role.USER;

    public static enum Role {
        USER, ADMIN;
    }

    public User(Long chatId, String username) {
        this.chatId = chatId;
        this.username = username;
    }
}
