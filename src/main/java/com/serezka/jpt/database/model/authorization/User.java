package com.serezka.jpt.database.model.authorization;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter @Setter @ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    // user info

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "chat_id", unique = true)
    Long chatId;
    String username;
    Role role = Role.DEFAULT;

    // user data
    String name = "";
    Integer priority = 0;

    @AllArgsConstructor @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public enum Role {
        DEFAULT(0), ADMIN1(1);

        int adminLvl;
    }

    public User(Long chatId, String username) {
        this.chatId = chatId;
        this.username = username;
    }
}
