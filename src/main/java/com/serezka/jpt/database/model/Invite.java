package com.serezka.jpt.database.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "invite_codes")
@NoArgsConstructor
@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Invite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String code;
    Integer usageCount;

    public Invite(String code, int usageCount) {
        this.code = code;
        this.usageCount = usageCount;
    }
}
