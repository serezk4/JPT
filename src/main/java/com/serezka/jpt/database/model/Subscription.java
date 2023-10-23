package com.serezka.jpt.database.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "subsriptions")
@NoArgsConstructor
@Getter @Setter @ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;
    @Column(name = "usages_count")
    long usagesCount;

    public Subscription(String name, long usagesCount) {
        this.name = name;
        this.usagesCount = usagesCount;
    }
}
