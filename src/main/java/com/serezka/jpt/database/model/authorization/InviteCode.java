package com.serezka.jpt.database.model.authorization;

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
public class InviteCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String code;
    Integer usageCount;

    public InviteCode(String code, int usageCount) {
        this.code = code;
        this.usageCount = usageCount;
    }
}
