package com.serezka.jpt.database.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "queries")
@NoArgsConstructor
@Getter @Setter @ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Query {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id")
    Long userId;
    Long chat;
    LocalDate date;

    @Column(length = 5000)
    String query;

    @Column(length = 5000)
    String answer;

    public Query(Long userId, Long chat, String query, String answer) {
        this.userId = userId;
        this.chat = chat;
        this.date = LocalDate.now();
        this.query = query;
        this.answer = answer;
    }
}
