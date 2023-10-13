package com.serezka.jpt.database.model.gpt;

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

    Long userId;
    LocalDate date;
    String query;
    String answer;

    public Query(Long userId, LocalDate date, String query, String answer) {
        this.userId = userId;
        this.date = date;
        this.query = query;
        this.answer = answer;
    }
}
