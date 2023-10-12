package com.serezka.jpt.database.repository.authorization;

import com.serezka.jpt.database.model.authorization.InviteCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteCodeRepository extends JpaRepository<InviteCode, Long> {
    boolean existsByCode(String code);
    InviteCode findByCode(String code);
    boolean deleteById(long id);
}
