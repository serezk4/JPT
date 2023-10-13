package com.serezka.jpt.database.model.gpt;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "queries")
@NoArgsConstructor
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Query {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id")
    Long userId;
    Long chat;
    LocalDate date;
    String query;
    boolean ok;

    public Query(Long userId, Long chat, String query, boolean ok) {
        this.userId = userId;
        this.chat = chat;
        this.date = LocalDate.now();
        this.query = query;
        this.ok = ok;
    }
}
