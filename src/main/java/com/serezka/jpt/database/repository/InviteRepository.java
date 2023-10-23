package com.serezka.jpt.database.repository;

import com.serezka.jpt.database.model.Invite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InviteRepository extends JpaRepository<Invite, Long> {
    boolean existsByCode(String code);
    Invite findByCode(String code);
    boolean deleteById(long id);
}
